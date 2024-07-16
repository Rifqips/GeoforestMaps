package id.application.core.domain.model.login

data class UserLoginRequest(
    val email: String = "",
    val password: String = ""
)
