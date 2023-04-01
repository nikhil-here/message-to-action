package com.nikhil.here.message_to_action

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.nikhil.here.message_to_action.ui.GetPromptUi
import com.nikhil.here.message_to_action.ui.LoadOpenAIConfigUI
import com.nikhil.here.message_to_action.ui.theme.MessagetoactionTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainState by mainViewModel.container.stateFlow.collectAsState()

            LaunchedEffect(key1 = true) {
                mainViewModel.container.sideEffectFlow.collectLatest {
                    when (it) {
                        is MainSideEffects.ShowToast -> {
                            Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            MessagetoactionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (val state = mainState) {
                        MainState.LoadOpenAiConfig -> {
                            LoadOpenAIConfigUI {
                                mainViewModel
                            }
                        }

                        is MainState.GetPrompt -> {
                            GetPromptUi(mainState = state) {
                                mainViewModel
                            }
                        }
                    }
                }
            }
        }
    }
}

