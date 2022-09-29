package com.example.dartapp.data.persistent.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.ZoneOffset

class FakeLegDatabaseDao(fillWithTestData: Boolean = false) : LegDatabaseDao {

    private val legsById: HashMap<Long, Leg> = HashMap()
    private val legLiveData = MutableLiveData<List<Leg>>(mutableListOf())

    init {
        if (fillWithTestData) {
            val testData = TestLegData.createExampleLegs()
            for (leg in testData) {
                this.insertBlocking(leg)
            }
        }
    }

    override suspend fun insert(leg: Leg) {
        insertBlocking(leg)
    }

    private fun insertBlocking(leg: Leg) {
        if (legsById.containsKey(leg.id)) {
            leg.id = legsById.keys.max() + 1
        }
        legsById[leg.id] = leg
        updateLiveData()
    }

    private fun updateLiveData() {
        val sortedLegs = legsById.values.sortedBy { leg -> leg.endTime }
        legLiveData.value = sortedLegs
    }

    override fun update(leg: Leg) {
        legsById[leg.id] = leg
        updateLiveData()
    }

    override suspend fun get(id: Long): Leg? {
        return legsById[id]
    }

    override fun clear() {
        legsById.clear()
        updateLiveData()
    }

    override fun getAllLegs(): LiveData<List<Leg>> {
        return legLiveData
    }

    override fun getLatestLeg(): Leg? {
        return legsById.values.maxByOrNull { leg ->
            Converters.toLocalDateTime(leg.endTime).toEpochSecond(ZoneOffset.UTC)
        }
    }

}