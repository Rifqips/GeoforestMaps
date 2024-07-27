package id.application.geoforestmaps.presentation.adapter.databaseoption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.application.core.domain.model.DatabaseOption
import id.application.geoforestmaps.databinding.ItemCardDashboardDataBinding

class DatabaseOptionAdapter(
    private val databaseList: (DatabaseOption) -> Unit,
    private val databaseGallery: (DatabaseOption) -> Unit,
    private val databaseMap: (DatabaseOption) -> Unit
) : RecyclerView.Adapter<DatabaseOptionAdapter.ViewHolder>() {

    private val dataDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<DatabaseOption>(){
        override fun areItemsTheSame(oldItem: DatabaseOption, newItem: DatabaseOption): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.image == newItem.image
        }

        override fun areContentsTheSame(oldItem: DatabaseOption, newItem: DatabaseOption): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardDashboardDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataDiffer.currentList[position], position)
    }

    override fun getItemCount(): Int {
        return dataDiffer.currentList.size
    }

    fun setData(data: List<DatabaseOption>) {
        dataDiffer.submitList(data)
        notifyItemRangeChanged(0, data.size)
    }

    inner class ViewHolder(private val binding: ItemCardDashboardDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DatabaseOption, position: Int) {
            with(binding) {
                ivImage.setImageResource(data.image)
                viewOverlay.setBackgroundResource(data.overlay)
                tvTitle.text = data.name
                itemView.setOnClickListener {
                    when(position){
                        0 -> databaseList(data)
                        1 -> databaseGallery(data)
                        2 -> databaseMap(data)
                    }
                }
            }
        }
    }
}
