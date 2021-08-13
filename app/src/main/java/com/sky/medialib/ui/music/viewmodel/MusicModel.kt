package com.sky.medialib.ui.music.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.medialib.ui.kit.model.Music
import com.sky.medialib.ui.music.repository.MusicRepo
import kotlinx.coroutines.launch

/**
 * @author: xuzhiyong
 * @date: 2021/8/12  下午5:13
 * @Email: 18971269648@163.com
 * @description:
 */
class MusicModel : ViewModel() {

    private val _list = MutableLiveData<List<Music>>()
    val data:LiveData<List<Music>> = _list

    fun getData(){
        viewModelScope.launch {
            _list.value = MusicRepo().getMusicList()
        }
    }
}