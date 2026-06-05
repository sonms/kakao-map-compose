package com.sonms.kakao.maps.open.map.compose.core

import androidx.compose.runtime.AbstractApplier

internal class KakaoMapApplier(
    root: KakaoMapNodeRoot,
) : AbstractApplier<KakaoMapNode>(root) {

    internal val mapRoot: KakaoMapNodeRoot = root

    override fun insertTopDown(index: Int, instance: KakaoMapNode) {
        // insertBottomUp을 사용하므로 비워둡니다
    }

    override fun insertBottomUp(index: Int, instance: KakaoMapNode) {
        current.children.add(index, instance)
        instance.onAttached()
    }

    override fun remove(index: Int, count: Int) {
        repeat(count) {
            current.children.removeAt(index).onRemoved()
        }
    }

    override fun move(from: Int, to: Int, count: Int) {
        val children = current.children
        val items = children.subList(from, from + count).toList()
        children.subList(from, from + count).clear()
        val dest = if (to > from) to - count else to
        children.addAll(dest, items)
    }

    override fun onClear() {
        root.onCleared()
    }
}
