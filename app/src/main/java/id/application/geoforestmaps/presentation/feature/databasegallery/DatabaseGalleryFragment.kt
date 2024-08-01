package id.application.geoforestmaps.presentation.feature.databasegallery

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.DialogSaveDatabaseBinding
import id.application.geoforestmaps.databinding.FragmentDatabaseGalleryBinding
import id.application.geoforestmaps.presentation.adapter.databasegallery.DatabaseGalleryAdapterItem
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant.generateFileName
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DatabaseGalleryFragment :
    BaseFragment<FragmentDatabaseGalleryBinding, VmApplication>(FragmentDatabaseGalleryBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingGeotagging: DatabaseGalleryAdapterItem by lazy {
        DatabaseGalleryAdapterItem(
            { _ -> },
            { itemDownload -> exportFileSingle(itemDownload.id) }
        )
    }

    private var activeDialog: AlertDialog? = null
    var blockName = ""

    override fun initView() {
        initVm()
        with(binding){
            topbar.ivTitle.text = "Gallery"
            topbar.ivDownlaod.load(R.drawable.ic_download)
        }
        onBackPressed()
        setUpPaging()
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
        if (view != null) {
            parentFragment?.viewLifecycleOwner?.let {
                viewModel.geotaggingListAll.observe(it) { pagingData ->
                    adapterPagingGeotagging.submitData(lifecycle, pagingData)
                }
            }

            adapterPagingGeotagging.addLoadStateListener { loadState ->
                with(binding) {
                    if (loadState.refresh is LoadState.Loading) {
                        pbLoading.visibility = View.VISIBLE
                        topbar.ivDownlaod.visibility = View.GONE
                    } else {
                        pbLoading.visibility = View.GONE
                        topbar.ivDownlaod.visibility = View.VISIBLE
                        val isEmpty = (loadState.refresh is LoadState.NotLoading &&
                                adapterPagingGeotagging.itemCount == 0)
                        if (isEmpty) {
                            tvValidatingData.isGone = false
                            tvValidatingData.text = "Belum ada data"
                        }

                        if (view != null) {
                            rvDatabaseGallery.apply {
                                layoutManager = GridLayoutManager(
                                    context,
                                    2,
                                    GridLayoutManager.VERTICAL,
                                    false
                                ).apply {
                                    isSmoothScrollbarEnabled = true
                                }
                                adapter = adapterPagingGeotagging
                            }
                        }
                    }
                }
            }
        }
    }
    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
    }

}