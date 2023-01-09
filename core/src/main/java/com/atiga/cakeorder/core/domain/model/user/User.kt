package com.atiga.cakeorder.core.domain.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User(
    var userId: String,
    var username: String,
    var userRole: UserRole
): Parcelable