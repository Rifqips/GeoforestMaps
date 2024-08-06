package id.application.geoforestmaps.presentation.adapter.databaselist

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
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
    private val onClickListener: (ItemAllGeotaging) -> Unit
) : PagingDataAdapter<ItemAllGeotaging, DatabaseListAdapterItem.LinearViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemAllGeotaging>() {
            override fun areItemsTheSame(
                oldItem: ItemAllGeotaging,
                newItem: ItemAllGeotaging
            ): Boolean {
                // Assuming `id` uniquely identifies each item
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ItemAllGeotaging,
                newItem: ItemAllGeotaging
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
