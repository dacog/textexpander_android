package com.dacotech.textexpanderapp

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable

class CustomTriggerAdapter(context: Context, private val resource: Int, private val items: List<String>) :
    ArrayAdapter<String>(context, resource, items), Filterable {

    private var filteredItems: List<String> = items

    override fun getCount(): Int {
        return filteredItems.size
    }

    override fun getItem(position: Int): String? {
        return filteredItems[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint == null || constraint.isEmpty()) {
                    filterResults.count = items.size
                    filterResults.values = items
                } else {
                    val query = constraint.toString().lowercase()
                    val filteredList = items.filter {
                        it.lowercase().contains(query)
                    }
                    filterResults.count = filteredList.size
                    filterResults.values = filteredList
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as List<String>
                notifyDataSetChanged()
            }
        }
    }
}
