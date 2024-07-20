package id.application.geoforestmaps.presentation.feature.checkdata

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.navigation.fragment.findNavController
import coil.load
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.DialogConfirmSaveDataBinding
import id.application.geoforestmaps.databinding.FragmentCheckDataBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant.IMAGE_PARSE
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckDataFragment :
    BaseFragment<FragmentCheckDataBinding, VmApplication>(FragmentCheckDataBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        binding.topBar.ivTitle.text = "Check Data"

        val blok_name = arguments?.getString("BLOK_NAME")
        binding.tvBlok.text = "$blok_name"

        val selectedPlantType = arguments?.getString("SELECTED_PLANT_TYPE")
        binding.tvPlantTypes.text = selectedPlantType

        val imageResult = arguments?.getString(IMAGE_PARSE)

        binding.ivPlant.load(imageResult) {
            crossfade(true)
        }

    }

    override fun initListener() {
        with(binding) {
            topBar.ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnSaveData.setOnClickListener {
                showDialogConfirmSaveData()
            }
        }
    }

    private fun showDialogConfirmSaveData() {
        val binding: DialogConfirmSaveDataBinding = DialogConfirmSaveDataBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext(), 0).create()

        dialog.apply {
            setView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }.show()

        binding.btnFinish.setOnClickListener {
            navigateToHome()
            dialog.dismiss()
        }

        binding.tvTakePictureAgain.setOnClickListener {
            navigateToGeoLocation()
            dialog.dismiss()
        }
    }


    private fun navigateToHome(){
        findNavController().navigate(R.id.action_checkDataFragment_to_homeFragment)
    }

    private fun navigateToGeoLocation(){
        findNavController().navigate(R.id.action_checkDataFragment_to_geotaggingLocationFragment)
    }


}