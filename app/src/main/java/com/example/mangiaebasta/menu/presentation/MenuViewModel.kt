package com.example.mangiaebasta.menu.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiaebasta.menu.data.local.Menu
import com.example.mangiaebasta.menu.data.repository.MenuRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MenuViewModel(
    private val repository: MenuRepository,
    private val fusedLocationClient: FusedLocationProviderClient,
) : ViewModel() {
    private val _menuList = MutableLiveData<List<Menu>>()
    val menuList: LiveData<List<Menu>> get() = _menuList

    private val _locationText = MutableLiveData<String>()
    val locationText: LiveData<String> get() = _locationText

    private val _latitude = MutableLiveData<Double>()
    val latitude: LiveData<Double> get() = _latitude

    private val _longitude = MutableLiveData<Double>()
    val longitude: LiveData<Double> get() = _longitude

    fun getMenuList(
        lat: Double,
        lng: Double,
    ) {
        viewModelScope.launch {
            val menus = repository.getMenuList(lat, lng)
            _menuList.value = menus
        }
    }

    fun getMenuById(mid: Int) {
        viewModelScope.launch {
            repository.getMenuLongDescription(mid)
        }
    }

    fun updateLocation() {
        viewModelScope.launch {
            val task = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            try {
                val location = task.await()
                _locationText.value = "Lat: ${location.latitude}, Lon: ${location.longitude}"
                _latitude.value = location.latitude
                _longitude.value = location.longitude
            } catch (e: Exception) {
                _locationText.value = "Error: ${e.message}"
            }
        }
    }

    fun buyMenu(
        mid: Int,
        lng: Double,
        lat: Double,
    ) {
        viewModelScope.launch {
            repository.buyMenu(mid, lat, lng)
        }
    }
}
