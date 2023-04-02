package com.example.recorder.ui.MainWindow

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.recorder.R
import com.example.recorder.databinding.FragmentMainWindowBinding
import com.example.recorder.utils.launchAndCollectIn
import com.example.recorder.utils.toast
import okhttp3.WebSocket
import timber.log.Timber


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

        requestPermission()

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


    private fun requestPermission() {
        requestCameraPermissionIfMissing { granted ->
            if (granted)
                startCamera()
            else
                toast(R.string.add_permission_for_camera)
        }
    }

    private fun startCamera() {
        val processCameraProvider = ProcessCameraProvider.getInstance(requireContext())
        processCameraProvider.addListener({
            try {
                val cameraProvider = processCameraProvider.get()
                val previewUseCase = buildPreviewUseCase()

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(requireActivity(), CameraSelector.DEFAULT_BACK_CAMERA, previewUseCase)
            } catch (e: Exception) {
                Timber.tag("Camera").d(e)
            }


        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun buildPreviewUseCase(): Preview {
        return Preview.Builder().build().also { it.setSurfaceProvider(binding.cameraPreview.surfaceProvider) }
    }

    private fun requestCameraPermissionIfMissing(onResult: ((Boolean) -> Unit)) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
            onResult(true)
        else
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                onResult(it)
            }.launch(android.Manifest.permission.CAMERA)
    }
}