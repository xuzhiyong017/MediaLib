package com.sky.medialib.ui.picture.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sky.media.kit.BaseMediaApplication
import com.sky.medialib.R
import com.sky.medialib.ui.kit.common.view.RectImageView
import com.sky.medialib.ui.kit.manager.StickersManager
import com.sky.medialib.ui.kit.model.StickerModel
import px
import kotlin.math.ceil

/**
 * @author: xuzhiyong
 * @date: 2021/7/31  下午5:12
 * @Email: 18971269648@163.com
 * @description:
 */
class StickersAdapter(private val onItemClickListener: ((ViewHolder, Int, StickerModel) -> Unit)?)
    : BaseQuickAdapter<StickerModel, StickersAdapter.ViewHolder>(R.layout.spmixed_icon_item,StickersManager.getStaticStickers()) {

    private val mDefaultIconWidth = BaseMediaApplication.sContext.resources.displayMetrics.widthPixels * 4 / 16.0f
    private val mDefaultItemWidth = (BaseMediaApplication.sContext.resources.displayMetrics.widthPixels - 5.0f.px * 4) * 4 / 16.0f
    private val mStickerNameNormalColor = ContextCompat.getColor(BaseMediaApplication.sContext, R.color.white)
    private val mStickerNameSelectedColor =  ContextCompat.getColor(BaseMediaApplication.sContext, R.color.common_red)

    private var mClickPosition = -1

    class ViewHolder(var mItemView: View) : BaseViewHolder(mItemView) {
        var mFrameIconView: RectImageView = mItemView.findViewById<RectImageView>(R.id.frame_icon)
        var mStickerIconView: ImageView = mItemView.findViewById<ImageView>(R.id.sticker_icon)
        var mStickerNameView: TextView = mItemView.findViewById<TextView>(R.id.sticker_name)
        var mStickerSelectedView: ImageView = mItemView.findViewById<ImageView>(R.id.sticker_selected_icon)
    }

    override fun convert(holder: ViewHolder, item: StickerModel) {
        val layoutParams = holder.mFrameIconView.layoutParams as RelativeLayout.LayoutParams
        layoutParams.height = mDefaultIconWidth.toInt()
        layoutParams.leftMargin = 2.5f.px.toInt()
        layoutParams.rightMargin = 2.5f.px.toInt()
        layoutParams.topMargin =7f.px.toInt()
        layoutParams.bottomMargin = 5f.px.toInt()
        val layoutParams2 = holder.mStickerIconView.layoutParams
        layoutParams2.height = mDefaultIconWidth.toInt()


        holder.mFrameIconView.visibility = View.INVISIBLE
        holder.mStickerIconView.visibility = View.VISIBLE
        holder.mStickerNameView.visibility = View.VISIBLE


        val layoutParams3: ViewGroup.LayoutParams = holder.mItemView.layoutParams
        layoutParams3.width = mDefaultItemWidth.toInt()
        val width = item.showRect.width
        val height = item.showRect.height
        if(width != 0 && height != 0){
//            val ceil = ceil((width * mDefaultIconWidth / height).toDouble())
//                .toInt()
//            layoutParams.width = ceil
//            layoutParams3.width = ceil + ceil(5.0f.px.toDouble())
//                .toInt()
            layoutParams.width = mDefaultIconWidth.toInt()
            layoutParams3.width = mDefaultItemWidth.toInt()
        }

        holder.mStickerIconView.setBackgroundResource(R.drawable.album_sticker_background)

        holder.mItemView.layoutParams = layoutParams3
        val layoutParams4 = holder.mStickerNameView.layoutParams as RelativeLayout.LayoutParams
        layoutParams4.addRule(RelativeLayout.BELOW, R.id.sticker_icon)
        layoutParams4.bottomMargin = 7.0f.px.toInt()
        holder.mStickerNameView.layoutParams = layoutParams4


        holder.mStickerIconView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        holder.mStickerIconView.setImageResource(item.btnIcon)
        holder.mStickerNameView.text = item.btnTitle

        if(mClickPosition == holder.adapterPosition){
            holder.mStickerSelectedView.visibility = View.VISIBLE
            holder.mStickerNameView.setTextColor(mStickerNameSelectedColor)
        }else{
            holder.mStickerSelectedView.visibility = View.INVISIBLE
            holder.mStickerNameView.setTextColor(mStickerNameNormalColor)
        }

        holder.itemView.setOnClickListener {
            mClickPosition = holder.adapterPosition
            onItemClickListener?.invoke(holder,mClickPosition,item)
            notifyDataSetChanged()
        }
    }

    fun unSelectSticker(stickerId :Int){
       for ((index,item) in data.withIndex()){
           if(stickerId == item.id && mClickPosition == index){
               mClickPosition = -1
               notifyDataSetChanged()
               break
           }
       }
    }
}