package org.albaspazio.psysuite.core.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import org.albaspazio.psysuite.core.R


class ResultsRecyclerViewAdapter(private val values: List<ResultFileEntry>) : RecyclerView.Adapter<ResultsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.results_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(values[position])
    }

    override fun getItemCount(): Int = values.size

    fun selectAll(select:Boolean){
        values.map{ it.selected = select    }
        notifyDataSetChanged()
    }

    val selectedItems:List<ResultFileEntry>
        get() = values.filter{ it.selected }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val labelView: TextView = view.findViewById(R.id.result_list_item_label)
        val switchView: SwitchCompat = view.findViewById(R.id.result_list_item_switch)

        override fun toString(): String {
            return super.toString() + " '" + labelView.text + "'"
        }

        fun bindItems(entry:ResultFileEntry){
            labelView.text           = entry.label
            switchView.isSelected    = entry.selected
            switchView.text          = ""
        }

        var isSelected:Boolean
            get() = switchView.isSelected
            set(value){
                switchView.isSelected = value
            }
    }
}

data class ResultFileEntry(val label:String, var selected:Boolean)