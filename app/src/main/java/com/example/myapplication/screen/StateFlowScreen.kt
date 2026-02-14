package com.example.myapplication.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun StateFlowScreen() {
    val counterStateFlow = remember { MutableStateFlow(0) }
    val counter: StateFlow<Int> = counterStateFlow.asStateFlow()
    val counterValue by counter.collectAsState()

    val isAutoIncrementingStateFlow = remember { MutableStateFlow(false) }
    val isAutoIncrementing: StateFlow<Boolean> = isAutoIncrementingStateFlow.asStateFlow()
    val isAutoIncrementingValue by isAutoIncrementing.collectAsState()

    val scope = rememberCoroutineScope()
    var autoIncrementJob by remember { mutableStateOf<Job?>(null) }

    fun increment() {
        counterStateFlow.value += 1
    }

    fun decrement() {
        counterStateFlow.value -= 1
    }

    fun reset() {
        counterStateFlow.value = 0
    }

    fun incrementBy(value: Int) {
        counterStateFlow.value += value
    }

    fun toggleAutoIncrement() {
        if (isAutoIncrementingValue) {
            isAutoIncrementingStateFlow.value = false
            autoIncrementJob?.cancel()
            autoIncrementJob = null
        } else {
            isAutoIncrementingStateFlow.value = true
            autoIncrementJob = scope.launch {
                while (true) {
                    delay(1000)
                    counterStateFlow.value += 1
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            autoIncrementJob?.cancel()
        }
    }

    Column {
        Text(
            text = counterValue.toString(),
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(8.dp))
        if (isAutoIncrementingValue) {
            Row {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Text("Автоинкремент активен")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { decrement() }) { Text("-1") }
            Spacer(modifier = Modifier.size(8.dp))
            Button(onClick = { increment() }) { Text("+1") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { reset() }) {
            Text("Сброс")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { incrementBy(5) }) {
            Text("+5")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { toggleAutoIncrement() }) {
            Text(if (isAutoIncrementingValue) "Остановить" else "Запустить автоинкремент")
        }
    }
}