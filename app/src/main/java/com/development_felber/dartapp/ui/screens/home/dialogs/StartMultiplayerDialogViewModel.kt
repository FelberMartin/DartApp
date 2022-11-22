package com.development_felber.dartapp.ui.screens.home.dialogs

import androidx.lifecycle.viewModelScope
import com.development_felber.dartapp.data.persistent.database.player.Player
import com.development_felber.dartapp.data.repository.PlayerRepository
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.navigation.command.NavigationCommand
import com.development_felber.dartapp.ui.shared.NavigationViewModel
import com.development_felber.dartapp.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PlayerPosition {
    PLAYER_1,
    PLAYER_2
}

const val MAX_PLAYER_NAME_LENGTH = 12

@HiltViewModel
class StartMultiplayerDialogViewModel @Inject constructor(
    navigationManager: NavigationManager,
    private val playerRepository: PlayerRepository,
) : NavigationViewModel(navigationManager) {

    private val _player1 = MutableStateFlow<Player?>(null)
    val player1 = _player1.asStateFlow()

    private val _player2 = MutableStateFlow<Player?>(null)
    val player2 = _player2.asStateFlow()

    val players = playerRepository.getPlayersStream()
        .stateIn(viewModelScope, WhileUiSubscribed, emptyList())

    private val _legCount = MutableStateFlow(3)
    val legCount = _legCount.asStateFlow()

    private val _setCount = MutableStateFlow(1)
    val setCount = _setCount.asStateFlow()


    init {
        viewModelScope.launch {
            _player1.value = playerRepository.getLastPlayer1()
            _player2.value = playerRepository.getLastPlayer2()
        }
    }

    fun setPlayer(playerPosition: PlayerPosition, player: Player?) {
        when (playerPosition) {
            PlayerPosition.PLAYER_1 -> _player1.value = player
            PlayerPosition.PLAYER_2 -> _player2.value = player
        }
        viewModelScope.launch {
            when (playerPosition) {
                PlayerPosition.PLAYER_1 -> playerRepository.setLastPlayer1(player!!)
                PlayerPosition.PLAYER_2 -> playerRepository.setLastPlayer2(player!!)
            }
        }
    }

    fun setLegCount(count: Int) {
        _legCount.value = count
    }

    fun setSetCount(count: Int) {
        _setCount.value = count
    }

    fun onCreateNewPlayer(playerName: String, playerPosition: PlayerPosition) {
        viewModelScope.launch {
            val result = playerRepository.createNewPlayer(playerName)
            if (result.isSuccess) {
                when (playerPosition) {
                    PlayerPosition.PLAYER_1 -> _player1.value = result.getOrNull()
                    PlayerPosition.PLAYER_2 -> _player2.value = result.getOrNull()
                }
            }
        }
    }

    fun isNewPlayerNameValid(playerName: String) : Result<Unit> {
        if (playerName.isBlank()) {
            return Result.failure(IllegalArgumentException("Player name cannot be empty"))
        } else if (playerName.length > MAX_PLAYER_NAME_LENGTH) {
            return Result.failure(IllegalArgumentException("Player name cannot be " +
                    "longer than $MAX_PLAYER_NAME_LENGTH characters"))
        } else if (players.value.map{ it.name }.contains(playerName)) {
            return Result.failure(IllegalArgumentException("Player with name $playerName already " +
                    "exists"))
        }
        return Result.success(Unit)
    }

    fun onDialogConfirmed() {
        // TODO
    }

    fun onDialogCancelled() {
        navigate(NavigationCommand.NAVIGATE_UP)
    }
}