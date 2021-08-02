package com.sky.medialib.ui.picture.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sky.media.kit.model.FilterExt
import com.sky.medialib.R
import com.sky.medialib.ui.kit.manager.ToolFilterManager
import com.sky.medialib.ui.picture.process.ImageProcessExt

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午5:21
 * @Email: 18971269648@163.com
 * @description:
 */
class ToolsAdapter(val imageProcessExt: ImageProcessExt, val screenWidth:Int, val mOnItemClickListener:((ToolViewHolder, Int, FilterExt) -> Unit)?)
    :BaseQuickAdapter<FilterExt,ToolsAdapter.ToolViewHolder>(R.layout.view_tool_item,ToolFilterManager.mToolFilters){

    private val mItemWidth =  (screenWidth * 3 / 16.0f).toInt()

    override fun convert(holder: ToolViewHolder, filter: FilterExt) {
        holder.run {
            val layoutParams = itemView.layoutParams
            layoutParams.width = mItemWidth
            itemView.layoutParams = layoutParams

            mToolIconView.setImageResource(filter.getIconResource())
            mToolNameView.text = filter.name
            mView.setOnClickListener {
                mOnItemClickListener?.invoke(this,adapterPosition,filter)
            }
            mView.isSelected = false
        }
    }

    class ToolViewHolder(var mView: View) : BaseViewHolder(mView) {
        var mToolIconView: ImageView
        var mToolNameView: TextView

        init {
            mToolIconView = mView.findViewById<View>(R.id.tool_icon) as ImageView
            mToolNameView = mView.findViewById<View>(R.id.tool_name) as TextView
        }
    }
}