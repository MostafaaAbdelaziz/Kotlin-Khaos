package com.kotlinkhaos.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.databinding.FragmentForgetPasswordBinding
import kotlinx.coroutines.launch


class ForgetPasswordFragment : Fragment() {
    private var _binding: FragmentForgetPasswordBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //This is to handle the "ForgetPassword" button whenever it is clicked it goes into the
        //function handleForgetPassword().
        binding.forgetPasswordButton.setOnClickListener {
            handleForgetPassword()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * This function is to handle the "ForgetPassword" button whenever it is clicked it goes into the
     * function handleForgetPassword(). This function handles the reset password of the user by asking the
     * users email. Then by using the sendForgotPasswordEmail found in the User class we are able
     * to reset the password of the user and if the user is reseted successfully with no issues or errors then
     * the user is taken to the login page where they are presented with the login page. The email as I
     * have tested multiple times can take a few minutes to be sent to the user, I recieved it in my
     * spam/junk folder.
     */
    private fun handleForgetPassword() {
        lifecycleScope.launch {
            try {
                //This is to get the email from the user.
                val email = binding.inputEmailAddress.text.toString().trim()
                //This is to reset the password of the user. This is to send the email.
                User.sendForgotPasswordEmail(email)
                //This is to show a toast message whenever ti has been resetted successfully and
                //tells the user to check their email.
                Toast.makeText(
                    requireContext(),
                    "Password has been reseted successfully! Please check your email.",
                    Toast.LENGTH_LONG
                ).show()
                //After that it will take the user if everything is successful to the login page.
                handleGoToLogin()
            } catch (err: Exception) {
                if (err is FirebaseAuthError) {
                    //This is to show the error message if there is an error.
                    binding.errorMessage.text = err.message
                    return@launch
                }
                //if theres anythign else that goes wrong it will throw an error.
                throw err
            }
        }

    }

    //This is to take the user back to the login page.
    private fun handleGoToLogin() {
        val action = ForgetPasswordFragmentDirections.actionGoToLogin()
        findNavController().navigate(action)
    }
}