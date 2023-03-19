package com.example.recorder.ui.MainWindow

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
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

        val menuHost: MenuHost = requireActivity()
        val app = view.context as AppCompatActivity
        app.supportActionBar?.show()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logout_button -> {
                        mainWindowViewModel.logout()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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