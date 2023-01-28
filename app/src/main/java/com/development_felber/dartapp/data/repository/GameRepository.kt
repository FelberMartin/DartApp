package com.development_felber.dartapp.data.repository

import android.util.Log
import com.development_felber.dartapp.data.PlayerOption
import com.development_felber.dartapp.data.persistent.database.Converters
import com.development_felber.dartapp.data.persistent.database.dart_set.DartSet
import com.development_felber.dartapp.data.persistent.database.dart_set.DartSetDao
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLegDao
import com.development_felber.dartapp.data.persistent.database.multiplayer_game.MultiplayerGame
import com.development_felber.dartapp.data.persistent.database.multiplayer_game.MultiplayerGameDao
import com.development_felber.dartapp.game.GameSetup
import com.development_felber.dartapp.game.GameState
import com.development_felber.dartapp.game.GameStatus
import com.development_felber.dartapp.game.PlayerRole
import com.development_felber.dartapp.util.Constants
import java.time.LocalDateTime
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val finishedLegDao: FinishedLegDao,
    private val dartSetDao: DartSetDao,
    private val multiplayerGameDao: MultiplayerGameDao,
) {

    suspend fun saveGame(game: GameState) {
        assert(game.gameStatus == GameStatus.Finished) { "Only completed games can be saved!" }
        when (game.setup) {
            is GameSetup.Solo ->
                saveSoloGame(game)
            is GameSetup.Multiplayer ->
                saveMultiplayerGame(game)
        }
    }

    private suspend fun saveSoloGame(game: GameState) {
        val leg = game.playerGameStates.first().previousLegsPerSet.first().first()
        finishedLegDao.insert(leg.toFinishedLeg(
            playerOption = PlayerOption.Solo,
            playerName = Constants.SOLO_GAME_PLACEHOLDER_NAME,
        ))
        Log.i("GameRepository", "Saved Solo Game")
    }

    private suspend fun saveMultiplayerGame(game: GameState) {
        val sets = mutableListOf<DartSet>()
        val setCount = game.playerGameStates.sumOf { it.setsWonCount }
        for (setIndex in 0 until setCount) {
            val legsForThisSet = mutableListOf<FinishedLeg>()
            val wonLegsCountPerPlayer = mutableListOf<Int>()
            for (playerGameState in game.playerGameStates) {
                val playerName = game.setup.getPlayerName(playerGameState.playerRole)!!
                val legs = playerGameState.previousLegsPerSet[setIndex]
                wonLegsCountPerPlayer.add(legs.sumOf { if (it.isOver) 1 else 0.toInt() })
                val finishedLegsForThisPlayer = legs.map {
                    it.toFinishedLeg(
                        playerOption = PlayerOption.Multiplayer,
                        playerName = playerName,
                    )
                }.toList()
                finishedLegDao.insert(finishedLegsForThisPlayer)
                legsForThisSet.addAll(finishedLegsForThisPlayer)
            }
            val set = DartSet(
                legIds = legsForThisSet.map { it.id },
                legsWonPlayer1 = wonLegsCountPerPlayer[0],
                legsWonPlayer2 = wonLegsCountPerPlayer[1],
            )
            dartSetDao.insert(set)
            sets.add(set)
        }
        val multiplayerGame = MultiplayerGame(
            player1Name = game.setup.getPlayerName(PlayerRole.One)!!,
            player2Name = game.setup.getPlayerName(PlayerRole.Two)!!,
            setIds = sets.map { it.id },
            setsWonPlayer1 = sets.count { it.legsWonPlayer1 > it.legsWonPlayer2 },
            setsWonPlayer2 = sets.count { it.legsWonPlayer1 < it.legsWonPlayer2 },
            endTime = Converters.fromLocalDateTime(LocalDateTime.now())
        )
        multiplayerGameDao.insert(multiplayerGame)

        Log.i("GameRepository", "Saved Multiplayer Game between " +
                "${game.setup.getPlayerName(PlayerRole.One)} and ${game.setup.getPlayerName(PlayerRole.Two)}")
    }
}