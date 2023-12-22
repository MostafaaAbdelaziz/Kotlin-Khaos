package com.kotlinkhaos.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kotlinkhaos.MainActivity
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.classes.user.viewmodel.UserStore
import com.kotlinkhaos.classes.user.viewmodel.UserViewModel
import com.kotlinkhaos.classes.user.viewmodel.UserViewModelFactory
import com.kotlinkhaos.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //This is to handle the "login" button whenever it is clicked it goes into the
        //function handleLogin().
        binding.loginButton.setOnClickListener {
            handleLogin()
        }
        //This is to handle the "register" button whenever it is clicked it goes into the
        //function handleGoToRegister().
        binding.goToRegister.setOnClickListener {
            handleGoToRegister()
        }
        //This is to handle the "forget password" button whenever it is clicked it goes into the
        //function handleGoToForgetPassword().
        binding.goToForgetPassword.setOnClickListener {
            handleGoToForgetPassword()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * This function is called when the user clicks on the "login" button. It will grab the users
     * email and password from the input fields and then call the login function from the User.kt
     * file. If the login is successful then it will take the user to the MainActivity.kt file, which
     * is the home page of the application. Of course there's more than one page in the application.
     * If the user is not authenticated successfully, then it will display an error message from
     * FirebaseAuthError, where it will show the user the error message.
     */
    private fun handleLogin() {
        lifecycleScope.launch {
            try {
                //This is to grab the users email and password from the input fields.
                val email = binding.inputEmailAddress.text.toString().trim()
                val password = binding.inputPassword.text.toString().trim()
                //This is to call the login function from the User.kt file.
                val user = User.login(userViewModel, email, password)
                if (user != null) {
                    //If everything is authenticated "Successfully", it will take the user to the MainActivity.kt
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
                //else it willl throw an error. if there's an error.
                throw err;
            }

        }
    }

    //This is to handle the "register" button whenever it is clicked so it can go to the RegisterFragment.kt
    private fun handleGoToRegister() {
        val action = LoginFragmentDirections.actionGoToRegister()
        findNavController().navigate(action)
    }

    //This is to handle the "forget password" button whenever it is clicked so it can go to the ForgetPasswordFragment.kt
    private fun handleGoToForgetPassword() {
        val action = LoginFragmentDirections.actionGoToForgetPassword()
        findNavController().navigate(action)
    }
}