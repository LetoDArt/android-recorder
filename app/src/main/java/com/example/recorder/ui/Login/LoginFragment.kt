package com.example.recorder.ui.Login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.recorder.R
import com.example.recorder.databinding.FragmentLoginBinding


class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = view.context as AppCompatActivity
        app.supportActionBar?.hide()

        binding.redirectToSignUpBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.LoginToSignup)
        }
    }
}
