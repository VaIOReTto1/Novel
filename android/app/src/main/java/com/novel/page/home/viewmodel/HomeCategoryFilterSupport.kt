package com.novel.page.home.viewmodel

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

object HomeCategoryFilterSupport {

    const val HOME_FILTER_ID = "0"
    const val HOME_FILTER_LABEL = "首页"
    const val LEGACY_RECOMMEND_FILTER_LABEL = "推荐"

    fun homeCategoryFilter(): CategoryInfo {
        return CategoryInfo(HOME_FILTER_ID, HOME_FILTER_LABEL)
    }

    fun isHomeFilter(categoryName: String): Boolean {
        return categoryName == HOME_FILTER_LABEL || categoryName == LEGACY_RECOMMEND_FILTER_LABEL
    }

    fun normalizeSelectedFilter(categoryName: String): String {
        return if (isHomeFilter(categoryName)) HOME_FILTER_LABEL else categoryName
    }

    fun normalizeFilters(filters: Iterable<CategoryInfo>): ImmutableList<CategoryInfo> {
        val normalized = mutableListOf(homeCategoryFilter())
        val seenIds = linkedSetOf(HOME_FILTER_ID)

        filters.forEach { filter ->
            if (filter.id == HOME_FILTER_ID || isHomeFilter(filter.name)) {
                return@forEach
            }
            if (seenIds.add(filter.id)) {
                normalized += filter
            }
        }

        return normalized.toImmutableList()
    }
}
