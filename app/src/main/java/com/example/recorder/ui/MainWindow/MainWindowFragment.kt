package com.example.recorder.ui.MainWindow

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.recorder.R
import com.example.recorder.databinding.FragmentMainWindowBinding


class MainWindowFragment : Fragment(R.layout.fragment_main_window) {
    private val binding by viewBinding(FragmentMainWindowBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = view.context as AppCompatActivity
        app.supportActionBar?.show()
    }
}