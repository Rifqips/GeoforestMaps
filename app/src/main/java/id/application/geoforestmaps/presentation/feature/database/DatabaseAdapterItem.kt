package id.application.geoforestmaps.presentation.feature.database

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.ItemBlokDataBinding

class DatabaseAdapterItem(
    private val onClickLister : (ItemAllBlocks) -> Unit
) : PagingDataAdapter<ItemAllBlocks, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemAllBlocks>() {
            override fun areItemsTheSame(
                oldItem: ItemAllBlocks,
                newItem: ItemAllBlocks
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ItemAllBlocks,
                newItem: ItemAllBlocks
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
        val binding = ItemBlokDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinearViewHolder(binding)
    }

    inner class LinearViewHolder(
        private val binding: ItemBlokDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindLinear(item: ItemAllBlocks) {
            with(binding) {
                ivImage.load(R.drawable.img_location)
                tvTitle.text = item.name
            }
            binding.root.setOnClickListener {
                onClickLister(item)
            }
        }
    }
}