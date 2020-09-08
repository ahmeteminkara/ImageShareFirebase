package com.aek.imagesharefirebase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aek.imagesharefirebase.R
import com.aek.imagesharefirebase.model.Post
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row.view.*

class FeedRecyclerAdapter(val list:ArrayList<Post>): RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {
    class PostHolder(itemView:View) :RecyclerView.ViewHolder(itemView){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row, parent, false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.itemView.recyclerview_tvemail.text = list.elementAt(position).email
        holder.itemView.recyclerview_tvcomment.text = list.elementAt(position).comment

        Picasso.get().load(list.elementAt(position).imageUrl).into(holder.itemView.recyclerview_imageview);

        //holder.itemView.recyclerview_imageview.setImageBitmap()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}