package com.joker.coolmallkmp.core.model.entity

/**
 * 商品分类树（UI 展示用，非网络直接反序列化）
 */
data class CategoryTree(
    val id: Long,
    val name: String,
    val parentId: Int?,
    val sortNum: Int,
    val pic: String?,
    val status: Int,
    val createTime: String?,
    val updateTime: String?,
    val children: List<CategoryTree> = emptyList(),
) {
    companion object {
        fun fromCategory(category: Category): CategoryTree {
            return CategoryTree(
                id = category.id,
                name = category.name,
                parentId = category.parentId,
                sortNum = category.sortNum,
                pic = category.pic,
                status = category.status,
                createTime = category.createTime,
                updateTime = category.updateTime,
            )
        }

        fun fromCategories(categories: List<Category>): List<CategoryTree> {
            val sortedList = categories.sortedBy { it.sortNum }
            val categoryTrees = sortedList.map(::fromCategory)
            val rootCategories = mutableListOf<CategoryTree>()
            val childrenMap = mutableMapOf<Long, MutableList<CategoryTree>>()

            categoryTrees.forEach { categoryTree ->
                val parentId = categoryTree.parentId
                if (parentId == null) {
                    rootCategories.add(categoryTree)
                } else {
                    childrenMap.getOrPut(parentId.toLong()) { mutableListOf() }.add(categoryTree)
                }
            }

            fun build(node: CategoryTree): CategoryTree {
                val children = childrenMap[node.id].orEmpty()
                if (children.isEmpty()) return node
                return node.copy(children = children.map(::build))
            }

            return rootCategories.map(::build)
        }
    }
}
