package com.sky.medialib.ui.kit.manager

import com.google.gson.Gson
import com.sky.media.kit.BaseMediaApplication
import com.sky.medialib.ui.kit.filter.MagicFilterExt
import com.sky.medialib.ui.kit.model.MagicFilterModel
import com.sky.medialib.ui.kit.model.json.magic.JsonTemplate
import com.sky.medialib.ui.kit.model.json.magic.TemplateList

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  上午10:41
 * @Email: 18971269648@163.com
 * @description:
 */
object MagicManager {

    val template = """
          {
        "count":15,
        "templates":[
            {
                "tid":"1",
                "name":"双景",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/002gQFVijx075K3QgLXW040f01000dSf0k01",
                "icon_large":"http://camera.us.sinaimg.cn/002gQFVijx075K3QiAuY040f01000dSf0k01",
                "mirrors":[
                    {
                        "mid":"1000001",
                        "tid":"1",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001lfND2jx075K3PZiDK040f010000eZ0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000002",
                        "tid":"1",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000Mqbwnjx075K3Q0BHa040f010000fU0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000003",
                        "tid":"1",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0015Uuy9jx075K3Q1ltR040f010000fb0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000004",
                        "tid":"1",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001K6jEIjx075K3Q17H1040f010000dS0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000005",
                        "tid":"1",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001LVvaijx075K3Q1joP040f010000ik0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"2",
                "name":"画框",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/001dOyt3jx075K3Qiox2040f01000eKL0k01",
                "icon_large":"http://camera.us.sinaimg.cn/001dOyt3jx075K3QiFXG040f01000eKL0k01",
                "mirrors":[
                    {
                        "mid":"1000006",
                        "tid":"2",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/002Crfcpjx075K3Q09l6040f0100039d0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000007",
                        "tid":"2",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0026gInejx075K3Q1JFS040f010003b80k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000008",
                        "tid":"2",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/002niybWjx075K3Q1RdC040f010003ac0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000009",
                        "tid":"2",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001ZrRKNjx075K3Q1XsI040f010003d50k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000010",
                        "tid":"2",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000M8kA5jx075K3Q22Fi040f010003aI0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"3",
                "name":"热浪",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/003af571jx075K3QiwkU040f01000ipV0k01",
                "icon_large":"http://camera.us.sinaimg.cn/003af571jx075K3QiQ6I040f01000ipV0k01",
                "mirrors":[
                    {
                        "mid":"1000011",
                        "tid":"3",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0028NLUYjx075K3Q1Cof040f010000kn0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000012",
                        "tid":"3",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001wp0Qkjx075K3Q2cyb040f010000kM0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000013",
                        "tid":"3",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/002rXZJZjx075K3Q27lB040f010000kQ0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000014",
                        "tid":"3",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0043CMJEjx075K3Q2XTG040f010000km0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000015",
                        "tid":"3",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000FD140jx075K3Q4utO040f010000jW0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"4",
                "name":"天际",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/000tddlnjx075K3Qj1yn040f01000iHo0k01",
                "icon_large":"http://camera.us.sinaimg.cn/000tddlnjx075K3Qj49F040f01000iHo0k01",
                "mirrors":[
                    {
                        "mid":"1000016",
                        "tid":"4",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004xXD6Pjx075K3Q2REz040f010000f20k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000017",
                        "tid":"4",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003BRsBKjx075K3Q2ZYH040f010000eL0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000018",
                        "tid":"4",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003wCfx9jx075K3Q4Maz040f010000dV0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000019",
                        "tid":"4",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001hxy1Sjx075K3Q4S9x040f010000fj0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000020",
                        "tid":"4",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003fez7zjx075K3Q4KBN040f010000e60k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"5",
                "name":"木屐",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/00166RWZjx075K3QiVji040f01000cCZ0k01",
                "icon_large":"http://camera.us.sinaimg.cn/00166RWZjx075K3QjwfB040f01000cCZ0k01",
                "mirrors":[
                    {
                        "mid":"1000021",
                        "tid":"5",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003JI4VZjx075K3Q54nC040f010000fz0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000022",
                        "tid":"5",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004AtTrwjx075K3Q4UuI040f010000fY0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000023",
                        "tid":"5",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004bEPHrjx075K3Q3xNu040f010000gx0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000024",
                        "tid":"5",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000tbOIvjx075K3Q54nC040f010000iW0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000025",
                        "tid":"5",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004m7KV0jx075K3Q52OQ040f010000iw0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"6",
                "name":"三色",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/003yjKtFjx075K3QjD0Y040f01000eVv0k01",
                "icon_large":"http://camera.us.sinaimg.cn/003yjKtFjx075K3QjqgD040f01000eVv0k01",
                "mirrors":[
                    {
                        "mid":"1000026",
                        "tid":"6",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003Srqj8jx075K3Q5wZO040f010000gs0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000027",
                        "tid":"6",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001uQKR4jx075K3Q5mAE040f010000gD0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000028",
                        "tid":"6",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/002nCNyqjx075K3Q5GCz040f010000kQ0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000029",
                        "tid":"6",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004CQFw6jx075K3Q5mAE040f010000jy0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000030",
                        "tid":"6",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001sH2drjx075K3Q5SQD040f010000gH0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"7",
                "name":"焦点",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/003PizdDjx075K3QjLBd040f01000fbv0k01",
                "icon_large":"http://camera.us.sinaimg.cn/003PizdDjx075K3Qjpuf040f01000fbv0k01",
                "mirrors":[
                    {
                        "mid":"1000031",
                        "tid":"7",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0002QFdrjx075K3Q5UVG040f010000qB0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000032",
                        "tid":"7",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004kGzNQjx075K3Q6ThB040f010000sC0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000033",
                        "tid":"7",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001hXfpOjx075K3Q7djy040f010000pb0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000034",
                        "tid":"7",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001SX6i2jx075K3Q6Zgz040f010000sY0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000035",
                        "tid":"7",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003PUow0jx075K3Q8glO040f010000pv0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"8",
                "name":"四格",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/004DINFLjx075K3QjRQj040f01000nc20k01",
                "icon_large":"http://camera.us.sinaimg.cn/004DINFLjx075K3QjKOP040f01000nc20k01",
                "mirrors":[
                    {
                        "mid":"1000036",
                        "tid":"8",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000LefyYjx075K3Q8vHq040f010000gY0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000037",
                        "tid":"8",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001wF9Wrjx075K3Q8MeP040f010000gE0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000038",
                        "tid":"8",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004hPYCOjx075K3Q9P0X040f010000fv0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000039",
                        "tid":"8",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001ohs5Pjx075K3Q9ArJ040f010000kf0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000040",
                        "tid":"8",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/002jQdUajx075K3Qa3Ab040f010000kp0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"9",
                "name":"印象",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/003J2fZpjx075K3QjKyI040f01000fSm0k01",
                "icon_large":"http://camera.us.sinaimg.cn/003J2fZpjx075K3Qk9Nd040f01000fSm0k01",
                "mirrors":[
                    {
                        "mid":"1000041",
                        "tid":"9",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004thHl7jx075K3Qb0nm040f0100044H0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000042",
                        "tid":"9",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003bgIS5jx075K3Qb5zW040f0100041x0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000043",
                        "tid":"9",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/002TMAERjx075K3Q9FUr040f010004480k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000044",
                        "tid":"9",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0002cSXQjx075K3Q9Nsb040f0100041X0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000045",
                        "tid":"9",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001IZsajjx075K3QbkVy040f0100041w0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"10",
                "name":"双骄",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/002cF6LCjx075K3QiLqn040f01000k6B0k01",
                "icon_large":"http://camera.us.sinaimg.cn/002cF6LCjx075K3Qk1Zl040f01000k6B0k01",
                "mirrors":[
                    {
                        "mid":"1000046",
                        "tid":"10",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/002L09jfjx075K3Q9YDJ040f010000gC0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000047",
                        "tid":"10",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/002H8dzSjx075K3QbfIY040f010000fp0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000048",
                        "tid":"10",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004pUbVsjx075K3Qab7W040f010000g30k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000049",
                        "tid":"10",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003zifVTjx075K3QbKGk040f010000fW0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000050",
                        "tid":"10",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0037Zwxpjx075K3QbsZx040f010000gC0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"11",
                "name":"回忆",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/000q8oAjjx075K3QkvUb040f01000klH0k01",
                "icon_large":"http://camera.us.sinaimg.cn/000q8oAjjx075K3QkcEE040f01000klH0k01",
                "mirrors":[
                    {
                        "mid":"1000051",
                        "tid":"11",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004wb1y8jx075K3QanSg040f010001QN0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000052",
                        "tid":"11",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000p6ToJjx075K3Qc0i3040f010001R80k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000053",
                        "tid":"11",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001juMdbjx075K3QbzKT040f010001Qu0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000054",
                        "tid":"11",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/00110Q7Ejx075K3QaGRG040f010001Q10k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000055",
                        "tid":"11",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000foIgTjx075K3QbRXV040f010001Sb0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"12",
                "name":"栅格",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/002F8Xicjx075K3QknA3040f01000lur0k01",
                "icon_large":"http://camera.us.sinaimg.cn/002F8Xicjx075K3QkFwX040f01000lur0k01",
                "mirrors":[
                    {
                        "mid":"1000056",
                        "tid":"12",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000sGrMsjx075K3QcrRJ040f010000hM0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000057",
                        "tid":"12",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/002PUQUrjx075K3QcAYg040f010000lx0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000058",
                        "tid":"12",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000bGYwkjx075K3QcJ2f040f010000i40k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000059",
                        "tid":"12",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003j9yjtjx075K3QcuZi040f010000ig0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000060",
                        "tid":"12",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0031fzTPjx075K3QcsE7040f010000hp0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"13",
                "name":"风车",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/003HJ6Pdjx075K3QkRL2040f01000hZ30k01",
                "icon_large":"http://camera.us.sinaimg.cn/003HJ6Pdjx075K3QjqwL040f01000hZ30k01",
                "mirrors":[
                    {
                        "mid":"1000061",
                        "tid":"13",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/002yzFySjx075K3QdMK3040f010000uc0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000062",
                        "tid":"13",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0037wRhejx075K3Qem7C040f010000uB0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000063",
                        "tid":"13",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004vbnAAjx075K3QeuHS040f010000us0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000064",
                        "tid":"13",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0014PGTSjx075K3Qff0Q040f010000zB0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000065",
                        "tid":"13",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/001eAW6Qjx075K3Qfjr1040f010000Ap0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"14",
                "name":"风铃",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/002O5GU2jx075K3QkHS7040f01000faR0k01",
                "icon_large":"http://camera.us.sinaimg.cn/002O5GU2jx075K3QkGzu040f01000faR0k01",
                "mirrors":[
                    {
                        "mid":"1000066",
                        "tid":"14",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000N3Vhtjx075K3Qe9Tx040f010000iL0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000067",
                        "tid":"14",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/004ykd84jx075K3QfJbN040f010000hF0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000068",
                        "tid":"14",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0029IlGVjx075K3Qej03040f010000iO0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000069",
                        "tid":"14",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000ARTyPjx075K3QgNMQ040f010000i00k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000070",
                        "tid":"14",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003vOOr5jx075K3QgAg7040f010000l20k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            },
            {
                "tid":"15",
                "name":"热恋",
                "status":"1",
                "mtime":"2016-10-19 16:18:14",
                "tag_type":"1",
                "icon_small":"http://camera.us.sinaimg.cn/004hxGWdjx075K3QldlK040f01000l4h0k01",
                "icon_large":"http://camera.us.sinaimg.cn/004hxGWdjx075K3QkYMw040f01000l4h0k01",
                "mirrors":[
                    {
                        "mid":"1000071",
                        "tid":"15",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/002UW7Vmjx075K3Qi3cs040f010002QW0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000072",
                        "tid":"15",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/000qjDXUjx075K3QhMyb040f010002Ro0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000073",
                        "tid":"15",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003IQwYyjx075K3QidBC040f010002RA0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000074",
                        "tid":"15",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/003iNPYdjx075K3Qijks040f010002RX0k01",
                        "mtime":"2016-10-19 16:17:59"
                    },
                    {
                        "mid":"1000075",
                        "tid":"15",
                        "status":"1",
                        "zip_url":"http://camera.us.sinaimg.cn/0020cP7Ejx075K3QikD5040f010002RM0k01",
                        "mtime":"2016-10-19 16:17:59"
                    }
                ]
            }
        ]
    }
    """.trimIndent()

    private var jsonTemplates = listOf<JsonTemplate>()



    open fun parseJsonList() {
        jsonTemplates = Gson().fromJson<TemplateList>(template,TemplateList::class.java).templates
        if(jsonTemplates != null){
            val list = mutableListOf<MagicFilterExt>()
            jsonTemplates.forEach {
                list.add(covertFilter(it))
            }
            ToolFilterManager.addMagicFilters(list)
        }

    }

    private fun covertFilter(jsonTemplate: JsonTemplate): MagicFilterExt {
        return MagicFilterExt(BaseMediaApplication.sContext,jsonTemplate)
    }


    fun getMagicFilters():MutableList<MagicFilterModel>{
        return arrayListOf()
    }


}