package com.example.mangiaebasta.user.data.repository

import com.example.mangiaebasta.user.data.remote.UserRemoteDataSource
import com.example.mangiaebasta.user.domain.model.UserInfoResponse
import com.example.mangiaebasta.user.domain.model.UserResponse

class UserRepository(
    private val userRemoteDataSource: UserRemoteDataSource,
) {
    suspend fun getUserInfo(
        sid: String,
        uid: Int,
    ): UserInfoResponse? = userRemoteDataSource.getUserInfo(sid, uid)

    suspend fun requestSID(): UserResponse? = userRemoteDataSource.requestSID()
}
