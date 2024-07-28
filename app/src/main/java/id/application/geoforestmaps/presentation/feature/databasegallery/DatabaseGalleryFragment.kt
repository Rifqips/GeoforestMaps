package id.application.geoforestmaps.presentation.feature.databasegallery

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.DialogSaveDatabaseBinding
import id.application.geoforestmaps.databinding.FragmentDatabaseGalleryBinding
import id.application.geoforestmaps.presentation.adapter.databasegallery.DatabaseGalleryAdapterItem
import id.application.geoforestmaps.presentation.adapter.databaselist.DatabaseListAdapterItem
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant.generateFileName
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DatabaseGalleryFragment :
    BaseFragment<FragmentDatabaseGalleryBinding, VmApplication>(FragmentDatabaseGalleryBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingGeotagging: DatabaseGalleryAdapterItem by lazy {
        DatabaseGalleryAdapterItem {}
    }

    var blockName: String? = ""
    private var activeDialog: AlertDialog? = null

    override fun initView() {
        onBackPressed()
        with(binding){
            topbar.ivTitle.text = "Gallery"
            topbar.ivDownlaod.load(R.drawable.ic_download)
        }
        blockName = arguments?.getString("blockName")
        loadPagingGeotagingAdapter(adapterPagingGeotagging)
        setUpPaging()
    }

    override fun initListener() {
        with(binding){
            topbar.ivBack.setOnClickListener {
                findNavController().navigate(R.id.action_databaseGalleryFragment_to_homeFragment)
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
        viewModel.eksports(type = "photos", block = blockName, file.name, requireContext())
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

    private fun loadPagingGeotagingAdapter(adapter: DatabaseGalleryAdapterItem) {
        if (blockName != null) {
            viewModel.loadPagingGeotaggingGallery(
                adapter,
                blockName,
            )
        }
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
                            tvValidatingData.visibility = View.VISIBLE
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
                    findNavController().navigate(R.id.action_databaseGalleryFragment_to_homeFragment)
                }
            }
        )
    }

}