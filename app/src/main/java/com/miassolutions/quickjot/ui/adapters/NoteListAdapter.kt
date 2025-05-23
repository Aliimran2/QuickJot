package com.miassolutions.quickjot.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miassolutions.quickjot.data.local.NoteEntity
import com.miassolutions.quickjot.databinding.ItemNoteBinding
import com.miassolutions.quickjot.utils.toFormattedDate

class NoteListAdapter(
    private val onItemClick: (NoteEntity) -> Unit,
    private val onItemLongClick: (NoteEntity) -> Boolean,
    private val isSelected : (NoteEntity) -> Boolean
) : ListAdapter<NoteEntity, NoteListAdapter.NoteViewHolder>(NoteDiffCallback()) {


    private val selectedItems = mutableSetOf<Int>()

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedItems(ids: Set<Int>) {
        selectedItems.clear()
        selectedItems.addAll(ids)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NoteViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return NoteViewHolder(ItemNoteBinding.inflate(inflate, parent, false))
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoteEntity) {
            binding.apply {
                tvTitle.text = item.title
                tvContent.text = item.content
                tvCreatedAt.text = "Created: ${item.createdAt.toFormattedDate()}"


                root.setBackgroundColor(
                    if (isSelected(item)) Color.LTGRAY else Color.WHITE
                )



                root.setOnClickListener {
                    onItemClick(item)
                }
                root.setOnLongClickListener {
                    onItemLongClick(item)
                }
            }
        }
    }
}