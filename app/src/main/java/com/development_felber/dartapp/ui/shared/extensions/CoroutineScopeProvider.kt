package com.development_felber.dartapp.ui.shared.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

interface CoroutineScopeProvider {

    fun getCoroutineScope() : CoroutineScope

    fun <T, K> StateFlow<T>.mapState(
        transform: (data: T) -> K
    ): StateFlow<K> {
        return mapLatest {
            transform(it)
        }
            .asStateFlow(transform(value))
    }

    fun <T, K> StateFlow<T>.mapState(
        initialValue: K,
        transform: suspend (data: T) -> K
    ): StateFlow<K> {
        return mapLatest {
            transform(it)
        }
            .stateIn(getCoroutineScope(), SharingStarted.Eagerly, initialValue)
    }

    fun <T> Flow<T>.asStateFlow(
        initialValue: T
    ) = stateIn(getCoroutineScope(), SharingStarted.Eagerly, initialValue)
}