package com.example.dartapp.database


import androidx.test.filters.SmallTest
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.data.persistent.database.LegDatabase
import com.example.dartapp.data.persistent.database.LegDatabaseDao
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
    suspend fun insertAndGetLeg() {
        val leg = Leg()
        legDao.insert(leg)
        val returnedLeg = legDao.getLatestLeg()

        assertEquals(returnedLeg?.endTime, leg.endTime)
    }
}