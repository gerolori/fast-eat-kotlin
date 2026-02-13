package com.example.mangiaebasta

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiaebasta.common.data.local.DataStoreManager
import com.example.mangiaebasta.common.data.remote.CommunicationController
import com.example.mangiaebasta.common.model.DeliveryLocationWithSid
import com.example.mangiaebasta.common.model.MenuResponseFromGet
import com.example.mangiaebasta.common.model.MenuResponseFromGetandImage
import com.example.mangiaebasta.common.model.NearMenuandImage
import com.example.mangiaebasta.common.model.Position
import com.example.mangiaebasta.common.model.UpdateUserRequest
import com.example.mangiaebasta.common.model.UserInfoResponse
import com.example.mangiaebasta.common.model.UserResponse
import com.example.mangiaebasta.common.utils.CoroutineDispatchers
import com.example.mangiaebasta.common.utils.PositionManager
import com.example.mangiaebasta.features.menu.data.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

//val DISPATCHER = Dispatchers.IO

class AppViewModel(
    private val dataStoreManager: DataStoreManager,
    private val positionManager: PositionManager,
    private val imageRepository: ImageRepository,
    private val dispatchers: CoroutineDispatchers = CoroutineDispatchers()
) : ViewModel() {

    private val _screen = MutableStateFlow(null as String?)
    val screen: StateFlow<String?> = _screen

    private val _user = MutableStateFlow(null as UserResponse?)
    val user: StateFlow<UserResponse?> = _user

    private val _userInfo = MutableStateFlow(null as UserInfoResponse?)
    val userInfo: StateFlow<UserInfoResponse?> = _userInfo

    private val _firstRun = MutableStateFlow(null as Boolean?)
    val firstRun: StateFlow<Boolean?> = _firstRun

    private val _position = MutableStateFlow(null as Location?)
    val position: StateFlow<Location?> = _position

    private val _menuList = MutableStateFlow(null as List<NearMenuandImage>?)
    val menuList: StateFlow<List<NearMenuandImage>?> = _menuList

    private val _lastMenuMid = MutableStateFlow(null as Int?)
    val lastMenuMid: StateFlow<Int?> = _lastMenuMid

    private val _orderInfo = MutableStateFlow(null as Any?)
    val orderInfo: StateFlow<Any?> = _orderInfo
    //getters

    suspend fun getMenu(): MenuResponseFromGetandImage {
        return withContext(dispatchers.io) {
            val menu = CommunicationController.getMenu(
                _lastMenuMid.value!!,
                DeliveryLocationWithSid(
                    _user.value!!.sid,
                    Position(_position.value!!.latitude, _position.value!!.longitude)
                )
            )
            val image = imageRepository.getImage(_lastMenuMid.value!!, _user.value!!.sid)
            MenuResponseFromGetandImage(menu, image!!)
        }

    }

    suspend fun getMenu(mid: Int): MenuResponseFromGet {
        return withContext(dispatchers.io) {
            val menu = CommunicationController.getMenu(
                mid,
                DeliveryLocationWithSid(
                    _user.value!!.sid,
                    Position(_position.value!!.latitude, _position.value!!.longitude)
                )
            )
            menu
        }
    }

    suspend fun getMenuName(): String {
        return withContext(dispatchers.io) {
            val menu = CommunicationController.getMenu(
                _lastMenuMid.value!!,
                DeliveryLocationWithSid(
                    _user.value!!.sid,
                    Position(_position.value!!.latitude, _position.value!!.longitude)
                )
            )
            menu.name
        }
    }

    fun getNearMenus() {
        Log.d("MainActivity", "Getting near menus")
        viewModelScope.launch {
            if (_position.value != null && _user.value != null) {
                val sidAndLocation = DeliveryLocationWithSid(
                    _user.value!!.sid,
                    Position(_position.value!!.latitude, _position.value!!.longitude)
                )
                val nearMenus = CommunicationController.getMenus(sidAndLocation)
                Log.d("MainActivity", "nearMenus ${nearMenus}")
                val nearMenusAndImages: MutableList<NearMenuandImage> = mutableListOf()
                nearMenus.forEach { menu ->
                    val image = imageRepository.getImage(menu.mid, _user.value!!.sid)
                    Log.d("MainActivity", "image for ${menu.mid}: $image")
                    image?.let { NearMenuandImage(menu, it) }?.let { nearMenusAndImages.add(it) }
                }
                _menuList.value = nearMenusAndImages
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            var utente = dataStoreManager.getUser()
            if (utente == null) {
                utente = CommunicationController.createUser()
                dataStoreManager.saveUser(utente)
            }
            _user.value = utente
        }
    }

    suspend fun getUserFirstAndLastName(): String {
        withContext(dispatchers.io) {
            if (_userInfo.value == null) {
                val newUserInfo = CommunicationController.getUserInfo(_user.value!!)
                _userInfo.value = newUserInfo
            }
        }
        return _userInfo.value?.firstName + " " + _userInfo.value?.lastName
    }

    fun getLocation() {
        viewModelScope.launch {
            val location = positionManager.getLocation()
            _position.value = location
        }
    }

    fun getFirstRun() {
        viewModelScope.launch {
            val utente = dataStoreManager.getUser()
            _firstRun.value = utente == null

        }
    }

    fun getAddress(): String {
        return positionManager.getAddress(_position.value!!)
    }

    fun getOrderInfo() {
        viewModelScope.launch {
            if (_user.value != null) {
                _userInfo.value = CommunicationController.getUserInfo(_user.value!!)
                val orderInfo =
                    _userInfo.value?.lastOid?.let {
                        CommunicationController.getOrder(
                            it,
                            _user.value!!.sid
                        )
                    }
                _orderInfo.value = orderInfo
            }
        }
    }

    private fun reloadLastScreen() {
        viewModelScope.launch {
            val lastScreen = dataStoreManager.getLastScreen()
            Log.d("AppViewModel", "Last screen: $lastScreen")
            _screen.value = lastScreen
        }

    }

    fun loadUserInfo() {
        viewModelScope.launch {
            if (_user.value != null) {
                val userInfo = CommunicationController.getUserInfo(_user.value!!)
                Log.d("AppViewModel", "User info: $userInfo")
                _userInfo.value = userInfo
            }
        }
    }

    private fun reloadLastMid() {
        viewModelScope.launch {
            val lastMid = dataStoreManager.getLastMid()
            Log.d("AppViewModel", "Last mid: $lastMid")
            _lastMenuMid.value = lastMid
        }

    }

    // Reload all data

    fun reloadData() {
        reloadLastScreen()
        reloadLastMid()
        loadUserInfo()
    }

    // setters

    fun setFirstRun(bool: Boolean) {
        _firstRun.value = bool
    }

    fun setScreen(screen: String?) {
        if (screen != null) {
            _screen.value = screen
        }
    }

    fun setLastMenuMid(mid: Int) {
        _lastMenuMid.value = mid
    }

    fun updateUserInfo(infoUser: UserInfoResponse?) {
        if (infoUser != null) {
            val userForPut = UpdateUserRequest(
                infoUser.firstName,
                infoUser.lastName,
                infoUser.cardFullName,
                infoUser.cardNumber,
                infoUser.cardExpireMonth,
                infoUser.cardExpireYear,
                infoUser.cardCVV,
                _user.value!!.sid
            )

            viewModelScope.launch {
                try {
                    CommunicationController.updateUser(userForPut, infoUser.uid)
                } catch (e: Exception) {
                    Log.e("AppViewModel", "Error updating user info: $e")
                } finally {
                    _userInfo.value = CommunicationController.getUserInfo(_user.value!!)
                }
            }
        } else {
            Log.e("AppViewModel", "Error updating user info: user info is null")
        }

    }

    // dataStore functions

    private fun saveScreenDS() = runBlocking {
        Log.d("AppViewModel", "Saving screen in DataStore: ${_screen.value}")
        dataStoreManager.saveLastScreen(_screen.value)
    }

    private fun saveLastMidDS() = runBlocking {
        Log.d("AppViewModel", "Saving last mid in DataStore: ${_lastMenuMid.value}")
        dataStoreManager.saveLastMid(_lastMenuMid.value)
    }

    // Save all data

    fun saveDataDS() {
        saveScreenDS()
        saveLastMidDS()
    }

    // others

    fun checkLocationPermission(): Boolean {
        return positionManager.checkLocationPermission()
    }

    fun isValidUserInfo(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isValid = withContext(dispatchers.io) {
                loadUserInfo()
                _userInfo.value?.firstName != null && _userInfo.value?.lastName != null && _userInfo.value?.cardFullName != null && _userInfo.value?.cardNumber != null && _userInfo.value?.cardExpireMonth != null && _userInfo.value?.cardExpireYear != null && _userInfo.value?.cardCVV != null
            }
            callback(isValid)
        }
    }

    fun createOrder(callback: () -> Unit, errorCallback: (string: String?) -> Unit) {
        viewModelScope.launch {
            if (_position.value != null && _user.value != null) {
                val sidAndLocation = DeliveryLocationWithSid(
                    _user.value!!.sid,
                    Position(_position.value!!.latitude, _position.value!!.longitude)
                )
                try {
                    val order =
                        CommunicationController.createOrder(sidAndLocation, _lastMenuMid.value!!)
                    _userInfo.value = CommunicationController.getUserInfo(_user.value!!)
                    _orderInfo.value = order
                    Log.d("AppViewModel", "Order created: $order")
                    Log.d("AppViewModel", "User info: ${_userInfo.value}")
                    callback()
                } catch (e: Exception) {
                    Log.e("AppViewModel", "Error creating order: $e")
                    errorCallback(e.message)
                }
            }
        }
    }

    fun userCanOrder(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val canOrder = withContext(dispatchers.io) {
                val userInfo = CommunicationController.getUserInfo(_user.value!!)
                _userInfo.value = userInfo
                when (_userInfo.value?.orderStatus) {
                    "COMPLETED" -> false
                    "ON_DELIVERY" -> true
                    else -> false
                }
            }
            callback(canOrder)
        }
    }
}