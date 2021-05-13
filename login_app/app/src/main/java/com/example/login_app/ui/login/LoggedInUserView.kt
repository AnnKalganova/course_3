package com.example.login_app.ui.login

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
        val userId: String,
        val displayName: String,
        val groupId: Int
        //... other data fields that may be accessible to the UI
)