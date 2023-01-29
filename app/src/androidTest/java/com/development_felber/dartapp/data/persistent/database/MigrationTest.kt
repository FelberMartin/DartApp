package com.development_felber.dartapp.data.persistent.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.development_felber.dartapp.data.PlayerOption
import com.development_felber.dartapp.util.Constants
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate6to7() {
        var db = helper.createDatabase(TEST_DB, 6)
        db.apply {
            // Id, endTime, durationSeconds, dartCount, average, doubleAttempts, checkout, servesList, doubleAttemptsList
            execSQL("INSERT INTO legs_table VALUES (1, 0, 60, 12, 42, 2, 24, \"[]\", \"[]\")")

            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB, 7, true, AppDatabase_AutoMigration_6_7_Impl())

        db.query("SELECT * FROM legs_table").apply {
            assertThat(moveToFirst()).isTrue()
            assertThat(getInt(getColumnIndex("player_option"))).isEqualTo(PlayerOption.Solo.ordinal)
            assertThat(getString(getColumnIndex("player_name"))).isEqualTo(Constants.SOLO_GAME_PLACEHOLDER_NAME)
        }
    }
}
