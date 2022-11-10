package com.development_felber.dartapp.data.persistent.database.leg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development_felber.dartapp.data.persistent.database.Converters
import com.development_felber.dartapp.data.persistent.database.TestLegData
import java.time.ZoneOffset

class FakeLegDao(fillWithTestData: Boolean = false) : LegDao {

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

    override suspend fun getLatestLeg(): Leg? {
        return legsById.values.maxByOrNull { leg ->
            Converters.toLocalDateTime(leg.endTime).toEpochSecond(ZoneOffset.UTC)
        }
    }

}