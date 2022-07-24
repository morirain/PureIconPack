package dev.jahir.blueprint.data.models

import com.google.gson.annotations.SerializedName

data class AppVotes(@SerializedName("status") val status: String, @SerializedName("result") val result: List<Components>)

data class Components(val componentInfo: String, val votes: Int = -1)