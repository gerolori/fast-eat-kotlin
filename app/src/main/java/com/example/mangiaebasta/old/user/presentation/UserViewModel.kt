package com.example.mangiaebasta.old.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiaebasta.old.user.data.local.User
import com.example.mangiaebasta.old.user.data.repository.UserRepository
import com.example.mangiaebasta.old.user.domain.model.UpdateUserRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository,
) : ViewModel() {
    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

    fun getUser(uid: Int) {
        viewModelScope.launch {
            val user = repository.getUser(uid)
            _userData.value = user
        }
    }

    fun updateUser(
        uid: Int,
        request: UpdateUserRequest,
    ) {
        viewModelScope.launch {
            repository.updateUser(uid, request)
            _userData.value = repository.getUser(uid)
        }
    }

    fun initializeUser(uid: Int) {
        viewModelScope.launch {
            val user = repository.initializeUser(uid)
            _userData.value = user
        }
    }
}
