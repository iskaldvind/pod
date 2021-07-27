package io.iskaldvind.pictureoftheday.api

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.iskaldvind.pictureoftheday.ui.MainActivity

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(dataItem: MainActivity.Data)
}