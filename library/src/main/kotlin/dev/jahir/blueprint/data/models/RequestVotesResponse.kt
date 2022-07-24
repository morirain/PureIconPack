package dev.jahir.blueprint.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestVotesResponse(val status: String? = null, val result: String? = null) : Parcelable
