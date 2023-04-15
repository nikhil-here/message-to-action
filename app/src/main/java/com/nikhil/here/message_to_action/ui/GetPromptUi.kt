package com.nikhil.here.message_to_action.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.nikhil.here.message_to_action.MainState
import com.nikhil.here.message_to_action.MainViewModel
import com.nikhil.here.message_to_action.domain.MessageToActionConvertor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetPromptUi(mainState: MainState.GetPrompt, mainViewModel: () -> MainViewModel) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var userQuery by remember {
            mutableStateOf("")
        }

        var model by remember {
            mutableStateOf(MessageToActionConvertor.DEFAULT_MODEL)
        }

        val scrollState = rememberScrollState()

        Text(
            text = "Message to Action", style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "based on your message, we will create a json object which you can use to perform database operation. this will help you build CRM system",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (mainState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }



        (mainState.messageToActionResponse)?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
                    .scrollable(scrollState, Orientation.Vertical)
            ) {
                Text(
                    text = it.toString(4),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        TextField(
            value = model, onValueChange = {
                model = it
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = userQuery,
                onValueChange = {
                    userQuery = it
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = {
                    mainViewModel().onPrompt(userQuery = userQuery, model = model)
                }
            ) {
                Text(text = "POST")
            }
        }
    }
}