package com.kotlinkhaos.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.kotlinkhaos.MainActivity
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.classes.user.UserType
import com.kotlinkhaos.classes.user.viewmodel.UserStore
import com.kotlinkhaos.classes.user.viewmodel.UserViewModel
import com.kotlinkhaos.classes.user.viewmodel.UserViewModelFactory
import com.kotlinkhaos.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserStore(requireContext()))
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //This is to handle the "register" button whenever it is clicked it goes into the
        //function handleRegister().
        binding.registerButton.setOnClickListener {
            handleRegister()
        }
        return root
    }

    /**
     * This function is to handle the "register" button whenever it is clicked it goes into the
     * function handleRegister(). This function handles the registration of the user by asking the
     * users name, email and password. It also asks the user whether they are a student or an
     * instructor by using userType as we have initialized it to be a student by default if it is not
     * toggled by the user manually. Then by using the register found in the User class we are able
     * to register the user and if the user is registered successfully with no issues or errors then
     * the user is taken to the main activity where they are presented with the home page of either
     * the student or the instructor (depending on what they chose to be).
     */
    private fun handleRegister() {
        lifecycleScope.launch {
            try {
                //This is to get the name, email and password from the user.
                val name = binding.inputName.text.toString().trim()
                val email = binding.inputEmailAddress.text.toString().trim()
                val password = binding.inputPassword.text.toString().trim()

                /*This is a toggle of switch choice where we are used Switch Compat in the XML,
                where if the user is a student then the userType is set to be a student and if
                the user is an instructor then the userType is set to be an instructor.
                Reference: https://developer.android.com/reference/androidx/appcompat/widget/SwitchCompat*/
                val userType = if (!binding.switchChoice.isChecked) {
                    UserType.STUDENT
                } else {
                    UserType.INSTRUCTOR
                }
                //This is to register the user from the User.kt file.
                val user = User.register(userViewModel, email, password, name, userType)
                if (user != null) {
                    //This is to take the user to the main activity if the user is registered successfully.
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            } catch (err: Exception) {
                if (err is FirebaseAuthError) {
                    //If the user is not authenticated successfully, then it will display an error message from
                    //FirebaseAuthError, where it will show the user the error message.
                    binding.errorMessage.text = err.message
                    return@launch
                }
                //If there is an error then it will throw an error.
                throw err
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}