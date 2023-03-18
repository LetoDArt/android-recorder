package com.example.recorder.ui.Loader

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.recorder.R
import com.example.recorder.utils.launchAndCollectIn

class LoaderFragment : Fragment(R.layout.fragment_loader) {

    private val loaderViewModel: LoaderViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = view.context as AppCompatActivity
        app.supportActionBar?.hide()

        loaderViewModel.getUser()

        loaderViewModel.redirectFlow.launchAndCollectIn(viewLifecycleOwner) {
            findNavController().navigate(it)
        }
    }


}