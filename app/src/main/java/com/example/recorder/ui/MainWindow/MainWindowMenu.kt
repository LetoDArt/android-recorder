package com.example.recorder.ui.MainWindow

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import com.example.recorder.R

class MainWindowMenu(val mainView: MainWindowViewModel): MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.options, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.logout_button -> {
                mainView.logout()
                true
            }
            else -> false
        }
    }
}