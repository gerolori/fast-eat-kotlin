package com.example.mangiaebasta.menu.data.repository

import android.content.Context
import com.example.mangiaebasta.menu.data.local.Menu
import com.example.mangiaebasta.menu.data.local.MenuDao
import com.example.mangiaebasta.menu.data.remote.MenuRemoteDataSource
import com.example.mangiaebasta.menu.domain.model.MenuNearRequest
import kotlinx.coroutines.CoroutineDispatcher

class MenuRepository(
    private val menuDao: MenuDao,
    context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val remoteDataSource = MenuRemoteDataSource(context, ioDispatcher)

    suspend fun getMenuList(
        lat: Double,
        lng: Double,
    ): List<Menu> {
        val remoteMenuList = remoteDataSource.getMenuList(MenuNearRequest(lat, lng))
        for (menu in remoteMenuList) {
            val localMenu = menuDao.getMenu(menu.mid)
            if (localMenu == null || localMenu.imageVersion != menu.imageVersion) {
                val imageResponse = remoteDataSource.getMenuImage(menu.mid)
                val updatedMenu =
                    Menu(
                        menu.mid,
                        menu.name,
                        menu.price,
                        menu.location.lat,
                        menu.location.lng,
                        menu.imageVersion,
                        menu.shortDescription,
                        menu.deliveryTime,
                        localMenu?.longDescription,
                        imageResponse.base64,
                    )
                menuDao.insertMenu(updatedMenu)
            } else {
                val remoteMenu =
                    Menu(
                        menu.mid,
                        menu.name,
                        menu.price,
                        menu.location.lat,
                        menu.location.lng,
                        menu.imageVersion,
                        menu.shortDescription,
                        menu.deliveryTime,
                        localMenu.longDescription,
                        localMenu.image,
                    )
                menuDao.insertMenu(remoteMenu)
            }
        }
        val menuIds = remoteMenuList.map { it.mid }
        var menuList = emptyList<Menu>()
        for (mid in menuIds) {
            val menu = menuDao.getMenu(mid)
            if (menu != null) {
                menuList += menu
            }
        }
        return menuList
    }

    suspend fun getMenuLongDescription(mid: Int): String {
        val menu = menuDao.getMenu(mid)
        if (menu?.longDescription == null) {
            val longDescription = remoteDataSource.getMenuDetailed(mid, menu!!.lat, menu.lng)?.longDescription
            if (longDescription != null) {
                menuDao.updateMenuLongDescription(mid, longDescription)
            }
        }
        val menuDb = menuDao.getMenu(mid)
        return menuDb?.longDescription ?: throw IllegalStateException("Menu long description is null")
    }
}
