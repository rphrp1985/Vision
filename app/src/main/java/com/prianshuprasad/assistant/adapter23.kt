package com.prianshuprasad.assistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


//class adapter23(private val listener: chatWindow2): RecyclerView.Adapter<messageViewHolder>()
//{
//    private val item: ArrayList<messageData> = ArrayList()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): messageViewHolder {
//
//        val view= LayoutInflater.from(parent.context).inflate(R.layout.messageview,parent,false)
//
//        val viewHolder=messageViewHolder(view)
//
//
//        view.setOnClickListener{
//
//            listener.onitemclicked(item[viewHolder.adapterPosition ])
//
//        }
//
//        return viewHolder
//
//
//
//    }
//
//
//    override fun getItemCount(): Int {
//        return item.size
//    }
//
//
//
//    override fun onBindViewHolder(holder: messageViewHolder, position: Int) {
//
//        val curritem= item[position]
//
//        if(curritem.who==0) {
//            holder.recievedText.text = curritem.Message
//            holder.sentText.visibility= View.GONE
//        }
//        else
//            if(curritem.who==1) {
//                holder.sentText.text = curritem.Message
//                holder.recievedText.visibility= View.GONE
//            }
//
//    }
//
//    fun updatenews(Newsarray:ArrayList<messageData>){
//
//        item.clear()
//        item.addAll(Newsarray)
//
//        notifyDataSetChanged()
//
//        listener.scrolltoPos(item.size-1)
//
//    }
//
//
//
//
//}
//
//
////
////
////class messageViewHolder(itemViews: View): RecyclerView.ViewHolder(itemViews){
////
////    var sentText : TextView = itemViews.findViewById(R.id.sentText)
////    var recievedText : TextView= itemViews.findViewById(R.id.recievedText)
//////    var imageView: ImageView = itemViews.findViewById(R.id.newsimage)
//////    var description: TextView = itemViews.findViewById(R.id.description)
////}
////
////interface itemclicked{
////    fun onitemclicked(item: messageData)
////}