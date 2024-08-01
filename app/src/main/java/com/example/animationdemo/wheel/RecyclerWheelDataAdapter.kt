package com.example.animationdemo.wheel

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animationdemo.R

class RecyclerWheelDataAdapter(
    private val count: Int,
    private val startOffset: Int,
    private val endOffset: Int
) :
    RecyclerView.Adapter<RecyclerWheelDataAdapter.ViewHolder>() {

    private val intRange by lazy { IntRange(startOffset, startOffset + count - 1) }

    var itemClick: ((pos: Int) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_wheel_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (intRange.contains(position)) {
            holder.itemView.visibility = View.VISIBLE
            holder.wheelTextView.text = getRelativePosition(position).toString()
        } else {
            holder.wheelTextView.text = ""
//            holder.itemView.visibility = View.INVISIBLE
        }
        holder.itemView.setOnClickListener {
            Log.i("RecyclerWheelView","click pos:$position")
            if (intRange.contains(position)) {
                itemClick?.invoke(getRelativePosition(position))
            } else if (position < intRange.min()) {
                itemClick?.invoke(0)
            } else {
                itemClick?.invoke(count - 1)
            }
        }
    }

    override fun getItemCount(): Int {
        return startOffset + count + endOffset
    }

    fun getRelativePosition(globalPosition: Int): Int {
        if (intRange.contains(globalPosition)) {
            return globalPosition - startOffset
        } else if (intRange.min() >= globalPosition) {
            return 0
        } else {
            return count - 1;
        }
    }

    fun getGlobalPosition(relativePosition: Int): Int {
        return startOffset + relativePosition
    }


    fun getDataGlobalPosition(globalPosition: Int): Int {
        return startOffset + getRelativePosition(globalPosition)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wheelTextView: TextView = itemView.findViewById(R.id.wheel_text)
    }
}
