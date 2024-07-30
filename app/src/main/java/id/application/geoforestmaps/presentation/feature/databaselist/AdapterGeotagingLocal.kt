package id.application.geoforestmaps.presentation.feature.databaselist


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.application.core.data.network.model.geotags.AllGeotaging
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.core.utils.ResultWrapper
import id.application.geoforestmaps.databinding.ItemHistoryDataBinding
import id.application.geoforestmaps.utils.Constant.formatDate
import id.application.geoforestmaps.utils.Constant.formatTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class AdapterGeotagingLocal(
    private val onClickListener: (AllGeotaging) -> Unit
) : PagingDataAdapter<AllGeotaging, AdapterGeotagingLocal.LinearViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AllGeotaging>() {
            override fun areItemsTheSame(
                oldItem: AllGeotaging,
                newItem: AllGeotaging
            ): Boolean {
                // Assuming `id` uniquely identifies each item
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: AllGeotaging,
                newItem: AllGeotaging
            ): Boolean {
                // Check if the content of items is the same
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: LinearViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bindLinear(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinearViewHolder {
        val binding = ItemHistoryDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinearViewHolder(binding)
    }

    inner class LinearViewHolder(
        private val binding: ItemHistoryDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "NewApi")
        fun bindLinear(item: AllGeotaging) {
            with(binding) {
                ivItemHistory.load(item.photo)  // Ensure `item.photo` is a valid URL or resource ID
                val dateString = item.createdAt
                val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                val dateTime = ZonedDateTime.parse(dateString, formatter)
                val formattedDate = dateTime.formatDate()
                val formattedTime = dateTime.formatTime()
                tvTitleItemHistory.text = item.plant
                tvDescItemHistory.text = item.block
                tvTimeItemHistory.text = formattedTime.toString()
                tvDateItemHistory.text = formattedDate.toString()
            }
            binding.root.setOnClickListener {
                onClickListener(item)
            }
        }
    }
}
