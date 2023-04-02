package com.example.recorder.ui.MainWindow

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.recorder.R
import com.example.recorder.databinding.FragmentMainWindowBinding
import com.example.recorder.utils.launchAndCollectIn
import okhttp3.WebSocket


class MainWindowFragment : Fragment(R.layout.fragment_main_window) {

    private val mainWindowViewModel: MainWindowViewModel by viewModels()
    private val socketViewModel: SocketViewModel by viewModels()
    private val binding by viewBinding(FragmentMainWindowBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainWindowViewModel.getUserFromStore()

        val menuHost: MenuHost = requireActivity()
        val app = view.context as AppCompatActivity
        app.supportActionBar?.show()

        val ws: WebSocket = mainWindowViewModel.connectSocket(socketViewModel)

        menuHost.addMenuProvider(
            MainWindowMenu(mainWindowViewModel),
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        mainWindowViewModel.userFlow.launchAndCollectIn(viewLifecycleOwner) { user ->
            user?.user_name.let {
                app.supportActionBar?.setTitle(it)
            }
        }

        mainWindowViewModel.navigateFlow.launchAndCollectIn(viewLifecycleOwner) { fragment ->
            findNavController().navigate(fragment)
        }

        binding.sendNameBtn.setOnClickListener {
            val message = "${mainWindowViewModel.obtainUser()}: ${binding.nameInput.text}"
            ws.send("{ \"message\": \"${message}\" }")
        }

        socketViewModel.messageFlow.launchAndCollectIn(viewLifecycleOwner) { message ->
            binding.textView2.text = message
        }
    }
}