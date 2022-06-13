package com.example.dartapp.database

import android.content.Context
import androidx.room.Room
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random

private const val daysBack = 120
private const val maxPerDay = 3
private const val TAG = "ExampleLegDatabase"

object ExampleLegDatabase {

    private lateinit var database: LegDatabase

    fun generate(context: Context): LegDatabase {
        if (this::database.isInitialized) {
            return database
        }

        database = Room.inMemoryDatabaseBuilder(context, LegDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        fillWithExampleData()

        return database
    }

    private fun fillWithExampleData() {
        for (daysBack in 0..daysBack) {
            if (Random.nextDouble(0.0, 1.0) < 0.6) {
                val legCount = Random.nextInt(maxPerDay)
                repeat(legCount) {
                    val leg = createRandomLeg(daysBack)
                    database.legDatabaseDao().insert(leg)
                }
            }
        }
    }

    private fun createRandomLeg(daysBack: Int): Leg {
        val now = LocalDateTime.now()
        val serves = createRandomServes()
        val doubleAttempts = createRandomDoubleAttempts()

        return Leg(
            gameMode = 1,
            endTime = Converters.fromLocalDateTime(now.minusDays(daysBack.toLong())),
            duration = Converters.fromDuration(Duration.ofSeconds(Random.nextLong(20) * 60)),
            dartCount = serves.size * 3,
            servesAvg = serves.average(),
            doubleAttempts = doubleAttempts.size,
            checkout = serves.last(),
            servesList = Converters.fromListOfInts(serves),
            doubleAttemptsList = Converters.fromListOfInts(doubleAttempts)
            )
    }

    private fun createRandomServes(): List<Int> {
        var pointsLeft = 501
        val serves = ArrayList<Int>()
        while (pointsLeft > 0) {
            var serve = Random.nextInt(180 + 1)
            if (pointsLeft - serve < 0) {
                serve = pointsLeft
            }
            serves.add(serve)
            pointsLeft -= serve
        }
        return serves
    }

    private fun createRandomDoubleAttempts(): List<Int> {
        val attempts = ArrayList<Int>()
        val count = 1 + Random.nextInt(5)
        repeat(count) {
            attempts.add(Random.nextInt(4))
        }

        return attempts
    }

}