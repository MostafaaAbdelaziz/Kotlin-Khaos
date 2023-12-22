package com.kotlinkhaos.classes.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinkhaos.classes.user.User
import kotlinx.coroutines.launch

/**
 * ViewModel for handling user avatar related operations.
 */
class UserAvatarViewModel : ViewModel() {
    private var _updatedUserId = ""
    private var _updatedUserAvatarUrl = MutableLiveData<String?>()
    val updatedUserAvatarUrl: LiveData<String?> = _updatedUserAvatarUrl

    private var _userAvatarError = MutableLiveData<Exception>()
    val userAvatarError: LiveData<Exception> = _userAvatarError

    private var _avatarUrl = MutableLiveData<String>()
    val avatarUrl: LiveData<String> = _avatarUrl

    /**
     * Loads the current user's avatar hash and updates the avatar URL LiveData.
     * Sets an error in userAvatarError LiveData if an exception occurs.
     */
    fun loadAvatarHash() {
        viewModelScope.launch {
            try {
                _avatarUrl.value = User.getProfilePicture()
            } catch (err: Exception) {
                _userAvatarError.value = err
            }
        }
    }

    /**
     * Retrieves the updated avatar URL if it matches the specified userId.
     * @param userId The ID of the user whose updated avatar URL is to be retrieved.
     * @return The updated avatar URL or null if the userId does not match.
     */
    fun getUpdatedAvatarUrl(userId: String): String? {
        if (_updatedUserId == userId) {
            return _updatedUserAvatarUrl.value
        }
        return null
    }


    /**
     * Updates the user's avatar URL based on the provided userId and avatarHash.
     * @param userId The ID of the user whose avatar URL is to be updated.
     * @param avatarHash The hash of the new avatar image.
     */
    fun updateUserAvatarUrl(userId: String, avatarHash: String) {
        _updatedUserAvatarUrl.value = User.getProfilePicture(userId, avatarHash)
        _updatedUserId = userId
    }
}
