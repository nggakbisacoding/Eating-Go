package com.example.eatinggo

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eatinggo.model.CafeDisplay

class CafeListAdapter(private val mList: List<CafeDisplay>) : RecyclerView.Adapter<CafeListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cafe_list_view, parent, false)
        return ViewHolder(view).listen{ pos, _ ->
            val item = mList[pos]
            Toast.makeText(view.context, item.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: CafeDisplay) {
            val imageView: ImageView = itemView.findViewById(R.id.cafe_image)
            val cafeName: TextView = itemView.findViewById(R.id.cafe_name)
            val cafeAddress: TextView = itemView.findViewById(R.id.cafe_address)
            val seatAvailable: TextView = itemView.findViewById(R.id.seat)
            cafeName.text = data.name
            cafeAddress.text = data.lokasi
            seatAvailable.text = data.seat.toString()
            Glide.with(itemView.context).load(Uri.parse(data.image)).override(300,300).into(imageView)
        }
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}