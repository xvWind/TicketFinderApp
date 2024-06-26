package com.example.ticketfinderapp

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyRecyclerAdapter(private val events: ArrayList<Event>) : RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>() {
    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder.
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.imageView)
        val title = itemView.findViewById<TextView>(R.id.textView_title)
        val location = itemView.findViewById<TextView>(R.id.textView_location)
        val address = itemView.findViewById<TextView>(R.id.textView_address)
        val date = itemView.findViewById<TextView>(R.id.textView_date)
        val range = itemView.findViewById<TextView>(R.id.textView_range)
        val button = itemView.findViewById<Button>(R.id.button_tickets)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val highestQualityImage = events[position].images.maxByOrNull {
            it.width.toInt() * it.height.toInt()
        }

        val context = holder.itemView.context

        if (highestQualityImage != null) {
            Glide.with(context)
                .load(highestQualityImage.url)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.no_image)
        }

        holder.title.text = events[position].name ?: "N/A"

        val venue = events[position]._embedded.venues.getOrNull(0)
        val venueName = venue?.name ?: "N/A"
        val cityName = venue?.city?.name ?: "N/A"
        holder.location.text = "$venueName, $cityName"
        val addressLine1 = venue?.address?.line1 ?: "N/A"
        val stateCode = venue?.state?.stateCode ?: "N/A"
        holder.address.text = "$addressLine1, $cityName, $stateCode"

        var dateText = events[position].dates.start.localDate ?: "N/A"
        var timeText = events[position].dates.start.localTime ?: "N/A"

        dateText = dateText.takeIf { it.isNotEmpty() }?.let {
            val dateParts = it.split("-")
            "${dateParts.getOrNull(1)}/${dateParts.getOrNull(2)}/${dateParts.getOrNull(0) ?: "N/A"}"
        } ?: "N/A"

        timeText = timeText.takeIf { it.isNotEmpty() }?.let {
            val timeParts = it.split(":")
            val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
            val amPm = if (hour < 12) "AM" else "PM"
            "${if (hour > 12) hour - 12 else hour}:${timeParts.getOrNull(1) ?: "N/A"} $amPm"
        } ?: "N/A"

        holder.date.text = "$dateText @ $timeText"

        if (events[position].priceRanges?.isNotEmpty() == true) {
            holder.range.text = "Price Range: $${events[position].priceRanges[0].min} - $${events[position].priceRanges[0].max}"
            holder.range.visibility = View.VISIBLE
        } else {
            holder.range.visibility = View.INVISIBLE
        }

        holder.button.setOnClickListener {
            events[position].url?.let { url ->
                val browserIntent = Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(url)
                holder.itemView.context.startActivity(browserIntent)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return events.size
    }
}