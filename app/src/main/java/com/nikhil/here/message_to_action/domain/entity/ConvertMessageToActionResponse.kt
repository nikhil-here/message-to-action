package com.nikhil.here.message_to_action.domain.entity

import com.google.gson.JsonObject
import org.json.JSONObject

sealed class ConvertMessageToActionResponse {
    data class Action(
        val json: JSONObject
    ) : ConvertMessageToActionResponse()

    data class UnknownException(
        val message: String
    ) : ConvertMessageToActionResponse()

    data class ApiError(
        val message: String
    ) : ConvertMessageToActionResponse()

    data class ApiResponseParsingError(
        val message: String
    ) : ConvertMessageToActionResponse()

    data class FetchDbOpnTypeSuccess(
        val response : FetchDbOpnTypeEntity,
        val userQuery : String
    ) : ConvertMessageToActionResponse()
}



