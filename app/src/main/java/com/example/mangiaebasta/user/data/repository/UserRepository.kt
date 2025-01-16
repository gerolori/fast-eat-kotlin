package com.example.mangiaebasta.user.data.repository

import android.content.Context
import android.util.Log
import com.example.mangiaebasta.user.data.local.User
import com.example.mangiaebasta.user.data.local.UserDao
import com.example.mangiaebasta.user.data.remote.UserRemoteDataSource
import com.example.mangiaebasta.user.domain.model.UpdateUserRequest
import com.example.mangiaebasta.user.domain.model.UserInfoResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val remoteDataSource = UserRemoteDataSource(context, ioDispatcher)

    suspend fun getUser(uid: Int): User? =
        withContext(ioDispatcher) {
            val user = userDao.getUser(uid)
            if (user == null) {
                val remoteUser = remoteDataSource.getUserInfo()
                if (remoteUser != null) {
                    createDBUser(remoteUser)
                    remoteUser.let { userDao.getUser(it.uid) }
                } else {
                    Log.e("UserRepository", "Error getting user info")
                }
                remoteUser?.let { userDao.getUser(it.uid) }
            } else {
                user
            }
        }

    suspend fun updateUser(
        uid: Int,
        request: UpdateUserRequest,
    ): User? =
        withContext(ioDispatcher) {
            remoteDataSource.updateUserInfo(uid, request)
            val remoteUser = remoteDataSource.getUserInfo()
            if (remoteUser != null) {
                createDBUser(remoteUser)
                Log.d("UserRepository", "User in db updated successfully")
                remoteUser.let { userDao.getUser(it.uid) }
            } else {
                Log.e("UserRepository", "Error updating user info")
                null
            }
        }

    suspend fun initializeUser(uid: Int): User? =
        withContext(ioDispatcher) {
            var user = userDao.getUser(uid)
            Log.d("UserRepository", "User: $user")
            if (user == null ||
                user.firstName.isNullOrBlank() ||
                user.lastName.isNullOrBlank() ||
                user.cardFullName.isNullOrBlank() ||
                user.cardNumber.isNullOrBlank() ||
                user.cardExpireMonth == null ||
                user.cardExpireYear == null ||
                user.cardCVV.isNullOrBlank()
            ) {
                Log.d("UserRepository", "User is null or blank, getting remote user")
                var remoteUser = remoteDataSource.getUserInfo()
                Log.d("UserRepository", "Remote user: $remoteUser")
                @Suppress("ktlint:standard:max-line-length")
                if (remoteUser == null ||
                    remoteUser.firstName.isNullOrBlank() ||
                    remoteUser.lastName.isNullOrBlank() ||
                    remoteUser.cardFullName.isNullOrBlank() ||
                    remoteUser.cardNumber.isNullOrBlank() ||
                    remoteUser.cardExpireMonth == null ||
                    remoteUser.cardExpireYear == null ||
                    remoteUser.cardCVV.isNullOrBlank()
                ) {
                    Log.d("UserRepository", "Remote user is null or blank, creating dummy user")
                    val request =
                        UpdateUserRequest(
                            firstName = "John",
                            lastName = "Doe",
                            cardFullName = "John Doe",
                            cardNumber = "1234567890123456",
                            cardExpireMonth = 12,
                            cardExpireYear = 2030,
                            cardCVV = "123",
                        )
                    updateUser(uid, request)
                } else {
                    createDBUser(remoteUser)
                    user = userDao.getUser(remoteUser.uid)
                }
            }
            user
        }

    private suspend fun createDBUser(remoteUser: UserInfoResponse): User {
        val dbUser =
            User(
                uid = remoteUser.uid,
                firstName = remoteUser.firstName,
                lastName = remoteUser.lastName,
                cardFullName = remoteUser.cardFullName,
                cardNumber = remoteUser.cardNumber,
                cardExpireMonth = remoteUser.cardExpireMonth,
                cardExpireYear = remoteUser.cardExpireYear,
                cardCVV = remoteUser.cardCVV,
                lastOid = remoteUser.lastOid ?: 0,
                orderStatus = remoteUser.orderStatus ?: "",
            )
        userDao.insertUser(dbUser)
        return dbUser
    }
}
