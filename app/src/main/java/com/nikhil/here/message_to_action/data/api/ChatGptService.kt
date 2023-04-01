package com.nikhil.here.message_to_action.data.api

import com.nikhil.here.message_to_action.data.entity.CompletionRequestEntity
import com.nikhil.here.message_to_action.data.entity.CompletionResponseEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatGptService {

    companion object {
        const val CHAT_GPT_BASE_URL = "https://api.openai.com/v1/"
    }

    @POST("completions")
    suspend fun complete(
        @Body completionRequestEntity: CompletionRequestEntity,
        @Header("Authorization") bearerToken: String
    ): Response<CompletionResponseEntity>
}