package com.skg.aws_sample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.amplify.generated.graphql.AllUsersQuery
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.skg.aws_sample.R

class UserListAdapter : RecyclerView.Adapter<UserListAdapter.VH>() {
    private var mData: ArrayList<AllUsersQuery.Item>? = null
    private lateinit var context: Context
    lateinit var onClickItem : View.OnClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_list, parent,false)
        view.setOnClickListener(onClickItem)
        context = parent.context
        return VH(view)
    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(context).
            load(R.drawable.amazon).
            circleCrop().
            into(holder.imgAvatar)

        for(i in mData?.get(position)?.Attributes()!!){
            if(i.Name() == "email"){
                holder.tvName.text = i.Value()
            }
            if(i.Name() == "picture"){
                Glide.with(context).load(i.Value())
                    .circleCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.imgAvatar)
            }

        }
    }

    fun setData(data: ArrayList<AllUsersQuery.Item>) {
        if (data!= null) {
            mData = data
            notifyDataSetChanged()
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgAvatar = itemView.findViewById<ImageView>(R.id.imgAvatar)!!
        var tvName = itemView.findViewById<TextView>(R.id.tvName)!!
    }
}