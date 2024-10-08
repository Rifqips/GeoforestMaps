package id.application.geoforestmaps.presentation.adapter.databasegallery

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.geoforestmaps.databinding.ItemDatabaseGalleryBinding

class DatabaseGalleryAdapterItem(
    private val onClickLister : (ItemAllGeotaging) -> Unit,
    private val onClickListerDownload : (ItemAllGeotaging) -> Unit,
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
                Log.d("check-photo", item.photo)

                tvTitleBlock.text = item.plant
                tvDescriptionBlock.text = item.block
                ivDownload.setOnClickListener {
                    onClickListerDownload(item)
                }
            }
            binding.root.setOnClickListener {
                onClickLister(item)
            }
        }
    }
}