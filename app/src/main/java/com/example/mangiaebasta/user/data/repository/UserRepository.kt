package com.example.mangiaebasta.user.data.repository

import android.content.Context
import com.example.mangiaebasta.user.data.local.User
import com.example.mangiaebasta.user.data.local.UserDao
import com.example.mangiaebasta.user.data.remote.UserRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val remoteDataSource = UserRemoteDataSource(context, ioDispatcher)

    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun getUser(uid: Int): User? =
        withContext(ioDispatcher) {
            val user = userDao.getUser(uid)
            if (user == null) {
                val remoteUser = remoteDataSource.getUserInfo()
                if (remoteUser != null) {
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
                }
                remoteUser?.let { userDao.getUser(it.uid) }
            } else {
                user
            }
        }
}
