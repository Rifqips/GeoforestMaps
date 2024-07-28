package id.application.geoforestmaps.presentation.adapter.databasegallery

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.geoforestmaps.databinding.ItemDatabaseGalleryBinding
import id.application.geoforestmaps.databinding.ItemHistoryDataBinding
import id.application.geoforestmaps.utils.Constant.formatDate
import id.application.geoforestmaps.utils.Constant.formatTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DatabaseGalleryAdapterItem(
    private val onClickLister : (ItemAllGeotaging) -> Unit
) : PagingDataAdapter<ItemAllGeotaging, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

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
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            when (holder) {
                is LinearViewHolder -> holder.bindLinear(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemDatabaseGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinearViewHolder(binding)
    }

    inner class LinearViewHolder(
        private val binding: ItemDatabaseGalleryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NewApi")
        fun bindLinear(item: ItemAllGeotaging) {
            with(binding) {
                ivGallery.load(item.photo)
                tvTitleBlock.text = item.plantId.toString()
                tvDescriptionBlock.text = item.blockId.toString()
            }
            binding.root.setOnClickListener {
                onClickLister(item)
            }
        }
    }
}