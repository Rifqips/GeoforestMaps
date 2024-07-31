package id.application.geoforestmaps.presentation.adapter.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.geoforestmaps.databinding.ItemHistoryDataBinding

class AdapterGeotagingLocal(
    private val onClickListener: (ItemAllGeotaging) -> Unit
) : PagingDataAdapter<ItemAllGeotaging, AdapterGeotagingLocal.LinearViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemAllGeotaging>() {
            override fun areItemsTheSame(
                oldItem: ItemAllGeotaging,
                newItem: ItemAllGeotaging
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ItemAllGeotaging,
                newItem: ItemAllGeotaging
            ): Boolean {
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
        fun bindLinear(item: ItemAllGeotaging) {
            with(binding) {
                ivItemHistory.load(item.photo)  // Ensure `item.photo` is a valid URL or resource ID
                val utcFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                val utcDateTime = ZonedDateTime.parse(item.createdAt, utcFormatter)
                val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Jakarta"))
                val localFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val formattedDate = localDateTime.format(localFormatter)
                val formattedTime = localDateTime.format(timeFormatter)

                tvTitleItemHistory.text = item.plant
                tvDescItemHistory.text = item.block
                tvTimeItemHistory.text = formattedTime
                tvDateItemHistory.text = formattedDate
            }
            binding.root.setOnClickListener {
                onClickListener(item)
            }
        }
    }
}
