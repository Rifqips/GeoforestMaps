package id.application.geoforestmaps.presentation.feature.databasegallery

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Environment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.DialogSaveDatabaseBinding
import id.application.geoforestmaps.databinding.FragmentDatabaseGalleryBinding
import id.application.geoforestmaps.presentation.adapter.databasegallery.DatabaseGalleryAdapterItem
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant.formatDateTime
import id.application.geoforestmaps.utils.Constant.generateFileName
import id.application.geoforestmaps.utils.Constant.isNetworkAvailable
import id.application.geoforestmaps.utils.Constant.showDialogDetail
import id.application.geoforestmaps.utils.NetworkCallback
import id.application.geoforestmaps.utils.NetworkChangeReceiver
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
class DatabaseGalleryFragment :
    BaseFragment<FragmentDatabaseGalleryBinding, VmApplication>(FragmentDatabaseGalleryBinding::inflate),
    NetworkCallback {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingGeotagging: DatabaseGalleryAdapterItem by lazy {
        DatabaseGalleryAdapterItem(
            {
                val (formattedDate, formattedTime) = formatDateTime(it.createdAt)
                showDialogDetail(
                    layoutInflater = layoutInflater,
                    context = requireContext(),
                    gallery = it.photo,
                    itemTitle = it.plant,
                    itemDescription = it.block,
                    createdBy = "Dibuat oleh : ${it.user}",
                    tvDateTime = formattedDate,
                    tvTimeItem = formattedTime
                )
            },
            { itemDownload -> exportFileSingle(itemDownload.id) }
        )
    }

    private val networkChangeReceiver: NetworkChangeReceiver by lazy {
        val refreshDataCallback = {
            if (isAdded && view != null) {
                refreshDataGallery()
            }
        }
        getKoin().get<NetworkChangeReceiver> { parametersOf(refreshDataCallback) }
    }

    private var activeDialog: AlertDialog? = null
    var blockName = ""

    override fun initView() {
        requireActivity().registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        with(binding){
            topbar.ivTitle.text = "Gallery"
            topbar.ivDownlaod.load(R.drawable.ic_download)
        }
        if (isNetworkAvailable(requireContext())) {
            setUpPaging()
            binding.layoutNoSignal.root.isGone = true
            lifecycleScope.launch {
                delay(2000)
                initVm()
            }
            onBackPressed()
        } else {
            binding.pbLoading.isGone = true
            binding.layoutNoSignal.root.isGone = false
            StyleableToast.makeText(
                requireContext(),
                getString(R.string.text_no_internet_connection),
                R.style.failedtoast
            ).show()
        }
    }

    private fun initVm() {
        viewModel.getBlockName()
        viewModel.isBlockName.observe(viewLifecycleOwner) { blockName ->
            this.blockName = blockName
            loadPagingGeotagingAdapter(adapterPagingGeotagging, blockName)
        }
    }

    override fun initListener() {
        with(binding){
            topbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
                clearTrafficPaging()
            }
            topbar.ivDownlaod.setOnClickListener {
                exportFile()
            }
        }
    }



    private fun exportFile() {
        viewModel.downloadStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                "Mendownload File..." -> {
                    showDialogConfirmSaveData(true, status)
                }

                "Download berhasil!" -> {
                    showDialogConfirmSaveData(false, status)
                }

                "Download gagal!" -> {
                    showDialogConfirmSaveData(false, status)
                }
            }
        })
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }
        val fileName = generateFileName("geotaging-$blockName", ".zip")
        val file = File(downloadDir, fileName)
        viewModel.eksports(type = "photos", block = blockName, null, file.name, requireContext())
    }

    private fun exportFileSingle(geotagId: Int) {
        viewModel.downloadStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                "Mendownload File..." -> {
                    showDialogConfirmSaveData(true, status)
                }
                "Download berhasil!" -> {
                    showDialogConfirmSaveData(false, status)
                }

                "Download gagal!" -> {
                    showDialogConfirmSaveData(false, status)
                }
            }
        })
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }
        val fileName = generateFileName("geotaging-$blockName", ".jpeg")
        val file = File(downloadDir, fileName)
        viewModel.eksports(type = "photo", block = blockName, geotagId, file.name, requireContext())
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun showDialogConfirmSaveData(state: Boolean, text: String) {
        activeDialog?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
        val binding: DialogSaveDatabaseBinding = DialogSaveDatabaseBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext(), 0).create()
        dialog.apply {
            setView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(false)
        }.show()
        activeDialog = dialog


        binding.dialogTitle.text = text
        with(binding) {
            when(text){
                "Download berhasil!"->{
                    ivSuccess.load(R.drawable.ic_success)
                }
                "Download gagal!"->{
                    ivSuccess.load(R.drawable.ic_download_failed)
                }
            }
            when (state) {
                true -> {
                    pBar.visibility = View.VISIBLE
                    ivSuccess.visibility = View.GONE
                    ivClose.visibility = View.GONE
                }
                false -> {
                    pBar.visibility = View.GONE
                    ivSuccess.visibility = View.VISIBLE
                    ivClose.visibility = View.VISIBLE
                }
            }
            ivClose.setOnClickListener {
                dialog.dismiss()
                activeDialog = null
            }
        }
        binding.root.setOnTouchListener { _, _ ->
            true
        }
    }

    private fun loadPagingGeotagingAdapter(adapter: DatabaseGalleryAdapterItem, blockName :String) {
        viewModel.loadPagingGeotaggingGallery(
            adapter,
            blockName,
        )
    }

    private fun setUpPaging() {
        view?.let { _ ->
            parentFragment?.viewLifecycleOwner?.let { lifecycleOwner ->
                viewModel.geotaggingList.observe(lifecycleOwner) { pagingData ->
                    adapterPagingGeotagging.submitData(lifecycle, pagingData)
                }
            }

            // Setup RecyclerView layout manager and adapter if not already set
            if (binding.rvDatabaseGallery.adapter == null) {
                binding.rvDatabaseGallery.apply {
                    adapter = adapterPagingGeotagging
                    layoutManager = GridLayoutManager(context, 2)
                }
            }

            // Add load state listener to adapter
            adapterPagingGeotagging.addLoadStateListener { loadState ->
                with(binding) {
                    when (loadState.refresh) {
                        is LoadState.Loading -> {
                            pbLoading.visibility = View.VISIBLE
                            topbar.ivDownlaod.visibility = View.GONE
                        }
                        is LoadState.NotLoading -> {
                            pbLoading.visibility = View.GONE
                            topbar.ivDownlaod.visibility = View.VISIBLE
                        }
                        is LoadState.Error -> {
                            pbLoading.visibility = View.GONE
                            topbar.ivDownlaod.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        // Observe loading state
        viewModel.loadingPagingResults.observe(viewLifecycleOwner) { onLoadPaging ->
            when (onLoadPaging) {
                true -> {}
                false -> {
                    if (adapterPagingGeotagging.itemCount == 0) {
                        binding.tvValidatingData.visibility = View.VISIBLE
                        binding.tvValidatingData.text = "Belum ada data"
                    } else {
                        binding.tvValidatingData.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun clearTrafficPaging(){
        viewModel.geotaggingList.removeObservers(viewLifecycleOwner)
        adapterPagingGeotagging.submitData(lifecycle, PagingData.empty())
        binding.rvDatabaseGallery.adapter = null
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    clearTrafficPaging()
                    findNavController().popBackStack()
                }
            }
        )
    }

    override fun onNetworkAvailable() {
        if (isAdded && view != null) {
            refreshDataGallery()
        }
    }
    fun refreshDataGallery() {
        setUpPaging()
        initVm()
        binding.pbLoading.isGone = true
        binding.layoutNoSignal.root.isGone = true
    }
}