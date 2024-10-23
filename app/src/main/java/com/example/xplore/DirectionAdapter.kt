package com.example.xplore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DirectionsAdapter(private val directionsList: List<Step>) : RecyclerView.Adapter<DirectionsAdapter.DirectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_direction, parent, false)
        return DirectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DirectionViewHolder, position: Int) {
        val step = directionsList[position]
        holder.bind(step)
    }

    override fun getItemCount(): Int {
        return directionsList.size
    }

    class DirectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val directionText: TextView = itemView.findViewById(R.id.directionText)

        fun bind(step: Step) {
            val directionString = "${step.htmlInstructions?.replace(Regex("<[^>]*>"), "")}: ${step.duration.text} (${step.distance.text})"
            directionText.text = directionString
        }
    }
}
