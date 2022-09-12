package com.prianshuprasad.assistant

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator


class adapter( private val listener: chatWindow): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val item: ArrayList<messageData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var view= LayoutInflater.from(parent.context).inflate(R.layout.chatbox_my,parent,false)

        var viewHolder:RecyclerView.ViewHolder = MychatViewHolder(view)

//         if(viewHolder.adapterPosition>=0 && item[viewHolder.adapterPosition].who==1)
//         {
//             view= LayoutInflater.from(parent.context).inflate(R.layout.chatbox_other,parent,false)
//                viewHolder= OtherchatViewHolder(view)
//
//
//
//         }



        view.setOnClickListener{

            listener.onitemclicked(item[viewHolder.adapterPosition ])

        }

        return viewHolder



    }


    override fun getItemCount(): Int {
        return item.size
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val curritem= item[position]




        if(curritem.who==0) {
            (holder as MychatViewHolder).MyWidg.visibility= View.VISIBLE
            (holder as MychatViewHolder).myText.text = curritem.Message
            (holder as MychatViewHolder).OtherWidg.visibility= View.GONE
        }
        else
        if(curritem.who==1) {
            (holder as MychatViewHolder).OtherWidg.visibility= View.VISIBLE
            if(curritem.Message.equals("%%%%"))
            {
                (holder as MychatViewHolder).OtherText.visibility= View.GONE
                (holder as MychatViewHolder).loading.visibility= View.VISIBLE
            }else
            {
                (holder as MychatViewHolder).OtherText.visibility= View.VISIBLE
                (holder as MychatViewHolder).loading.visibility= View.GONE
            }
            (holder as MychatViewHolder).OtherText.text = curritem.Message
            (holder as MychatViewHolder).MyWidg.visibility= View.GONE

        }

    }

    fun updatenews(Newsarray:ArrayList<messageData>){

        item.clear()
        item.addAll(Newsarray)

        notifyDataSetChanged()

        listener.scrolltoPos(item.size-1)

    }




}




class MychatViewHolder(itemViews: View): RecyclerView.ViewHolder(itemViews){

    var myText : TextView = itemViews.findViewById(R.id.text_gchat_message_me)
    var OtherText: TextView = itemViews.findViewById(R.id.text_gchat_message_other)
    var MyWidg:CardView = itemViews.findViewById(R.id.card_gchat_message_me)
    var OtherWidg:CardView = itemViews.findViewById(R.id.card_gchat_message_other)
    var loading: LinearProgressIndicator = itemViews.findViewById(R.id.linearProgressIndicator)
//    var imageView: ImageView = itemViews.findViewById(R.id.newsimage)
//    var description: TextView = itemViews.findViewById(R.id.description)
}








interface itemclicked{
    fun onitemclicked(item: messageData)
}