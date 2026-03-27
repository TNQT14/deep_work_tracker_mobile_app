package com.example.todo.presentation.tododetail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.deepworktracker.ui.theme.DeepWorkTrackerTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen (
    todoId: String,
    onBack: () -> Unit = {}
){
    DeepWorkTrackerTheme(){
        Scaffold (
            topBar = {
                TopAppBar(
                    title = { Text(text = "Task Detail") }
                )
            }
        ){
            Column {  }

        }
    }
}