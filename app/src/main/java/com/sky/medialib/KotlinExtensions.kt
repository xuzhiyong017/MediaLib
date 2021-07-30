
import android.content.res.Resources
import android.util.TypedValue

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  下午6:26
 * @Email: 18971269648@163.com
 * @description:
 */

val Float.px
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,this, Resources.getSystem().displayMetrics)

val Int.dp
    get() = this.toFloat().px