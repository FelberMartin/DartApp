package com.development_felber.dartapp.data.persistent.database.multiplayer_game

import androidx.lifecycle.LiveData

class FakeMultiplayerGameDao : MultiplayerGameDao {

    private val multiplayerGamesById: HashMap<Long, MultiplayerGame> = HashMap()

    override suspend fun insert(multiplayerGame: MultiplayerGame) {
        if (multiplayerGamesById.containsKey(multiplayerGame.id)) {
            multiplayerGame.id = multiplayerGamesById.keys.max() + 1
        }
        multiplayerGamesById[multiplayerGame.id] = multiplayerGame
    }

    override suspend fun get(id: Long): MultiplayerGame? {
        return multiplayerGamesById[id]
    }

    override fun getAll(): List<MultiplayerGame> {
        return multiplayerGamesById.values.toList()
    }
}