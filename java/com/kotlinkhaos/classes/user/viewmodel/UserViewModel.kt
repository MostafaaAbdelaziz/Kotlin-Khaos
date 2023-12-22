package com.kotlinkhaos.classes.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.classes.user.UserType
import kotlinx.coroutines.launch

/**
 * ViewModel for managing and storing user details.
 * @param userTypeDataStore An instance of UserStore for data persistence.
 */
class UserViewModel(private val userTypeDataStore: UserStore) : ViewModel() {
    private var _storedUserDetails = MutableLiveData<StoredUserDetails?>()
    val storedUserDetails: LiveData<StoredUserDetails?> = _storedUserDetails

    private var _userDetails = MutableLiveData<StoredUserDetails?>()
    val userDetails: LiveData<StoredUserDetails?> = _userDetails

    /**
     * Loads user details from the User class and updates LiveData.
     * If the user is null, clears stored details.
     */
    fun loadDetails() {
        viewModelScope.launch {
            val user = User.getUser()
            if (user == null) {
                _userDetails.value = null
                _storedUserDetails.value = null;
                userTypeDataStore.clearUserDetails()
                return@launch
            }
            val userDetails = StoredUserDetails(user.getCourseId(), user.getType())
            _userDetails.value = userDetails
            userTypeDataStore.saveUserDetails(userDetails)
        }
    }

    /**
     * Loads user details from the UserStore and updates LiveData.
     */
    fun loadDetailsFromStore() {
        viewModelScope.launch {
            val storedUserDetails = userTypeDataStore.loadUserDetails()
            _storedUserDetails.value = storedUserDetails
        }
    }

    /**
     * Saves the specified course ID and user type to the UserStore and updates LiveData.
     * @param courseId The course ID of the user.
     * @param userType The type of the user (e.g., STUDENT, INSTRUCTOR).
     */
    fun saveDetails(courseId: String, userType: UserType) {
        viewModelScope.launch {
            val userDetails = StoredUserDetails(courseId, userType)
            userTypeDataStore.saveUserDetails(userDetails)
            _storedUserDetails.value = userDetails;
            _userDetails.value = userDetails;
        }
    }

    /**
     * Clears user details from the UserStore and sets LiveData to null.
     */
    fun clear() {
        viewModelScope.launch {
            userTypeDataStore.clearUserDetails()
            _storedUserDetails.value = null;
            _userDetails.value = null;
        }
    }
}
