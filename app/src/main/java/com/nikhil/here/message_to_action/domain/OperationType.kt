package com.nikhil.here.message_to_action.domain

enum class OperationType {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    UNKNOWN
}


enum class UserAction {
    SHOW_MENU,
    SEARCH_BY_FOOD_QUERY,
    UNKNOWN,
}