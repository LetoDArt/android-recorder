package com.example.recorder.ui.MainWindow

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.recorder.R
import com.example.recorder.databinding.FragmentMainWindowBinding
import com.example.recorder.utils.launchAndCollectIn


class MainWindowFragment : Fragment(R.layout.fragment_main_window) {

    private val mainWindowViewModel: MainWindowViewModel by viewModels()
    private val binding by viewBinding(FragmentMainWindowBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainWindowViewModel.getUserFromStore()

        val app = view.context as AppCompatActivity
        app.supportActionBar?.show()

        binding.button.setOnClickListener {
            mainWindowViewModel.logout()
        }

        mainWindowViewModel.userFlow.launchAndCollectIn(viewLifecycleOwner) { user ->
            user?.user_name.let {
                app.supportActionBar?.setTitle(it)
            }
        }

        mainWindowViewModel.navigateFlow.launchAndCollectIn(viewLifecycleOwner) { fragment ->
            findNavController().navigate(fragment)
        }
    }
}