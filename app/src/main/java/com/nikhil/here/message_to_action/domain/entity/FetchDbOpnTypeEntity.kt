package com.nikhil.here.message_to_action.domain.entity

import com.google.gson.annotations.SerializedName
import com.nikhil.here.message_to_action.domain.OperationType
import com.nikhil.here.message_to_action.domain.UserAction

data class FetchDbOpnTypeEntity(
    @SerializedName("operationType")
    val operationType: OperationType,
    @SerializedName("extraParams")
    val extraParams: ExtraParams
) {
    data class ExtraParams(
        @SerializedName("reason")
        val reason: String,
        @SerializedName("languageCodes")
        val languageCodes: List<String>
    )
}


data class FetchUserActionAndExtraParamsEntity(
    @SerializedName("userAction")
    val userAction: UserAction,
    @SerializedName("extraParams")
    val extraParams: ExtraParams
) {
    data class ExtraParams(
        @SerializedName("reason")
        val reason: String?,
        @SerializedName("query")
        val query: String?,
        @SerializedName("item")
        val item: String?,
        @SerializedName("category")
        val category: String?
    )
}


data class MessageToActionResponse(
    @SerializedName("operationType")
    val operationType: OperationType = OperationType.UNKNOWN,
    @SerializedName("userAction")
    val userAction: UserAction = UserAction.UNKNOWN,
    @SerializedName("extraParams")
    val extraParams: MessageToActionResponse.ExtraParams? = null
) {
    data class ExtraParams(
        @SerializedName("item")
        val item: String? = null,
        @SerializedName("query")
        val query: String? = null,
        @SerializedName("category")
        val category: String? = null,
        @SerializedName("languageCodes")
        val languageCodes: List<String>? = null,
        @SerializedName("operationTypeReasoning")
        val operationTypeReasoning: String? = null,
        @SerializedName("userActionReasoning")
        val userActionReasoning: String? = null,
    )
}

