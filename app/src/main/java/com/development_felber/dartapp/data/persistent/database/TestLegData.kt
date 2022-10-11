package com.development_felber.dartapp.data.persistent.database

import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random


object TestLegData {

//    private lateinit var database: LegDatabase
//
//    fun generate(context: Context): LegDatabase {
//        if (this::database.isInitialized) {
//            return database
//        }
//
//        database = Room.inMemoryDatabaseBuilder(context, LegDatabase::class.java)
//            .allowMainThreadQueries()
//            .build()
//
//        fillWithExampleData()
//
//        return database
//    }

    fun createExampleLegs(random: Random = Random, daysBack: Int = 120, maxPerDay: Int = 3) : List<Leg> {
        val legs = ArrayList<Leg>()
        for (currentDaysBack in 0..daysBack) {
            if (random.nextDouble(0.0, 1.0) < 0.6) {
                val legCount = random.nextInt(maxPerDay)
                repeat(legCount) {
                    val leg = createRandomLeg(random, currentDaysBack)
                    legs.add(leg)
                }
            }
        }
        return legs
    }

    fun createRandomLeg(random: Random = Random, daysBack: Int = 0): Leg {
        val now = LocalDateTime.now()
        val serves = createRandomServes(random)
        val doubleAttempts = createRandomDoubleAttempts(random)

        return Leg(
            id = random.nextLong(),
            endTime = Converters.fromLocalDateTime(now.minusDays(daysBack.toLong())),
            durationSeconds = Converters.fromDuration(Duration.ofSeconds(random.nextLong(20) * 60)),
            dartCount = serves.size * 3,
            average = serves.average(),
            doubleAttempts = doubleAttempts.size,
            checkout = serves.last(),
            servesList = Converters.fromListOfInts(serves),
            doubleAttemptsList = Converters.fromListOfInts(doubleAttempts)
            )
    }

    private fun createRandomServes(random: Random): List<Int> {
        var pointsLeft = 501
        val serves = ArrayList<Int>()
        while (pointsLeft > 0) {
            var serve = random.nextInt(180 + 1)
            if (pointsLeft - serve < 0) {
                serve = pointsLeft
            }
            serves.add(serve)
            pointsLeft -= serve
        }
        return serves
    }

    private fun createRandomDoubleAttempts(random: Random): List<Int> {
        val attempts = ArrayList<Int>()
        val count = 1 + random.nextInt(5)
        repeat(count) {
            attempts.add(random.nextInt(4))
        }

        return attempts
    }

}