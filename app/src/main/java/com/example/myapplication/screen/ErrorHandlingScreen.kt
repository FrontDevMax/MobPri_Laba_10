package com.example.myapplication.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

suspend fun riskyOperation(success: Boolean): String {
    delay(1000)
    if (!success) {
        throw IllegalStateException("Операция не удалась")
    }
    return "Операция выполнена успешно"
}

fun riskyFlow(): Flow<String> = flow {
    emit("Шаг 1")
    delay(500)
    emit("Шаг 2")
    delay(500)
    throw RuntimeException("Ошибка на шаге 3!")
    emit("Шаг 3")
}.catch { exception ->
    emit("Ошибка обработана: ${exception.message}")
}

suspend fun safeOperation(success: Boolean): Result<String> {
    return try {
        delay(1000)
        if (!success) {
            Result.failure(IllegalStateException("Операция не удалась"))
        } else {
            Result.success("Операция выполнена успешно")
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

@Composable
fun ErrorHandlingScreen() {
    var result by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column {
        result?.let {
            Card {
                Text(text = it)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        errorMessage?.let {
            Card(colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )) {
                Text("Ошибка: $it")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    try {
                        val res = riskyOperation(true)
                        result = res
                    } catch (e: Exception) {
                        errorMessage = e.message
                    }
                }
            }
        ) {
            Text("Успешная операция")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    try {
                        val res = riskyOperation(false)
                        result = res
                    } catch (e: Exception) {
                        errorMessage = e.message
                    }
                }
            }
        ) {
            Text("Операция с ошибкой")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    riskyFlow().collect { value ->
                        result = value
                        delay(500)
                    }
                }
            }
        ) {
            Text("Flow с обработкой ошибок")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    val safeResult = safeOperation(false)
                    safeResult.fold(
                        onSuccess = { result = it },
                        onFailure = { errorMessage = it.message ?: "Неизвестная ошибка" }
                    )
                }
            }
        ) {
            Text("Безопасная операция")
        }
    }
}