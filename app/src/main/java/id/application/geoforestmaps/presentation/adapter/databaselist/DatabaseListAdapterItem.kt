package id.application.geoforestmaps.presentation.adapter.databaselist

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.ItemHistoryDataBinding
import id.application.geoforestmaps.utils.Constant.formatDate
import id.application.geoforestmaps.utils.Constant.formatDateTime
import id.application.geoforestmaps.utils.Constant.formatTime
import okhttp3.OkHttpClient
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DatabaseListAdapterItem(
    private val onClickListener: (ItemAllGeotaging) -> Unit,
    private val headerTitle: String,
    private val showHeader: Boolean // Tambahkan parameter untuk kontrol header
) : PagingDataAdapter<ItemAllGeotaging, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1

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

    override fun getItemViewType(position: Int): Int {
        return if (showHeader && position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header_rv, parent, false)
            HeaderViewHolder(view)
        } else {
            val binding = ItemHistoryDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.headerTextView.text = headerTitle
        } else if (holder is ItemViewHolder) {
            val item = getItem(if (showHeader) position - 1 else position)
            item?.let { holder.bindLinear(it) }
        }
    }

    override fun getItemCount(): Int {
        return if (showHeader) super.getItemCount() + 1 else super.getItemCount()
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerTextView: TextView = view.findViewById(R.id.headerTextView)
    }

    inner class ItemViewHolder(
        private val binding: ItemHistoryDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "NewApi")
        fun bindLinear(item: ItemAllGeotaging) {
            with(binding) {
                val client = OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request()
                        val response = chain.proceed(request)
                        response
                    }
                    .build()
                val imageLoader = ImageLoader.Builder(itemView.context)
                    .okHttpClient(client)
                    .build()
                ivItemHistory.load(item.photo, imageLoader) {
                    placeholder(R.drawable.ic_img_loading) // Gambar sementara saat loading
                    error(R.drawable.ic_img_failed) // Gambar saat gagal memuat
                    crossfade(true)
                }
                val (formattedDate, formattedTime) = formatDateTime(item.createdAt)
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

