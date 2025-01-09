package com.example.mangiaebasta.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiaebasta.user.data.local.User
import com.example.mangiaebasta.user.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository,
) : ViewModel() {
    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

    fun insertUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
            _userData.value = user
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
            _userData.value = user
        }
    }

    fun getUser(uid: Int) {
        viewModelScope.launch {
            val user = repository.getUser(uid)
            _userData.value = user
        }
    }
}
