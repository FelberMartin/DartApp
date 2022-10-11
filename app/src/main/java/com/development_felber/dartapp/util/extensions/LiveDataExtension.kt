package com.development_felber.dartapp.util.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData

@Composable
fun <T> LiveData<T>.observeAsStateNonOptional(): State<T> = observeAsState(value!!)