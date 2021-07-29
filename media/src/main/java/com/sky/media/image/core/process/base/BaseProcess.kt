package com.sky.media.image.core.process.base

import com.sky.media.image.core.filter.Adjuster
import com.sky.media.image.core.filter.Filter
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author: xuzhiyong
 * @date: 2021/7/27  下午5:27
 * @Email: 18971269648@163.com
 * @description:
 */
abstract class BaseProcess {

    protected var mUsedFilters =  CopyOnWriteArrayList<Filter>()

    open fun initFilters(list: List<Filter>) {
        mUsedFilters.clear()
        mUsedFilters.addAll(list)
    }

    open fun clearFilters() {
        mUsedFilters.clear()
    }

    open fun addFilter(filter: Filter?) {
        if (filter != null && !mUsedFilters.contains(filter)) {
            mUsedFilters.add(filter)
        }
    }

    open fun addFilter(i: Int, filter: Filter?) {
        if (filter != null && !mUsedFilters.contains(filter)) {
            mUsedFilters.add(i, filter)
        }
    }

    open fun setFilter(i: Int, filter: Filter?) {
        if (filter != null) {
            mUsedFilters[i] = filter
        }
    }

    open fun removeFilter(i: Int) {
        val filter = mUsedFilters.removeAt(i) as Filter?
        if (filter != null) {
            val adjuster: Adjuster? = filter.adjuster
            adjuster?.adjust(adjuster.initProgress)
        }
    }

    open fun removeFilter(filter: Filter) {
        mUsedFilters.remove(filter)
        val adjuster: Adjuster? = filter.adjuster
        adjuster?.adjust(adjuster.initProgress)
    }

    open fun getUsedFilters(): List<Filter?>? {
        return mUsedFilters
    }

    open fun isUsedFilter(filter: Filter?): Boolean {
        return mUsedFilters.contains(filter)
    }
}