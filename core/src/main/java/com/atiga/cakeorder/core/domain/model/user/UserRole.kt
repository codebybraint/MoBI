package com.atiga.cakeorder.core.domain.model.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserRole(
    val roleId: Int,
    val roleName: String
): Parcelable