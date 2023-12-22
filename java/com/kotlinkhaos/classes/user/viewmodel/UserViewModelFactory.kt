package com.kotlinkhaos.classes.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for creating instances of UserViewModel.
 * This factory is necessary to inject dependencies into the UserViewModel.
 *
 * @param userTypeDataStore An instance of UserStore to be passed to the UserViewModel.
 */
class UserViewModelFactory(private val userTypeDataStore: UserStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userTypeDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
