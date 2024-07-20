package id.application.geoforestmaps.presentation.adapter.geotags

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.ItemBlokDataBinding
import id.application.geoforestmaps.databinding.ItemHistoryDataBinding

class GeotaggingAdapterItem(
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
        val binding = ItemHistoryDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinearViewHolder(binding)
    }

    inner class LinearViewHolder(
        private val binding: ItemHistoryDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindLinear(item: ItemAllGeotaging) {
            with(binding) {
                ivItemHistory.load(item.photo)
                tvTitleItemHistory.text = item.blockId.toString()
                tvDescItemHistory.text = item.blockId.toString()
                tvTimeItemHistory.text = item.blockId.toString()
                tvDateItemHistory.text = item.blockId.toString()
            }
            binding.root.setOnClickListener {
                onClickLister(item)
            }
        }
    }
}