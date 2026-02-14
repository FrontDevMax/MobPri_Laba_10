package com.example.myapplication.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun numberFlow(): Flow<Int> = flow {
    for (i in 1..10) {
        delay(500)
        emit(i)
    }
}

fun transformedFlow(flow: Flow<Int>): Flow<Int> = flow
    .map { it * it }
    .filter { it % 2 == 0 }

fun errorFlow(): Flow<String> = flow {
    emit("Первое значение")
    delay(500)
    emit("Второе значение")
    delay(500)
    throw RuntimeException("Произошла ошибка!")
}.catch { exception ->
    emit("Ошибка обработана: ${exception.message}")
}

@Composable
fun FlowScreen() {
    var flowValues by remember { mutableStateOf<List<String>>(emptyList()) }
    val scope = rememberCoroutineScope()

    Column {
        LazyColumn {
            items(flowValues) { value ->
                Text(text = value)
            }
        }

        Button(
            onClick = {
                flowValues = emptyList()
                scope.launch {
                    numberFlow().collect { value ->
                        flowValues = flowValues + "Число: $value"
                    }
                }
            }
        ) {
            Text("Запустить Flow")
        }

        Button(
            onClick = {
                flowValues = emptyList()
                scope.launch {
                    transformedFlow(numberFlow()).collect { value ->
                        flowValues = flowValues + "Квадрат четного: $value"
                    }
                }
            }
        ) {
            Text("Запустить преобразованный Flow")
        }

        Button(
            onClick = {
                flowValues = emptyList()
                scope.launch {
                    errorFlow().collect { value ->
                        flowValues = flowValues + value
                    }
                }
            }
        ) {
            Text("Запустить Flow с ошибкой")
        }
    }
}