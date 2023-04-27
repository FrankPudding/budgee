package com.example.budgee.db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*

class AssetTypeViewModel(private val assetTypeDao: AssetTypeDao) : ViewModel() {
    fun addNewAssetType(name: String, position: Int) {
        val assetType = AssetType(id=UUID.randomUUID(), name=name, position=position)
        viewModelScope.launch {
            assetTypeDao.insert(assetType)
        }
    }
}

class AssetTypeViewModelFactory(private val assetTypeDao: AssetTypeDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssetTypeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssetTypeViewModel(assetTypeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
