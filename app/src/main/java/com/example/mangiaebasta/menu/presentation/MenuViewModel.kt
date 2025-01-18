package com.example.mangiaebasta.menu.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiaebasta.menu.data.local.Menu
import com.example.mangiaebasta.menu.data.repository.MenuRepository
import kotlinx.coroutines.launch

class MenuViewModel(
    private val repository: MenuRepository,
) : ViewModel() {
    private val _menuList = MutableLiveData<List<Menu>>()
    val menuList: LiveData<List<Menu>> get() = _menuList

    fun getMenuList(
        lat: Double,
        lng: Double,
    ) {
        viewModelScope.launch {
            val menus = repository.getMenuList(lat, lng)
            _menuList.value = menus
        }
    }
}
