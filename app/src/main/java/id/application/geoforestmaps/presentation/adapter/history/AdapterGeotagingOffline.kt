package id.application.geoforestmaps.presentation.adapter.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.application.core.domain.model.geotags.ItemAllGeotagingOffline
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.ItemHistoryDataBinding
import id.application.geoforestmaps.presentation.adapter.databaselist.DatabaseListAdapterItem.HeaderViewHolder
import id.application.geoforestmaps.presentation.adapter.databaselist.DatabaseListAdapterItem.ItemViewHolder
import id.application.geoforestmaps.presentation.adapter.history.HistoryListAdapter.ViewHolder
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class AdapterGeotagingOffline(
    private val onClickListener: (ItemAllGeotagingOffline) -> Unit,
    private val headerTitle: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ItemAllGeotagingOffline>(){
        override fun areItemsTheSame(oldItem: ItemAllGeotagingOffline, newItem: ItemAllGeotagingOffline): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: ItemAllGeotagingOffline, newItem: ItemAllGeotagingOffline): Boolean {
            return oldItem == newItem
        }
    })

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header_rv, parent, false)
                HeaderViewHolder(view)
            }
            VIEW_TYPE_ITEM -> {
                val binding = ItemHistoryDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.headerTextView.text = headerTitle
            }
            is ViewHolder -> {
                val adjustedPosition = position - 1 // Adjust for header
                val item = dataDiffer.currentList[adjustedPosition]
                item?.let { holder.bind(it) }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataDiffer.currentList.size + 1 // Add 1 for header
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerTextView: TextView = view.findViewById(R.id.headerTextView)
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
                ivItemHistory.load(R.drawable.img_red_tree) // Replace with `item.photo` if needed
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

    fun setData(data: List<ItemAllGeotagingOffline>) {
        dataDiffer.submitList(data)
        notifyDataSetChanged() // Update the entire dataset including the header
    }
}
