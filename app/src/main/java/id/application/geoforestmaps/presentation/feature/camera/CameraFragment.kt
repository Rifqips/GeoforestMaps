package id.application.geoforestmaps.presentation.feature.camera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Size
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentCameraBinding
import id.application.geoforestmaps.presentation.viewmodel.VmPreLogin
import id.application.geoforestmaps.utils.Constant
import id.application.geoforestmaps.utils.Constant.IMAGE_FORMAT
import id.application.geoforestmaps.utils.Constant.IMAGE_PARSE
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log

class CameraFragment :
    BaseFragment<FragmentCameraBinding, VmPreLogin>(FragmentCameraBinding::inflate) {

    override val viewModel: VmPreLogin by viewModel()
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var openGalleryLauncher: ActivityResultLauncher<Intent>

    override fun initView() {
        checkPermissions()
    }

    override fun initListener() {
        initGallery()
        with(binding) {
            ivShutter.setOnClickListener{
                takePhoto()
            }
            ivSwitch.setOnClickListener {
                cameraSelector =
                    if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                    else CameraSelector.DEFAULT_BACK_CAMERA
                startCamera()
            }
            ivGallery.setOnClickListener {
                startGallery()
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        try {
            startCamera()
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                // Permission denied
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val imageAnalysis = ImageAnalysis
                    .Builder()
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setTargetResolution(Size(1280, 720))
                    .build()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }
                imageCapture = ImageCapture
                    .Builder()
                    .setTargetResolution(Size(1280, 720)).build()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture, imageAnalysis
                )
                Log.d("CameraFragment", "Camera started")
            } catch (e: Exception) {
                Log.e("CameraFragment", "Error starting camera: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun initGallery() {
        openGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImg: Uri? = result.data?.data
                selectedImg?.let {
                    navigateToHome(it.toString())

                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = IMAGE_FORMAT
        val chooser = Intent.createChooser(intent,resources.getString(R.string.string_choose_a_picture))
        openGalleryLauncher.launch(chooser)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = Constant.createFile(requireActivity().application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {}

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    navigateToHome(savedUri.toString())
                }
            }
        )
    }

    private fun navigateToHome(imageResult : String){
        val bun = Bundle()
        bun.putString(IMAGE_PARSE, imageResult)
        findNavController().navigate(R.id.action_cameraFragment_to_homeFragment, bun)
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
    }

}