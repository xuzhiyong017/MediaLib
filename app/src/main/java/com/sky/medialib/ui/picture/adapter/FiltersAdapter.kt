package com.sky.medialib.ui.picture.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sky.media.kit.model.FilterExt
import com.sky.medialib.R
import com.sky.medialib.ui.kit.filter.OriginNormalFilter
import com.sky.medialib.ui.kit.manager.ToolFilterManager
import com.sky.medialib.ui.picture.process.ImageProcessExt

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午2:06
 * @Email: 18971269648@163.com
 * @description:
 */
class FiltersAdapter(val imageProcessExt: ImageProcessExt,val screenWidth:Int,val listener:((FilterViewHolder,Int,FilterExt) -> Unit)?)
    :BaseQuickAdapter<FilterExt, FiltersAdapter.FilterViewHolder>(R.layout.view_filter_item,ToolFilterManager.normalFilters){

    private var mSelectedFilter:FilterExt ? = null
    private val mFilterItemWidth =  screenWidth * 3 / 16.0f

    init {

    }

    override fun convert(holder: FilterViewHolder, filterExt: FilterExt) {
        holder.run {
            mFilterNewView.setImageResource(R.drawable.edit_filter_icon_recommed)
            mFilterLayout.setOnClickListener {
                mSelectedFilter = filterExt
                listener?.invoke(holder,adapterPosition,filterExt)
            }
            mFilterLayout.layoutParams = RecyclerView.LayoutParams(mFilterItemWidth.toInt(),ViewGroup.LayoutParams.WRAP_CONTENT)
            Glide.with(mFilterLayout).load(filterExt.getIconResource())
                .placeholder(R.drawable.defaultpics_filter_200)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(mFilterIconView)
            mFilterNameView.text = filterExt.name
            val filter = imageProcessExt.getNormalFilter()
            var selected = false
            if(filter != null){
                if(filter.mId == filterExt.mId){
                    selected = true
                }
            }else if(filterExt is OriginNormalFilter){
                selected = true
            }
            if(selected){
                mFilterLayout.isSelected = true
                mFilterMaskView.visibility = View.VISIBLE
            }else{
                mFilterLayout.isSelected = false
                mFilterMaskView.visibility = View.GONE
            }
        }
    }

    class FilterViewHolder(view: View) : BaseViewHolder(view) {
        var mFilterIconView: ImageView
        var mFilterLayout: RelativeLayout
        var mFilterMaskView: ImageView
        var mFilterNameView: TextView
        var mFilterNewView: ImageView

        init {
            mFilterLayout = view.findViewById<View>(R.id.filter_item_layout) as RelativeLayout
            mFilterIconView = view.findViewById<View>(R.id.filter_icon) as ImageView
            mFilterNameView = view.findViewById<View>(R.id.filter_name) as TextView
            mFilterMaskView = view.findViewById<View>(R.id.filter_mask) as ImageView
            mFilterNewView = view.findViewById<View>(R.id.filter_new) as ImageView
        }
    }
}