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
import com.sky.medialib.ui.picture.process.ImageProcessExt
import com.sky.medialib.R
import com.sky.medialib.ui.kit.adjust.AdjusterExt
import com.sky.medialib.ui.kit.filter.MagicFilterExt
import com.sky.medialib.ui.kit.filter.OriginMagicFilterExt
import com.sky.medialib.ui.kit.manager.MagicManager
import com.sky.medialib.ui.kit.manager.ToolFilterManager
import com.sky.medialib.ui.kit.model.MagicFilterModel
import kotlin.math.round

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  上午10:37
 * @Email: 18971269648@163.com
 * @description:
 */
class MagicFilterAdapter(val imageProcessExt: ImageProcessExt, val screenWidth:Int, val mOnItemClickListener:((MagicViewHolder, Int,MagicFilterExt) -> Unit)?)
    : BaseQuickAdapter<MagicFilterExt,MagicFilterAdapter.MagicViewHolder>(R.layout.view_magic_item,ToolFilterManager.magicFilterList){

    private val mMagicItemWidth =  screenWidth * 3 / 16.0f

    override fun convert(holder: MagicViewHolder, item: MagicFilterExt) {
        val filter = item
        holder.run {
            mMagicNewView.setImageResource(R.drawable.edit_filter_icon_recommed)
            mMagicLayout.setOnClickListener {
                mOnItemClickListener?.invoke(this,adapterPosition,filter)
            }
            mMagicLayout.layoutParams = RecyclerView.LayoutParams(round(mMagicItemWidth).toInt(),ViewGroup.LayoutParams.WRAP_CONTENT)
            Glide.with(itemView).load(R.drawable.filter_icon_0003)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .placeholder(R.drawable.filter_icon_0003)
                .error(R.drawable.filter_icon_0003)
                .into(mMagicIconView)
            mMagicNameView.text = filter.name
            val magicFilter = imageProcessExt.getMagicFilter()
            var hasMirroir = if(magicFilter != null){
                magicFilter.getMirrorPos(magicFilter.mId) >= 0
            }else {
                magicFilter is OriginMagicFilterExt
            }

            if(hasMirroir){
                mMagicLayout.isSelected = true
                mMagicMaskView.visibility = View.VISIBLE
                if (filter is OriginMagicFilterExt && magicFilter == null || filter is OriginMagicFilterExt && magicFilter is OriginMagicFilterExt) {
                    mMagicColorIndexView.visibility = View.GONE
                    mMagicNormalDotView.visibility = View.VISIBLE
                } else {
                    mMagicColorIndexView.visibility = View.VISIBLE
                    mMagicColorIndexView.text = "" + ((filter.adjuster as AdjusterExt).currentMirrorPos + 1)
                    mMagicNormalDotView.visibility = View.GONE
                }
            }else{
                mMagicLayout.isSelected = false
                mMagicMaskView.visibility = View.GONE
                mMagicColorIndexView.visibility = View.GONE
                mMagicNormalDotView.visibility = View.GONE
            }
        }

    }


    class MagicViewHolder(view: View) : BaseViewHolder(view) {
        var mMagicColorIndexView: TextView
        var mMagicIconView: ImageView
        var mMagicLayout: RelativeLayout
        var mMagicMaskView: ImageView
        var mMagicNameView: TextView
        var mMagicNewView: ImageView
        var mMagicNormalDotView: ImageView

        init {
            mMagicLayout = view.findViewById<View>(R.id.magic_item_layout) as RelativeLayout
            mMagicIconView = view.findViewById<View>(R.id.magic_icon) as ImageView
            mMagicNameView = view.findViewById<View>(R.id.magic_name) as TextView
            mMagicColorIndexView = view.findViewById<View>(R.id.magic_color_index) as TextView
            mMagicMaskView = view.findViewById<View>(R.id.magic_mask) as ImageView
            mMagicNormalDotView = view.findViewById<View>(R.id.magic_mask_normal_dot) as ImageView
            mMagicNewView = view.findViewById<View>(R.id.magic_new) as ImageView
        }
    }

}