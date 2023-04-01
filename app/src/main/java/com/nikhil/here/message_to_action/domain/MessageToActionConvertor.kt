package com.nikhil.here.message_to_action.domain

import android.util.Log
import com.google.gson.Gson
import com.nikhil.here.message_to_action.data.api.ChatGptService
import com.nikhil.here.message_to_action.data.entity.CompletionRequestEntity
import com.nikhil.here.message_to_action.data.entity.CompletionResponseEntity
import com.nikhil.here.message_to_action.domain.entity.FetchDbOpnTypeEntity
import com.nikhil.here.message_to_action.domain.entity.FetchUserActionAndExtraParamsEntity
import com.nikhil.here.message_to_action.domain.entity.MessageToActionResponse
import org.json.JSONObject
import javax.inject.Inject

class MessageToActionConvertor @Inject constructor(
    private val chatGptService: ChatGptService,
    private val gson: Gson
) {

    companion object {
        private const val DEFAULT_MODEL = "text-davinci-003"
        private const val DEFAULT_TEMPERATURE = 0.9f
        private const val DEFAULT_MAX_TOKENS = 500
        private const val DEFAULT_TOP_P = 1f
        private const val DEFAULT_FREQUENCY_PENALTY = 0f
        private const val DEFAULT_PRESENCE_PENALTY = 0.6f
        private const val TAG = "MessageToActionConverto"

        private const val jsonPattern = "###"
    }


    suspend fun convertMessageToAction(
        userQuery: String,
        apiKey: String
    ) : JSONObject {
        val fetchDbOpnTypeEntity = fetchDbOpnType(userQuery = userQuery, apiKey = apiKey)
        val fetchUserActionAndExtraParamsEntity =  fetchDbOpnTypeEntity?.let {
            fetchUserActionAndExtraParams(
                apiKey = apiKey,
                userQuery = userQuery,
                fetchDbOpnTypeEntity = fetchDbOpnTypeEntity
            )
        }
        Log.i(TAG, "convertMessageToAction: fetchDbOpnType $fetchDbOpnTypeEntity fetchUserAction $fetchUserActionAndExtraParamsEntity")
        val messageToActionResponse = MessageToActionResponse(
            operationType = fetchDbOpnTypeEntity?.operationType ?: OperationType.UNKNOWN,
            extraParams = MessageToActionResponse.ExtraParams(
                //operationTypeReasoning = fetchDbOpnTypeEntity?.extraParams?.reason,
                languageCodes = fetchDbOpnTypeEntity?.extraParams?.languageCodes,
                //userActionReasoning = fetchUserActionAndExtraParamsEntity?.extraParams?.reason,
                item = fetchUserActionAndExtraParamsEntity?.extraParams?.item,
                category = fetchUserActionAndExtraParamsEntity?.extraParams?.category,
                query = fetchUserActionAndExtraParamsEntity?.extraParams?.query,
            ),
            userAction = fetchUserActionAndExtraParamsEntity?.userAction ?: UserAction.UNKNOWN
        )
        return JSONObject(gson.toJson(messageToActionResponse))
    }


    private suspend fun fetchDbOpnType(userQuery: String, apiKey: String): FetchDbOpnTypeEntity? {
        return try {
            val response = chatGptService.complete(
                completionRequestEntity = CompletionRequestEntity(
                    model = DEFAULT_MODEL,
                    prompt = createPromptForFetchingDbOpn(userQuery = userQuery),
                    temperature = DEFAULT_TEMPERATURE,
                    maxTokens = DEFAULT_MAX_TOKENS,
                    topP = DEFAULT_TOP_P,
                    frequencyPenalty = DEFAULT_FREQUENCY_PENALTY,
                    presencePenalty = DEFAULT_PRESENCE_PENALTY
                ),
                bearerToken = "Bearer $apiKey"
            )
            response.body()?.let {
                parseFetchDbOpnResponse(response = it, userQuery = userQuery)
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun parseFetchDbOpnResponse(
        response: CompletionResponseEntity,
        userQuery: String
    ): FetchDbOpnTypeEntity? {
        val rawResponse = response.choices.firstOrNull()?.text
        return if (rawResponse != null) {
            try {
                val startIndex = rawResponse.indexOf(jsonPattern) + jsonPattern.length
                val endIndex = rawResponse.lastIndexOf(jsonPattern)
                val jsonString = rawResponse.substring(startIndex, endIndex)
                gson.fromJson(jsonString, FetchDbOpnTypeEntity::class.java)
            } catch (e: Exception) {
                Log.i(TAG, "parseFetchDbOpnResponse: $e")
                null
            }
        } else {
            Log.i(TAG, "parseFetchDbOpnResponse: rawResponse is empty")
            null
        }
    }

    private suspend fun parseUserActionFromResponse(
        response: CompletionResponseEntity,
        userQuery: String
    ): FetchUserActionAndExtraParamsEntity? {
        val rawResponse = response.choices.firstOrNull()?.text
        return if (rawResponse != null) {
            try {
                val startIndex = rawResponse.indexOf(jsonPattern) + jsonPattern.length
                val endIndex = rawResponse.lastIndexOf(jsonPattern)
                val jsonString = rawResponse.substring(startIndex, endIndex)
                gson.fromJson(jsonString, FetchUserActionAndExtraParamsEntity::class.java)
            } catch (e: Exception) {
                Log.i(TAG, "parseUserActionFromResponse: $e")
                null
            }
        } else {
            Log.i(TAG, "parseUserActionFromResponse: rawResponse is empty")
            null
        }
    }


    private suspend fun fetchUserActionAndExtraParams(
        apiKey: String,
        userQuery: String,
        fetchDbOpnTypeEntity: FetchDbOpnTypeEntity
    ) : FetchUserActionAndExtraParamsEntity? {
        return when(fetchDbOpnTypeEntity.operationType) {
            OperationType.READ -> {
                val response = chatGptService.complete(
                    completionRequestEntity = CompletionRequestEntity(
                        model = DEFAULT_MODEL,
                        prompt = createPromptForFetchingUserAction(userQuery = userQuery, operationType = fetchDbOpnTypeEntity.operationType),
                        temperature = DEFAULT_TEMPERATURE,
                        maxTokens = DEFAULT_MAX_TOKENS,
                        topP = DEFAULT_TOP_P,
                        frequencyPenalty = DEFAULT_FREQUENCY_PENALTY,
                        presencePenalty = DEFAULT_PRESENCE_PENALTY
                    ),
                    bearerToken = "Bearer $apiKey"
                )
                response.body()?.let {
                    parseUserActionFromResponse(response = it, userQuery = userQuery)
                }
            }
            else -> {
                null
            }
        }
    }

    private fun createPromptForFetchingUserAction(userQuery: String, operationType: OperationType): String {
        val task = "we want to perform ${operationType.name} database operation based on the above user query, map the exact user action from the above user query and also follow following rules while giving response"
        val rules = listOf(
            "response should be in the form of JSON only. don't add any additional information or explanation in the response.",
            "response JSON should contain only following fields \"userAction\" and \"extraParams\".",
            "\"userAction\" is a string, and \"extraParams\" is a nested JSON object inside response JSON.",
            "\"userAction\" field value should be extracted from the above user query, \"userAction\" can be SHOW_MENU, SEARCH_BY_FOOD_QUERY, UNKNOWN",
            "if \"userAction\" is not clear from the user query then keep \"userAction\" as UNKNOWN.",
            "add the \"reason\" field inside nested \"extraParams\" JSON explaining reasoning behind mapping given user query to particular \"userAction\".",
            "\"userAction\" will be equal to SHOW_MENU when user requests a menu from a restaurant",
            "\"userAction\" will be equal to SEARCH_BY_FOOD_QUERY when user requests any specific food item or category",
            "add the \"query\" field inside nested \"extraParams\" JSON, \"query\" field should contain any food item name, category or description mention in the user query, if you are not able to find any specific food item, category or description in the user query, keep \"query\" field as null",
            "add the \"item\" field inside nested \"extraParams\", \"item\" field will contain specific food item mention in the user query, if there are no food item mention in the user query then keep the \"item\" field json value as null",
            "add the \"category\" field inside nested \"extraParams\", based on user query try to find the \"category\" of food item user is trying to request, if there are no specific food category is mention in the user query then keep \"category\" json field value as null",
            "add $jsonPattern at the beginning and end of the response JSON"
        )
        val prompt = "User Query : $userQuery" +
                "\n$task" +
                "\nRules" +
                rules.joinToString { "\n -$it" }

        Log.i(TAG, "createPromptForFetchingUserAction: $prompt")
        return prompt
    }

    private fun createPromptForFetchingDbOpn(userQuery: String): String {
        val task =
            "find out which database operation we should do based on the above user query and also follow following rules while giving response.\n"
        val rules = listOf(
            "response should be in the form of JSON only. don't add any additional information or explanation in the response.",
            "response JSON should contain only following fields \"operationType\" and \"extraParams\".",
            "\"operationType\" is a string, and \"extraParams\" is a nested JSON object inside response JSON.",
            "\"operationType\" field value should be extracted from the above user query, \"operationTypes\" can be ${
                OperationType.values().joinToString(", ") { it.name }
            }.",
            "if \"operationType\" is not clear from the user query then keep \"operationType\" as ${OperationType.UNKNOWN}.",
            "add the \"reason\" field inside nested \"extraParams\" JSON explaining why reason behind mapping given user query to particular \"operationType\".",
            "add “languageCodes” field in \"extraParams\" JSON, languageCodes field should contain the ISO 639-1 language codes of all the languages used in the user query.",
            "only use user query to map it to the \"operationTypes\". do not perform any action based on the user query.",
            "add $jsonPattern at the beginning and end of the response JSON."
        )
        val prompt = "User Query : $userQuery" +
                "\n$task" +
                "\nRules" +
                rules.joinToString { "\n -$it" }

        Log.i(TAG, "createPromptForFetchingDbOpn: $prompt")
        return prompt
    }
}