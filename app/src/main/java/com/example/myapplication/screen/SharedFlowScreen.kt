package com.example.myapplication.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SharedFlowScreen() {
    val eventsSharedFlow = remember { MutableSharedFlow<String>(replay = 3) }
    val eventsFlow: SharedFlow<String> = eventsSharedFlow.asSharedFlow()
    var events by remember { mutableStateOf<List<String>>(emptyList()) }
    var eventCount by remember { mutableStateOf(0) }
    var eventCounter by remember { mutableStateOf(0) }
    var isAutoGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var autoGenerationJob by remember { mutableStateOf<Job?>(null) }
    LaunchedEffect(Unit) {
        eventsFlow.collect { event ->
            events = (events + event).takeLast(10)
            eventCount++
        }
    }
    fun emitEvent(message: String) {
        scope.launch {
            eventsSharedFlow.emit(message)
        }
    }

    fun startAutoGeneration() {
        if (autoGenerationJob?.isActive == true) return
        isAutoGenerating = true
        autoGenerationJob = scope.launch {
            while (true) {
                delay(2000)
                eventCounter++
                val randomNumber = Random.nextInt(1, 101)
                emitEvent("Событие #$eventCounter: $randomNumber")
            }
        }
    }

    fun stopAutoGeneration() {
        isAutoGenerating = false
        autoGenerationJob?.cancel()
        autoGenerationJob = null
    }
    DisposableEffect(Unit) {
        onDispose {
            autoGenerationJob?.cancel()
        }
    }

    Column {
        Text("Всего событий: $eventCount")

        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(events.reversed()) { event ->
                Card {
                    Text(text = event)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                emitEvent("Ручное событие #${eventCount + 1}")
            }
        ) {
            Text("Сгенерировать событие")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (isAutoGenerating) {
                    stopAutoGeneration()
                } else {
                    startAutoGeneration()
                }
            }
        ) {
            Text(if (isAutoGenerating) "Остановить автогенерацию" else "Запустить автогенерацию")
        }
    }
}