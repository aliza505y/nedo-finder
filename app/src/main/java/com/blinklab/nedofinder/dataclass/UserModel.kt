package com.blinklab.nedofinder.dataclass

import androidx.annotation.Keep

@Keep
data class UserModel(
    val uid: String = "",
    val username: String = "",
    val userEmail: String = "",
    val profileImgUrl: String = "",
)
