package id.application.geoforestmaps.presentation.adapter.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.application.core.domain.model.geotags.ItemAllGeotagingOffline
import id.application.geoforestmaps.databinding.ItemHistoryDataBinding
import id.application.geoforestmaps.presentation.adapter.history.HistoryListAdapter.ViewHolder
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class AdapterGeotagingOffline(
    private val onClickListener: (ItemAllGeotagingOffline) -> Unit
) : RecyclerView.Adapter<AdapterGeotagingOffline.ViewHolder>() {

    private val dataDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ItemAllGeotagingOffline>(){
        override fun areItemsTheSame(oldItem: ItemAllGeotagingOffline, newItem: ItemAllGeotagingOffline): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: ItemAllGeotagingOffline, newItem: ItemAllGeotagingOffline): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterGeotagingOffline.ViewHolder, position: Int) {
        holder.bind(dataDiffer.currentList[position])
    }

    inner class ViewHolder(
        private val binding: ItemHistoryDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "NewApi")
        fun bind(item: ItemAllGeotagingOffline) {
            with(binding) {
                ivItemHistory.load(item.base64)  // Ensure `item.photo` is a valid URL or resource ID
                val instant = Instant.ofEpochMilli(item.createdAt)
                val utcDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"))
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

    override fun getItemCount(): Int {
        return dataDiffer.currentList.size
    }

    fun setData(data : List<ItemAllGeotagingOffline>){
        dataDiffer.submitList(data)
        notifyItemRangeChanged(0,data.size)
    }
}
