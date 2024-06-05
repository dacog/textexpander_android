package com.dacotech.textexpanderapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

class CustomTriggerAdapter(
    context: Context,
    private val resource: Int,
    private val triggers: MutableList<Match>
) : ArrayAdapter<Match>(context, resource, triggers), Filterable {

    private var filteredItems: List<Match> = triggers

    override fun getCount(): Int = filteredItems.size

    override fun getItem(position: Int): Match? = filteredItems[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        val item = getItem(position)
        textView.text = "${item?.trigger} -> ${item?.replaceText}"
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint == null || constraint.isEmpty()) {
                    filterResults.values = triggers
                    filterResults.count = triggers.size
                } else {
                    val query = constraint.toString().lowercase()
                    val filteredList = triggers.filter {
                        it.trigger.lowercase().contains(query) || it.replaceText.lowercase().contains(query)
                    }
                    filterResults.values = filteredList
                    filterResults.count = filteredList.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as? List<Match> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}
