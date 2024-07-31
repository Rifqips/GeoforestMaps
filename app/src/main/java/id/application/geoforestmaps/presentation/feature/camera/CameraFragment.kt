package id.application.geoforestmaps.presentation.feature.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import id.application.core.utils.BaseFragment
import id.application.core.utils.proceedWhen
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.DialogConfirmCustomBinding
import id.application.geoforestmaps.databinding.FragmentCameraBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant
import id.application.geoforestmaps.utils.Constant.IMAGE_FORMAT
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
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
    private var idPlant = ""
    private var idBlock = ""
    private var addressText = ""
    private var latitude = 0.0
    private var longitude = 0.0
    private var altitude = 0
    private var savedUri: Uri = Uri.EMPTY
    private var userName = ""
    private var dateTime = ""

    override fun initView() {
        onBackPressed()
        val title = arguments?.getString("title")
        binding.topBar.ivTitle.text = "Ambil Data $title"

        // inisialisasi blok_name
        blokName = title.toString()
        idBlock = arguments?.getInt("ID_BLOCK").toString()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkPermissions()
        initViewModel()
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

    private fun loadingState(state: Boolean) {
        with(binding) {
            when (state) {
                true -> {
                    binding.pbLoadingCamera.isGone = false
                    binding.llPlantsType.isGone = true
                    binding.containerBottom.isGone = true

                }

                false -> {
                    binding.pbLoadingCamera.isGone = true
                    binding.llPlantsType.isGone = false
                    binding.containerBottom.isGone = false

                }
            }
        }
    }

    private fun initViewModel() {
        viewModel.fetchPlants()
        viewModel.getPlant()
        viewModel.plantsLiveData.observe(viewLifecycleOwner) { plants ->
            Log.d("spinner-plants", plants.toString())
            val plantNames = plants.map { it.name }
            val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, plantNames)
            adapter.setDropDownViewResource(R.layout.item_dropdown_spinner)
            with(binding) {
                spinnerPlantTypes.adapter = adapter
                spinnerPlantTypes.dropDownVerticalOffset = 146
                spinnerPlantTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (view != null) {
                            selectedPlantType = plantNames[position]
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.selected_item) + " " + selectedPlantType,
                                Toast.LENGTH_SHORT
                            ).show()
                            idPlant = (plants.find { it.name == selectedPlantType }?.id ?: 0).toString()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            }
        }
        viewModel.getUserName()
        viewModel.isUserName.observe(viewLifecycleOwner){
            userName = it
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.pbLoadingCamera.visibility = View.VISIBLE
            } else {
                binding.pbLoadingCamera.visibility = View.GONE
                showDialogConfirmSaveData()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
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

                val metrics = DisplayMetrics().also { binding.viewFinder.display.getRealMetrics(it) }
                val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
                val targetResolution = Size(1080, 1080) // Resolusi persegi yang diinginkan

                val imageAnalysis = ImageAnalysis.Builder()
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setTargetResolution(targetResolution)
                    .setTargetRotation(rotation)
                    .build()

                val preview = Preview.Builder()
                    .setTargetResolution(targetResolution)
                    .setTargetRotation(rotation)
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder()
                    .setTargetResolution(targetResolution)
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
            } catch (e: Exception) {
                Log.e("CameraFragment", "Error starting camera: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }


    private fun aspectRatio(width: Int, height: Int): Int {
        return AspectRatio.RATIO_4_3 // Default ke rasio 4:3
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = IMAGE_FORMAT
        val chooser =
            Intent.createChooser(intent, resources.getString(R.string.string_choose_a_picture))
        openGalleryLauncher.launch(chooser)
    }

    private fun initGallery() {
        openGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImg: Uri? = result.data?.data
                selectedImg?.let {
                    // inisialisasi datetime
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("id", "ID"))
                    dateTime = dateFormat.format(Date())

                    // add location to image and intent result to checkdata layout
                    addLocationToImageFromGallery(it)
                }
            }
        }
    }

    private fun addLocationToImageFromGallery(imageUri: Uri) {
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
            alpha = 150
        }

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>?

        try {
            currentLocation?.let {
                addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                addressText = if (addresses.isNullOrEmpty()) {
                    "Alamat tidak tersedia"
                } else {
                    "Latitude\t: ${it.latitude}\nLongitude\t: ${it.longitude}\nAltitude\t: ${it.altitude}" +
                            "\nBlock\t: $blokName\nPlant\t: $selectedPlantType\nUser\t: $userName\nDate Time\t: $dateTime"
                }
                latitude = it.latitude
                longitude = it.longitude
                altitude = it.altitude.toInt()
            } ?: run {
                addressText = "Lokasi tidak tersedia"
            }
        } catch (e: IOException) {
            addressText = "Gagal mendapatkan alamat"
        }

        val textBounds = Rect()
        textPaint.getTextBounds(addressText, 0, addressText.length, textBounds)
        val textWidth = textBounds.width()
        val textHeight = textBounds.height()

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

            scaledCanvas.drawRect(
                xPos,
                textY - textPaint.descent() - 8,
                xPos + staticLayout.width,
                textY + staticLayout.height.toFloat() + 8,
                backgroundPaint
            )

            scaledCanvas.translate(xPos, textY)
            staticLayout.draw(scaledCanvas)
        } else {
            scaledCanvas.drawRect(
                xPos,
                yPos - textHeight - 8,
                xPos + textWidth + 16,
                yPos + 8,
                backgroundPaint
            )
            scaledCanvas.drawText(addressText, xPos, yPos, textPaint)
        }

        // Save the new bitmap to a file
        val newFile = File(requireContext().cacheDir, "temp_image.jpg")
        val outputStream = FileOutputStream(newFile)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        // Menggunakan file baru dengan lokasi tambahan
        layoutCheckDataItem(Uri.fromFile(newFile), latitude, longitude, altitude)
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        getCurrentLocation()
        val photoFile = Constant.createFile(requireActivity().application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // inisialisasi datetime
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("id", "ID"))
        dateTime = dateFormat.format(Date())

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    correctImageOrientation(photoFile)
                    saveImageToGallery(savedUri)
                    addLocationToImageFromCamera(savedUri)
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

    private fun addLocationToImageFromCamera(imageUri: Uri) {
        val resolver = requireContext().contentResolver
        val inputStream = resolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (bitmap == null) {
            Toast.makeText(requireContext(), "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
            return
        }
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
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

        try {
            currentLocation?.let {
                addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                addressText = if (addresses.isNullOrEmpty()) {
                    "Alamat tidak tersedia"
                } else {
                    "Latitude\t: ${it.latitude}\nLongitude\t: ${it.longitude}\nAltitude\t: ${it.altitude}" +
                            "\nBlock\t: $blokName\nPlant\t: $selectedPlantType\nUser\t: $userName\nDate Time\t: $dateTime "
                }
                // inisialisasi request data for create geotaging
                latitude = it.latitude
                longitude = it.longitude
                altitude = it.altitude.toInt()
                layoutCheckDataItem(savedUri, latitude, longitude, altitude)

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
            addLocationToImageFromCamera(imageUri)
        }
    }

    private fun stateLayout(stateLayout: Boolean) {
        val layoutCheck = binding.layoutCheckData.root
        val topBarCheck = binding.topBar.root
        with(binding) {
            when (stateLayout) {
                true -> {
                    layoutCheck.isGone = false
                    topBarCheck.isGone = true
                    llPlantsType.isGone = true
                    containerBottom.isGone = true
                }

                false -> {
                    layoutCheck.isGone = true
                    topBarCheck.isGone = false
                    llPlantsType.isGone = false
                    viewFinder.isGone = false
                }
            }
        }
    }

    private fun layoutCheckDataItem(
        imageResult: Uri,
        latitude: Double,
        longitude: Double,
        altitude: Int
    ) {
        stateLayout(true)
        with(binding) {
            llPlantsType.isGone = true
            viewFinder.isGone = true
            containerBottom.isGone = true

            layoutCheckData.topBar.ivTitle.text = "Check Data " + blokName
            layoutCheckData.tvBlok.text = blokName
            layoutCheckData.tvPlantTypes.text = selectedPlantType
            layoutCheckData.ivPlant.load(imageResult) {
                crossfade(true)
            }
            layoutCheckData.btnSaveData.setOnClickListener {
                val imageFile = when (imageResult.scheme) {
                    "file" -> File(imageResult.path!!)
                    "content" -> {
                        // Handle content URI case
                        val inputStream = requireContext().contentResolver.openInputStream(imageResult)
                        val tempFile = File(requireContext().cacheDir, "temp_image.jpg")
                        val outputStream = FileOutputStream(tempFile)
                        inputStream?.copyTo(outputStream)
                        inputStream?.close()
                        outputStream.close()
                        tempFile
                    }
                    else -> null
                }
                if (imageFile != null) {
                    val imageRequestBody = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        imageFile.name,
                        imageRequestBody
                    )
                    viewModel.createGeotaging(
                        idPlant.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        idBlock.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        latitude.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        longitude.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        altitude.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        imageMultipart
                    )
                    Log.d("check-post", "$idPlant $idBlock $latitude $longitude $altitude")
                } else {
                    Toast.makeText(requireContext(), "File tidak dapat diakses", Toast.LENGTH_SHORT).show()
                }
            }
            layoutCheckData.topBar.ivBack.setOnClickListener {
                stateLayout(false)

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showDialogConfirmSaveData() {
        val binding: DialogConfirmCustomBinding =
            DialogConfirmCustomBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext(), 0).create()

        dialog.apply {
            setView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(false)
        }.show()
        with(binding){
            dialogTitle.text = "Input Data Berhasil !"
            tvDialogDesc.text = "Input data sudah berhasil disimpan"
            tvDialogSubDesc.text = "Ambil Foto Lagi ?"
            btnYes.setOnClickListener {
                findNavController().navigateUp()
                dialog.dismiss()
            }

            btnNo.setOnClickListener {
                navigateToHome()
                dialog.dismiss()
            }
            root.setOnTouchListener { _, _ ->
                true
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_cameraFragment_to_homeFragment)
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
    }

}