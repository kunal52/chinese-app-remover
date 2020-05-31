package com.kunal.chineaseappuninstaller.adpater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kunal.chineaseappuninstaller.R
import com.kunal.chineaseappuninstaller.model.AppModel
import kotlinx.android.synthetic.main.app_list_item_layout.view.*


class AppListAdapter(var context: Context, var appList: ArrayList<AppModel>) :
    RecyclerView.Adapter<AppListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.app_list_item_layout, parent, false)
        return ViewHolder(inflate, context)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appList[position])
    }

    fun removeItem(packageName: String) {
        val iterator = appList.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.packages!! == packageName.substring(8)) {
                iterator.remove()
                notifyDataSetChanged()
                return
            }
        }
    }

    fun listChanged(appList: ArrayList<AppModel>) {
        this.appList = appList
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView) {
        val appName = itemView.app_name
        val packageName = itemView.package_name
        val appIcon = itemView.app_icon
        val deleteIcon = itemView.delete_icon

        fun bind(data: AppModel) {
            appName.text = data.name
            packageName.text = data.packages
            appIcon.setImageDrawable(data.icon)

            deleteIcon.setOnClickListener {

                val intent = Intent(Intent.ACTION_DELETE)
                intent.data = Uri.parse("package:${data.packages}")
                context.startActivity(intent)
            }
        }
    }


}