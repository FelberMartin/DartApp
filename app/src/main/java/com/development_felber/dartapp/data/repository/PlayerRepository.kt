package com.development_felber.dartapp.data.repository

import com.development_felber.dartapp.data.persistent.database.player.Player
import com.development_felber.dartapp.data.persistent.database.player.PlayerDao
import com.development_felber.dartapp.data.persistent.keyvalue.IKeyValueStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlayerRepository(
    private val keyValueStorage: IKeyValueStorage,
    private val playerDao: PlayerDao,
) {

    fun getPlayersStream() = playerDao.observePlayers()

    suspend fun getPlayers() : List<Player> {
        return playerDao.getPlayers()
    }

    suspend fun getPlayer(name: String) = playerDao.getPlayer(name)

    suspend fun createNewPlayer(name: String) : Result<Player> {
        val player = Player(name)
        if (playerDao.getPlayer(name) != null) {
            return Result.failure(IllegalArgumentException("Player with name $name already exists"))
        }
        playerDao.insertPlayer(player)
        return Result.success(player)
    }

    suspend fun getLastPlayer1() : Player? = getLastPlayer(PLAYER_1_KEY)
    suspend fun getLastPlayer2() : Player? = getLastPlayer(PLAYER_2_KEY)

    private suspend fun getLastPlayer(key: String) : Player? {
        return keyValueStorage.getFlow(key).map { value: String? ->
            if (value == null) {
                return@map null
            }
            return@map Player(value)
        }.first()
    }

    suspend fun setLastPlayer1(player: Player) = setLastPlayer(PLAYER_1_KEY, player)
    suspend fun setLastPlayer2(player: Player) = setLastPlayer(PLAYER_2_KEY, player)

    private suspend fun setLastPlayer(key: String, player: Player) {
        keyValueStorage.put(key, player.name)
    }

    companion object {
        private const val PLAYER_1_KEY = "PLAYER_1"
        private const val PLAYER_2_KEY = "PLAYER_2"
    }
}