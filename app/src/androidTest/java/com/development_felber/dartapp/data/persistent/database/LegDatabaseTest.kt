package com.development_felber.dartapp.database


import androidx.test.filters.SmallTest
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.data.persistent.database.AppDatabase
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLegDao
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named


@SmallTest
@HiltAndroidTest
class FinishedLegDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("in_memory_db")
    lateinit var appDatabase: AppDatabase
    private lateinit var finishedLegDao: FinishedLegDao

    @Before
    fun setup() {
        hiltRule.inject()
        finishedLegDao = appDatabase.getLegDao()
    }

    @Test
    @Throws(Exception::class)
    suspend fun insertAndGetLeg() {
        val leg = FinishedLeg()
        finishedLegDao.insert(leg)
        val returnedLeg = finishedLegDao.getLatestLeg()

        assertEquals(returnedLeg?.endTime, leg.endTime)
    }
}