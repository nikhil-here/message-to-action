package com.nikhil.here.message_to_action.data.entity

import com.google.gson.annotations.SerializedName

data class CompletionRequestEntity(
    @SerializedName("model")
    val model : String,
    @SerializedName("prompt")
    val prompt : String,
    @SerializedName("temperature")
    val temperature : Float,
    @SerializedName("max_tokens")
    val maxTokens : Int,
    @SerializedName("top_p")
    val topP : Float,
    @SerializedName("frequency_penalty")
    val frequencyPenalty : Float,
    @SerializedName("presence_penalty")
    val presencePenalty : Float
)
