package com.sky.medialib.ui.music

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sky.medialib.R
import com.sky.medialib.ui.kit.common.base.AppActivity
import com.sky.medialib.ui.kit.common.base.adapter.BaseRecyclerCommonAdapter
import com.sky.medialib.ui.kit.common.base.adapter.Item
import com.sky.medialib.ui.kit.common.base.adapter.OnLoadMoreListener
import com.sky.medialib.ui.kit.common.base.recycler.ListItemDecoration
import com.sky.medialib.ui.kit.model.Music
import com.sky.medialib.ui.music.entity.MusicItem
import com.sky.medialib.ui.music.event.CutMusicInfoEvent
import com.sky.medialib.ui.music.viewmodel.MusicModel
import com.sky.medialib.util.EventBusHelper
import kotlinx.android.synthetic.main.activity_music_choose.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

const val KEY_IS_FROM_CAMERA = "key_from_camera"

class MusicChooseActivity : AppActivity(),OnLoadMoreListener {

    val viewModel = viewModels<MusicModel>()

    lateinit var mAdapter: BaseRecyclerCommonAdapter<Music>
    private var mIsFromCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_choose)
        EventBusHelper.register(this)

        mIsFromCamera = intent.getBooleanExtra(KEY_IS_FROM_CAMERA,true)
        mAdapter = object : BaseRecyclerCommonAdapter<Music>(recycler_view) {
            override fun createItem(obj: Any?): Item<Music> {
                return MusicItem()
            }
        }.apply {
            setOnLoadMoreListener(this@MusicChooseActivity)
        }

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addItemDecoration(ListItemDecoration(ColorDrawable(ContextCompat.getColor(this,R.color.background)),2))
        recycler_view.adapter = mAdapter
        recycler_view.setOnItemClickListener { _, i, _ ->
            val item: Music = mAdapter.getItem(i)
            val intent = Intent(this@MusicChooseActivity, MusicPlayActivity::class.java)
            intent.putExtra("key_music", item)
            intent.putExtra(KEY_IS_FROM_CAMERA, mIsFromCamera)
            startActivity(intent)
        }

        viewModel.value.data.observe(this,
            {
                mAdapter.list = it
                error_view.updateStatus(0)
            })

        error_view.setOnClickListener {
            getData()
        }

        toolbar_navigation.setOnClickListener {
            finish()
        }

        getData()
    }

    private fun getData() {
        viewModel.value.getData()
    }

    override fun onLoadMore() {

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusHelper.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(cutMusicInfoEvent: CutMusicInfoEvent?) {
        finish()
    }

}