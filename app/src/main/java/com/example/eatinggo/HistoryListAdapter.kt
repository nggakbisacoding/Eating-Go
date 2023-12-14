package com.example.eatinggo

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eatinggo.model.CafeDisplay

class HistoryListAdapter(private val mList: List<CafeDisplay>) : RecyclerView.Adapter<HistoryListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item_holder, parent, false)
        return ViewHolder(view).listen{ _, _ ->
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
            val imageView: ImageView = itemView.findViewById(R.id.cafe_img)
            val cafeName: TextView = itemView.findViewById(R.id.cafe_nm)
            val cafeAddress: TextView = itemView.findViewById(R.id.cafe_dress)
            cafeName.text = data.name
            cafeAddress.text = data.lokasi
            val query: String = if(data.image == null) {
                "https://hesolutions.com.pk/wp-content/uploads/2019/01/picture-not-available.jpg"
            } else {
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=${data.image[0].width}&maxheight=${data.image[0].height}&photo_reference=${data.image[0].references}&key=${BuildConfig.PLACES_API_KEY}"
            }
            Glide.with(itemView).load(Uri.parse(query)).override(350,120).into(imageView)
        }
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}
