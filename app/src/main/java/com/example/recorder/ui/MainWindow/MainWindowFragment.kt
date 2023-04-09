package com.example.recorder.ui.MainWindow

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.*
import android.provider.MediaStore
import android.util.Size
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCaptureException
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
import com.example.recorder.utils.convertToMessage
import com.example.recorder.utils.launchAndCollectIn
import com.example.recorder.utils.toast
import okhttp3.WebSocket
import okio.ByteString.Companion.toByteString
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executor


class MainWindowFragment : Fragment(R.layout.fragment_main_window) {

    private val mainWindowViewModel: MainWindowViewModel by viewModels()
    private val socketViewModel: SocketViewModel by viewModels()
    private val binding by viewBinding(FragmentMainWindowBinding::bind)

    private val handler = Handler(Looper.getMainLooper())

    private var startAlgorithm: Boolean = false

    private lateinit var ws: WebSocket

    private lateinit var context: Context
    private lateinit var cameraExecutor: Executor
    private lateinit var imageCapture: ImageCapture

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context = requireContext()

        mainWindowViewModel.getUserFromStore()
        cameraExecutor = ContextCompat.getMainExecutor(context)

        val menuHost: MenuHost = requireActivity()
        val app = view.context as AppCompatActivity
        app.supportActionBar?.show()

        ws = mainWindowViewModel.connectSocket(socketViewModel)

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

        binding.streamBtn.setOnClickListener {
            activateAlgorithm()
        }

        socketViewModel.messageFlow.launchAndCollectIn(viewLifecycleOwner) { message ->
            binding.statusView.text = message
        }

        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    private val runnable = object : Runnable {
        override fun run() {
            if (startAlgorithm) {
                takePhoto()
            }

            handler.postDelayed(this, 1000)
        }
    }

    private fun activateAlgorithm() {
        startAlgorithm = !startAlgorithm
        if (startAlgorithm) {
            ws.send("Processing".convertToMessage())
            binding.streamBtn.text = getString(R.string.stream_stop_button)
        } else {
            ws.send("Stopped".convertToMessage())
            binding.streamBtn.text = getString(R.string.stream_start_button)
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
        val processCameraProvider = ProcessCameraProvider.getInstance(context)
        processCameraProvider.addListener({

            try {
                val cameraProvider = processCameraProvider.get()
                val previewUseCase = buildPreviewUseCase()

                cameraProvider.unbindAll()

                imageCapture = ImageCapture
                    .Builder()
                    .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetResolution(Size(480, 640))
                    .build()

                cameraProvider.bindToLifecycle(requireActivity(), CameraSelector.DEFAULT_BACK_CAMERA, previewUseCase, imageCapture)
            } catch (e: Exception) {
                Timber.tag("Camera").d(e)
            }


        }, cameraExecutor)
    }

    private fun buildPreviewUseCase(): Preview {
        return Preview.Builder().build().also { it.setSurfaceProvider(binding.cameraPreview.surfaceProvider) }
    }

    private fun requestCameraPermissionIfMissing(onResult: ((Boolean) -> Unit)) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
            onResult(true)
        else
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                onResult(it)
            }.launch(android.Manifest.permission.CAMERA)
    }

    private fun takePhoto() {
        val content = requireActivity().contentResolver

        val name = generateFilename()
        val folderName = getString(R.string.folder_name)

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Pictures/${folderName}")
            }
        }

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            content,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                @SuppressLint("RestrictedApi")
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = outputFileResults.savedUri
                        ?: throw RuntimeException(getString(R.string.no_output_error))

                    val inputStream = content.openInputStream(uri)
                        ?: throw RuntimeException(getString(R.string.no_input_error))

                    val bytes = inputStream.readBytes()
                    inputStream.close()

                    Timber.tag("Attempt").d(name)
                    ws.send(bytes.toByteString())
                    deleteImage(folderName, name)
                }

                override fun onError(exception: ImageCaptureException) {}
            }
        )
    }

    private fun deleteImage(folderName: String, imageName: String) {
        val fileDir = getPath(folderName)
        val file = File(fileDir, imageName)

        if (file.exists()) {
            file.delete()
        }
    }

    private fun getPath(folderName: String): String {
        val picFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return "${picFolder}/${folderName}"
    }

    private fun generateFilename(): String {
        return "${System.currentTimeMillis()}.jpg"
    }
}
