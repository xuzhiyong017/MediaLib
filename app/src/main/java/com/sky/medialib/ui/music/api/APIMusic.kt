package com.sky.medialib.ui.music.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sky.medialib.ui.music.entity.JsonMusic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xuzhiyong
 * @date: 2021/8/12  下午4:48
 * @Email: 18971269648@163.com
 * @description:
 */

const val json = """[
    {
        "id":458,
        "type":92,
        "typeName":"华语流行",
        "name":"最美的期待",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/46SjK8PZSG.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/SeGhD6nwXp.mp3",
        "authorName":"周笔畅",
        "videoTimeSize":"03:30",
        "createTime":"2018-02-09 09:19:26",
        "modifyTime":"2018-02-09 09:19:26",
        "delFlag":1
    },
    {
        "id":455,
        "type":92,
        "typeName":"华语流行",
        "name":"少林英雄",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/SaGWDwQt84.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/hk5zKe7MsZ.mp3",
        "authorName":"于荣光",
        "videoTimeSize":"02:55",
        "createTime":"2018-02-08 09:33:18",
        "modifyTime":"2018-02-08 09:33:18",
        "delFlag":1
    },
    {
        "id":454,
        "type":92,
        "typeName":"华语流行",
        "name":"My Sunshine",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/EZetwXnKZ5.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/xX88XxhZQ4.mp3",
        "authorName":"张杰",
        "videoTimeSize":"04:26",
        "createTime":"2018-02-08 09:32:34",
        "modifyTime":"2018-02-08 09:32:34",
        "delFlag":1
    },
    {
        "id":438,
        "type":92,
        "typeName":"华语流行",
        "name":"盗心贼",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/BGCWBZKkYh.png",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/MZfmaFTwSY.mp3",
        "authorName":"黑龙",
        "videoTimeSize":"03:34",
        "createTime":"2018-02-02 10:04:43",
        "modifyTime":"2018-02-02 10:04:43",
        "delFlag":1
    },
    {
        "id":437,
        "type":92,
        "typeName":"华语流行",
        "name":"最美情侣",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/S5FjAFQ3WH.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/jYkDrPHiWZ.mp3",
        "authorName":"白小白",
        "videoTimeSize":"04:01",
        "createTime":"2018-02-02 09:23:32",
        "modifyTime":"2018-02-02 09:23:32",
        "delFlag":1
    },
    {
        "id":388,
        "type":92,
        "typeName":"华语流行",
        "name":"小公主",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/DfrAFAQ6h2.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/isrtaAKbKD.mp3",
        "authorName":"蒋蒋 &amp; 杨清柠",
        "videoTimeSize":"03:11",
        "createTime":"2018-02-01 16:04:08",
        "modifyTime":"2018-02-01 16:05:33",
        "delFlag":1
    },
    {
        "id":347,
        "type":92,
        "typeName":"华语流行",
        "name":"她妈妈不喜欢我",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/dGXBbzHFws.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/snDjECRZJb.mp3",
        "authorName":"王矜霖",
        "videoTimeSize":"03:25",
        "createTime":"2018-02-01 15:43:06",
        "modifyTime":"2018-02-01 15:43:06",
        "delFlag":1
    },
    {
        "id":338,
        "type":92,
        "typeName":"华语流行",
        "name":"至少还有你",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/p7zHD4GnzP.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/mYsBZMK8j7.mp3",
        "authorName":"林忆莲",
        "videoTimeSize":"04:35",
        "createTime":"2018-02-01 15:37:55",
        "modifyTime":"2018-02-01 15:37:55",
        "delFlag":1
    },
    {
        "id":336,
        "type":92,
        "typeName":"华语流行",
        "name":"遇见",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/6PRjrxHZbP.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/HBGJfXYGfd.mp3",
        "authorName":"孙燕姿",
        "videoTimeSize":"03:30",
        "createTime":"2018-02-01 15:35:45",
        "modifyTime":"2018-02-01 15:35:45",
        "delFlag":1
    },
    {
        "id":284,
        "type":92,
        "typeName":"华语流行",
        "name":"走心小卖家",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/AzShTpNrN5.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/8K64GtFxda.mp3",
        "authorName":"张雪飞 &amp; 菜刀",
        "videoTimeSize":"03:40",
        "createTime":"2018-02-01 14:36:31",
        "modifyTime":"2018-02-01 14:36:31",
        "delFlag":1
    },
    {
        "id":283,
        "type":92,
        "typeName":"华语流行",
        "name":"情人节",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/Qm3RtE85QH.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/A7weQB3art.mp3",
        "authorName":"庄心妍",
        "videoTimeSize":"04:09",
        "createTime":"2018-02-01 14:35:37",
        "modifyTime":"2018-02-01 14:35:37",
        "delFlag":1
    },
    {
        "id":282,
        "type":92,
        "typeName":"华语流行",
        "name":"再也没有",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/aMfbxkY7jn.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/RmWXKrxJdA.mp3",
        "authorName":"Ryan.B",
        "videoTimeSize":"03:32",
        "createTime":"2018-02-01 14:35:03",
        "modifyTime":"2018-02-01 14:35:03",
        "delFlag":1
    },
    {
        "id":281,
        "type":92,
        "typeName":"华语流行",
        "name":"没有翅膀的鸟",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/PPGKJCYG3K.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/bSSkTN4dsi.mp3",
        "authorName":"庄心妍",
        "videoTimeSize":"04:43",
        "createTime":"2018-02-01 14:34:03",
        "modifyTime":"2018-02-01 14:34:03",
        "delFlag":1
    },
    {
        "id":279,
        "type":92,
        "typeName":"华语流行",
        "name":"好可惜",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/yMcHiymZ4x.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/ctExB6Fjsk.mp3",
        "authorName":"庄心妍",
        "videoTimeSize":"05:15",
        "createTime":"2018-02-01 14:32:42",
        "modifyTime":"2018-02-01 14:32:42",
        "delFlag":1
    },
    {
        "id":276,
        "type":92,
        "typeName":"华语流行",
        "name":"远走高飞",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/HXMhMdtmZx.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/j2Dfp7pQMa.mp3",
        "authorName":"金志文",
        "videoTimeSize":"04:01",
        "createTime":"2018-02-01 14:25:09",
        "modifyTime":"2018-02-01 14:25:09",
        "delFlag":1
    },
    {
        "id":273,
        "type":92,
        "typeName":"华语流行",
        "name":"隐形的翅膀",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/zJspZX5D6A.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/6pWtGApFGf.mp3",
        "authorName":"张韶涵",
        "videoTimeSize":"03:44",
        "createTime":"2018-02-01 14:22:31",
        "modifyTime":"2018-02-01 14:22:31",
        "delFlag":1
    },
    {
        "id":269,
        "type":92,
        "typeName":"华语流行",
        "name":"一人饮酒醉",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/WBzwdDr4f3.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/sPTkkbbsKe.mp3",
        "authorName":"大鹏 &amp; MC天佑",
        "videoTimeSize":"03:18",
        "createTime":"2018-02-01 14:18:26",
        "modifyTime":"2018-02-01 14:18:26",
        "delFlag":1
    },
    {
        "id":267,
        "type":92,
        "typeName":"华语流行",
        "name":"夜空中最亮的星",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/tdQjMWaCRi.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/R5Jf4cQMmd.mp3",
        "authorName":"张杰",
        "videoTimeSize":"06:12",
        "createTime":"2018-02-01 14:16:51",
        "modifyTime":"2018-02-01 14:16:51",
        "delFlag":1
    },
    {
        "id":265,
        "type":92,
        "typeName":"华语流行",
        "name":"要不要做我女朋友",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/tFTcneedyR.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/WBfSWnFBN6.mp3",
        "authorName":"王极",
        "videoTimeSize":"03:56",
        "createTime":"2018-02-01 14:13:43",
        "modifyTime":"2018-02-01 14:13:43",
        "delFlag":1
    },
    {
        "id":263,
        "type":92,
        "typeName":"华语流行",
        "name":"痒",
        "coverUrl":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/k3y5dDJ2A5.jpg",
        "url":"http://zxcity-app.oss-cn-hangzhou.aliyuncs.com/music/AD52bS2RbH.mp3",
        "authorName":"黄龄",
        "videoTimeSize":"03:48",
        "createTime":"2018-02-01 14:11:46",
        "modifyTime":"2018-02-01 14:11:46",
        "delFlag":1
    }
]"""
class APIMusic {

    suspend fun getMusicList():List<JsonMusic>{
        return withContext(Dispatchers.IO){
            Gson().fromJson(json,object :TypeToken<ArrayList<JsonMusic>>(){}.type)
        }
    }
}