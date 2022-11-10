package com.development_felber.dartapp.data.persistent.database.leg

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.development_felber.dartapp.data.PlayerOption
import com.development_felber.dartapp.data.persistent.database.Converters
import com.development_felber.dartapp.util.Constants

@Entity(tableName = "legs_table")
data class Leg(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var playerOption: PlayerOption = PlayerOption.Solo,
    @ColumnInfo(name = "player_option")
    private var _playerOption: Int = playerOption.ordinal,

    @ColumnInfo(name = "player_name")
    var playerName: String = Constants.SOLO_GAME_PLACEHOLDER_NAME,

    @ColumnInfo(name = "end_time")
    var endTime: String = "",

    @ColumnInfo(name = "duration_seconds")
    var durationSeconds: Long = 20 * 60L,

    @ColumnInfo(name = "dart_count")
    var dartCount: Int = 0,

    @ColumnInfo(name = "serves_avg")
    var average: Double = 0.0,

    @ColumnInfo(name = "double_attempts")
    var doubleAttempts: Int = 0,

    @ColumnInfo(name = "checkout")
    var checkout: Int = 0,

    @ColumnInfo(name = "serve_list")
    var servesList: String = "",

    @ColumnInfo(name = "double_attempts_list")
    var doubleAttemptsList: String = "",
) {

    fun nineDartsAverage() : Double {
        val serves = Converters.toListOfInts(servesList)
        return serves.subList(0, 3).average()
    }
}



