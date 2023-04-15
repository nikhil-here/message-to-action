package com.nikhil.here.message_to_action


import androidx.lifecycle.ViewModel
import com.nikhil.here.message_to_action.domain.MessageToActionConvertor
import com.nikhil.here.message_to_action.domain.entity.ConvertMessageToActionResponse
import com.nikhil.here.message_to_action.domain.entity.MessageToActionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val messageToActionConvertor: MessageToActionConvertor
) : ViewModel(), ContainerHost<MainState, MainSideEffects> {

    override val container: Container<MainState, MainSideEffects> =
        container(MainState.LoadOpenAiConfig)

    fun updateOpenAIAPIKey(
        apiKey: String
    ) {
        intent {
            if (apiKey.isNotEmpty()) {
                reduce {
                    MainState.GetPrompt(apiKey = apiKey)
                }
            } else {
                postSideEffect(
                    MainSideEffects.ShowToast("API key is empty")
                )
            }
        }
    }

    fun onPrompt(userQuery: String, model: String) {
        intent {
            if (userQuery.isNotEmpty() && model.isNotEmpty()) {
                val apiKey = (state as? MainState.GetPrompt)?.apiKey.orEmpty()
                updateIsLoading(true)
                val response = messageToActionConvertor.convertMessageToAction(
                    userQuery = userQuery,
                    apiKey = apiKey,
                    model = model
                )
                updateIsLoading(false)
                reduce {
                    (state as? MainState.GetPrompt)?.copy(
                        messageToActionResponse = response
                    ) ?: state
                }
            } else {
                postSideEffect(
                    MainSideEffects.ShowToast("User Query or Model cannot be empty")
                )
            }
        }
    }


    fun updateIsLoading(isLoading: Boolean) {
        intent {
            reduce {
                (state as? MainState.GetPrompt)?.let {
                    it.copy(
                        isLoading = isLoading
                    )
                } ?: state
            }
        }
    }

}


sealed class MainState {
    object LoadOpenAiConfig : MainState()
    data class GetPrompt(
        val apiKey: String,
        val isLoading: Boolean = false,
        val messageToActionResponse: JSONObject? = null
    ) : MainState()
}

sealed class MainSideEffects {
    data class ShowToast(val message: String) : MainSideEffects()
}