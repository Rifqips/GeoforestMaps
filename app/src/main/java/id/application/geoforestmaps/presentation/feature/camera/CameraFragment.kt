package id.application.geoforestmaps.presentation.feature.camera

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import id.application.core.utils.BaseFragment
import id.application.core.utils.proceedWhen
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentCameraBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant
import id.application.geoforestmaps.utils.Constant.IMAGE_FORMAT
import id.application.geoforestmaps.utils.Constant.IMAGE_PARSE
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

class CameraFragment :
    BaseFragment<FragmentCameraBinding, VmApplication>(FragmentCameraBinding::inflate) {

    override val viewModel: VmApplication by viewModel()
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var openGalleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private var selectedPlantType = ""
    private var blokName = ""
    private var plantTypes = mutableListOf<String>()

    override fun initView() {
        val title = arguments?.getString("title")
        binding.topBar.ivTitle.text = "Ambil Data $title"

        // inisialisasi blok_name
        blokName = title.toString()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkPermissions()
        plantsTypeSpinner()
    }

    override fun initListener() {
        initGallery()
        with(binding) {
            ivShutter.setOnClickListener {
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
            topBar.ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        try {
            startCamera()
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
    }

    private fun plantsTypeSpinner() {
        viewModel.getPlant()
        viewModel.plantsResult.observe(viewLifecycleOwner) { result ->
            result.proceedWhen(
                doOnLoading = {},
                doOnSuccess = {
                    val data = it.payload?.data?.items
                    data?.let { items ->
                        plantTypes.clear()
                        plantTypes.addAll(items.map { item -> item.name })
                        val adapter =
                            ArrayAdapter(requireContext(), R.layout.item_spinner, plantTypes)
                        adapter.setDropDownViewResource(R.layout.item_dropdown_spinner)
                        with(binding) {
                            spinnerPlantTypes.adapter = adapter
                            spinnerPlantTypes.dropDownVerticalOffset = 146
                            spinnerPlantTypes.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        if (view != null) {
                                            selectedPlantType = plantTypes[position]

                                            Toast.makeText(
                                                requireContext(),
                                                getString(R.string.selected_item) + " " + plantTypes[position],
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {}
                                }
                        }
                    }
                }
            )
        }
    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), CAMERA_PERMISSION_CODE
            )
        } else {
            startCamera()
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startCamera()
                getCurrentLocation()
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Permissions not granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    currentLocation = location
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT)
                        .show()
                }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Izin lokasi tidak diberikan", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val rotation = binding.viewFinder.display.rotation

                val metrics =
                    DisplayMetrics().also { binding.viewFinder.display.getRealMetrics(it) }
                val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

                val imageAnalysis = ImageAnalysis.Builder()
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setTargetAspectRatio(screenAspectRatio)
                    .setTargetRotation(rotation)
                    .build()

                val preview = Preview.Builder()
                    .setTargetAspectRatio(screenAspectRatio)
                    .setTargetRotation(rotation)
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder()
                    .setTargetAspectRatio(screenAspectRatio)
                    .setTargetRotation(rotation)
                    .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
                Log.d("CameraFragment", "Camera started")
            } catch (e: Exception) {
                Log.e("CameraFragment", "Error starting camera: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = width.coerceAtLeast(height).toDouble() / Math.min(width, height)
        if (Math.abs(previewRatio - 4.0 / 4.0) <= Math.abs(previewRatio - 20.0 / 20.0)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun initGallery() {
        openGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImg: Uri? = result.data?.data
                selectedImg?.let {
                    navigateToCheckData(it.toString(), selectedPlantType, blokName)

                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = IMAGE_FORMAT
        val chooser =
            Intent.createChooser(intent, resources.getString(R.string.string_choose_a_picture))
        openGalleryLauncher.launch(chooser)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        getCurrentLocation()
        val photoFile = Constant.createFile(requireActivity().application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    correctImageOrientation(photoFile)
                    saveImageToGallery(savedUri)
                    addLocationToImage(savedUri)
                    navigateToCheckData(savedUri.toString(), selectedPlantType, blokName)
                }
            }
        )
    }

    private fun correctImageOrientation(photoFile: File) {
        try {
            val exif = ExifInterface(photoFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val matrix = Matrix()

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }
            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            val rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            FileOutputStream(photoFile).use { out ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: IOException) {
            Log.e("CameraFragment", "Failed to correct image orientation: ${e.message}", e)
        }
    }

    private fun addLocationToImage(imageUri: Uri) {
        val resolver = requireContext().contentResolver
        val inputStream = resolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (bitmap == null) {
            Toast.makeText(requireContext(), "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
            return
        }
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(newBitmap)
        val textPaint = TextPaint().apply {
            color = ContextCompat.getColor(requireContext(), R.color.white)
            textSize = 38f
            isAntiAlias = true
        }

        val backgroundPaint = Paint().apply {
            color = ContextCompat.getColor(requireContext(), R.color.black)
            alpha = 150 // Adjust the alpha to make the background semi-transparent
        }

        // Mendapatkan alamat
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>?
        var addressText = ""

        try {
            currentLocation?.let {
                addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                addressText = if (addresses.isNullOrEmpty()) {
                    "Alamat tidak tersedia"
                } else {
                    addresses[0].getAddressLine(0)
                }
            } ?: run {
                addressText = "Lokasi tidak tersedia"
            }
        } catch (e: IOException) {
            addressText = "Gagal mendapatkan alamat"
        }

        // Mengukur lebar dan tinggi teks
        val textBounds = Rect()
        textPaint.getTextBounds(addressText, 0, addressText.length, textBounds)
        val textWidth = textBounds.width()
        val textHeight = textBounds.height()

        // Menggunakan Matrix untuk skala gambar
        val imageView = binding.viewFinder
        val imageViewWidth = imageView.width
        val imageViewHeight = imageView.height

        val scaleWidth = imageViewWidth.toFloat() / newBitmap.width
        val scaleHeight = imageViewHeight.toFloat() / newBitmap.height

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        val scaledBitmap =
            Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.width, newBitmap.height, matrix, true)
        val scaledCanvas = Canvas(scaledBitmap)

        val xPos = 26f
        val yPos = scaledBitmap.height - textHeight - 30f

        // Multiple lines
        if (textWidth > scaledBitmap.width - 2 * xPos) {
            val staticLayout = StaticLayout.Builder.obtain(
                addressText,
                0,
                addressText.length,
                textPaint,
                scaledBitmap.width - 2 * xPos.toInt()
            ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()

            val staticLayoutHeight = staticLayout.height
            val textY = scaledBitmap.height - staticLayoutHeight - 30f

            // Menggambar latar belakang hitam sebelum menggambar teks
            scaledCanvas.drawRect(
                xPos,
                textY - textPaint.descent() - 8, // Adjust the padding as needed
                xPos + staticLayout.width,
                textY + staticLayout.height.toFloat() + 8, // Adjust the padding as needed
                backgroundPaint
            )

            scaledCanvas.translate(xPos, textY)
            staticLayout.draw(scaledCanvas)
        } else {
            // Menggambar latar belakang hitam sebelum menggambar teks
            scaledCanvas.drawRect(
                xPos,
                yPos - textHeight - 8, // Adjust the padding as needed
                xPos + textWidth + 16, // Adjust the padding as needed
                yPos + 8, // Adjust the padding as needed
                backgroundPaint
            )
            scaledCanvas.drawText(addressText, xPos, yPos, textPaint)
        }

        resolver.openOutputStream(imageUri)?.let { outputStream ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
        }

        // Set the scaled bitmap to the ImageView

    }


    private fun saveImageToGallery(savedUri: Uri) {
        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                val inputStream = requireContext().contentResolver.openInputStream(savedUri)
                inputStream?.use { input ->
                    input.copyTo(outputStream)

                }
            }
            addLocationToImage(imageUri)
        }
    }

    private fun navigateToCheckData(
        imageResult: String,
        selectedPlantType: String,
        blokName: String
    ) {
        val bun = Bundle()
        bun.putString("BLOK_NAME", blokName)
        bun.putString("SELECTED_PLANT_TYPE", selectedPlantType)
        bun.putString(IMAGE_PARSE, imageResult)
        findNavController().navigate(R.id.action_cameraFragment_to_checkDataFragment, bun)
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
    }

}