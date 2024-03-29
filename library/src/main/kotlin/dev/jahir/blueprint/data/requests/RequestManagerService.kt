package dev.jahir.blueprint.data.requests

import dev.jahir.blueprint.data.models.AppVotes
import dev.jahir.blueprint.data.models.RequestManagerResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RequestManagerService {
    @Headers("Accept: application/json", "User-Agent: afollestad/icon-request")
    @Multipart
    @POST("v1/request")
    suspend fun uploadRequest(
        @Header("TokenID") TokenID: String,
        @Part("apps") apps: String,
        @Part archive: MultipartBody.Part
    ): RequestManagerResponse

    @Headers("Accept: application/json", "User-Agent: afollestad/icon-request")
    @POST("v1/request/request-votes/")
    suspend fun sendVotesRequest(
        @Body apps: String,
    ): Response<AppVotes>
}