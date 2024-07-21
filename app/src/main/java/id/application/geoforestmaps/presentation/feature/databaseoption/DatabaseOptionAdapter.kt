package id.application.geoforestmaps.presentation.feature.databaseoption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.application.core.domain.model.Dashboard
import id.application.geoforestmaps.databinding.ItemCardDashboardDataBinding

class DatabaseOptionAdapter(
    private val onClickListener: (Dashboard) -> Unit
) : RecyclerView.Adapter<DatabaseOptionAdapter.ViewHolder>() {

    private val dataDiffer = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Dashboard>(){
        override fun areItemsTheSame(oldItem: Dashboard, newItem: Dashboard): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.image == newItem.image
        }

        override fun areContentsTheSame(oldItem: Dashboard, newItem: Dashboard): Boolean {
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

    fun setData(data: List<Dashboard>) {
        dataDiffer.submitList(data)
        notifyItemRangeChanged(0, data.size)
    }

    inner class ViewHolder(private val binding: ItemCardDashboardDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Dashboard, position: Int) {
            with(binding) {
                ivImage.setImageResource(data.image)
                tvTitle.text = data.name
                itemView.setOnClickListener {
                    if (position == 2) {
                        onClickListener(data) // Memanggil lambda jika posisi adalah 2
                    }
                }
            }
        }
    }
}
