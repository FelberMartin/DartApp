package com.example.dartapp.database


import androidx.test.filters.SmallTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named


@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
@HiltAndroidTest
class LegDatabaseDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("in_memory_db")
    lateinit var legDatabase: LegDatabase
    private lateinit var legDao: LegDatabaseDao

    @Before
    fun setup() {
        hiltRule.inject()
        legDao = legDatabase.legDatabaseDao()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetLeg() {
        val leg = Leg()
        legDao.insert(leg)
        val returnedLeg = legDao.getLatestLeg()

        assertEquals(returnedLeg?.endTime, leg.endTime)
    }
}