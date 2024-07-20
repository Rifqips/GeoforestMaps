package id.application.core.domain.model.login

import id.application.core.data.network.model.login.ResponseLoginItem
import id.application.core.data.network.model.login.UserLogin

data class UserLoginResponse(
    val accessToken: String = "",
    val tokenType: String = "",
    val user: UserLogin
)

fun ResponseLoginItem.toLoginResponse() = UserLoginResponse(
    accessToken = this.accessToken,
    tokenType = this.tokenType,
    user = this.user
)