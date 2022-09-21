package com.example.dartapp.data.persistent.database

import java.time.ZoneOffset

class FakeLegDatabaseDao(fillWithTestData: Boolean = false) : LegDatabaseDao {

    private val legsById: HashMap<Long, Leg> = HashMap()

    init {
        if (fillWithTestData) {
            val testData = TestLegData.createExampleLegs()
            for (leg in testData) {
                this.insert(leg)
            }
        }
    }

    override fun insert(leg: Leg) {
        legsById[leg.id] = leg
    }

    override fun update(leg: Leg) {
        legsById[leg.id] = leg
    }

    override fun get(id: Long): Leg? {
        return legsById[id]
    }

    override fun clear() {
        legsById.clear()
    }

    override fun getAllLegs(): List<Leg> {
        return legsById.values.toList()
    }

    override fun getLatestLeg(): Leg? {
        return legsById.values.maxByOrNull { leg ->
            Converters.toLocalDateTime(leg.endTime).toEpochSecond(ZoneOffset.UTC)
        }!!
    }

}