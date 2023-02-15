package com.development_felber.dartapp.data.persistent.database.finished_leg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development_felber.dartapp.data.persistent.database.Converters
import com.development_felber.dartapp.data.persistent.database.TestLegData
import java.time.ZoneOffset

class FakeFinishedLegDao(fillWithTestData: Boolean = false) : FinishedLegDao {

    private val legsById: HashMap<Long, FinishedLeg> = HashMap()
    private val legLiveData = MutableLiveData<List<FinishedLeg>>(mutableListOf())

    init {
        if (fillWithTestData) {
            val testData = TestLegData.createExampleLegs()
            for (leg in testData) {
                this.insertBlocking(leg)
            }
        }
    }

    override suspend fun insert(leg: FinishedLeg) {
        insertBlocking(leg)
    }

    override suspend fun insert(legs: List<FinishedLeg>) {
        for (leg in legs) {
            insertBlocking(leg)
        }
    }

    private fun insertBlocking(leg: FinishedLeg) {
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

    override fun update(leg: FinishedLeg) {
        legsById[leg.id] = leg
        updateLiveData()
    }

    override suspend fun get(id: Long): FinishedLeg? {
        return legsById[id]
    }

    override fun clear() {
        legsById.clear()
        updateLiveData()
    }

    override fun getAllLegs(): LiveData<List<FinishedLeg>> {
        return legLiveData
    }

    override suspend fun getLatestLeg(): FinishedLeg? {
        return legsById.values.maxByOrNull { leg ->
            Converters.toLocalDateTime(leg.endTime).toEpochSecond(ZoneOffset.UTC)
        }
    }

}