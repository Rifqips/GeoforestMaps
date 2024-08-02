package id.application.geoforestmaps.presentation.feature.databaselist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.view.size
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.DialogSaveDatabaseBinding
import id.application.geoforestmaps.databinding.FragmentDatabaseListBinding
import id.application.geoforestmaps.presentation.adapter.databaselist.DatabaseListAdapterItem
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant.generateFileName
import id.application.geoforestmaps.utils.Constant.isNetworkAvailable
import id.application.geoforestmaps.utils.NetworkCallback
import id.application.geoforestmaps.utils.NetworkChangeReceiver
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class DatabaseListFragment :
    BaseFragment<FragmentDatabaseListBinding, VmApplication>(FragmentDatabaseListBinding::inflate),
    NetworkCallback {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingGeotagging: DatabaseListAdapterItem by lazy {
        DatabaseListAdapterItem {}
    }
    private var activeDialog: AlertDialog? = null

    var blockName: String? = ""

    private val networkChangeReceiver: NetworkChangeReceiver by lazy {
        val refreshDataCallback = {
            if (isAdded && view != null) {
                refreshDataList()
            }
        }
        getKoin().get<NetworkChangeReceiver> { parametersOf(refreshDataCallback) }
    }

    override fun initView() {
        requireActivity().registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        with(binding) {
            topbar.ivTitle.text = "List"
            topbar.ivDownlaod.load(R.drawable.ic_download)
        }
        if (isNetworkAvailable(requireContext())) {
            binding.layoutNoSignal.root.isGone = true
            setUpPaging()
            initVm()
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


    override fun initListener() {
        with(binding) {
            topbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
                clearTrafficPaging()
            }
            topbar.ivDownlaod.setOnClickListener {
                exportFile()
            }
        }
    }

    private fun initVm() {
        viewModel.getBlockName()
        viewModel.isBlockName.observe(viewLifecycleOwner) { blockName ->
            this.blockName = blockName
            loadPagingGeotagingAdapter(adapterPagingGeotagging, blockName)
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
        val fileName = generateFileName("geotaging-$blockName", ".xlsx")
        val file = File(downloadDir, fileName)
        viewModel.eksports(type = "list", block = blockName, null, file.name, requireContext())
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
            when (text) {
                "Download berhasil!" -> {
                    ivSuccess.load(R.drawable.ic_success)
                }

                "Download gagal!" -> {
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

    private fun loadPagingGeotagingAdapter(adapter: DatabaseListAdapterItem, blockName: String) {
        viewModel.loadPagingGeotagging(
            adapter,
            blockName,
        )
    }

    private fun setUpPaging() {
        view?.let { _ ->
            parentFragment?.viewLifecycleOwner?.let { lifecycleOwner ->
                viewModel.geotaggingListAll.observe(lifecycleOwner) { pagingData ->
                    adapterPagingGeotagging.submitData(lifecycle, pagingData)
                }
            }
            binding.rvDatabaseList.apply {
                adapter = adapterPagingGeotagging
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

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
        viewModel.loadingPagingResults.observe(viewLifecycleOwner) { onLoadPaging ->
            when (onLoadPaging) {
                true -> {}
                false -> {
                    if (adapterPagingGeotagging.itemCount == 0 && binding.rvDatabaseList.size == 0) {
                        binding.tvValidatingData.visibility = View.VISIBLE
                        binding.tvValidatingData.text = "Belum ada data"
                    } else {
                        binding.tvValidatingData.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun clearTrafficPaging() {
        viewModel.geotaggingListAll.removeObservers(viewLifecycleOwner)
        adapterPagingGeotagging.submitData(lifecycle, PagingData.empty())
        binding.rvDatabaseList.adapter = null
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
            refreshDataList()
        }
    }
    fun refreshDataList() {
        setUpPaging()
        initVm()
        binding.pbLoading.isGone = true
        binding.layoutNoSignal.root.isGone = true
    }

}
