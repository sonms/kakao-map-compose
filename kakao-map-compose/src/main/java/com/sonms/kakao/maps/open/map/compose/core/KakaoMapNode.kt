package com.sonms.kakao.maps.open.map.compose.core

import com.kakao.vectormap.KakaoMap as KakaoMapSdk
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineManager

internal interface KakaoMapNode {
    val children: MutableList<KakaoMapNode>
    fun onAttached() {}
    fun onRemoved() {}
    fun onCleared() {}
}

internal class KakaoMapNodeRoot(
    val kakaoMap: KakaoMapSdk,
    val state: KakaoMapState,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()

    override fun onCleared() {
        children.forEach { it.onCleared() }
        children.clear()
    }
}

// 커스텀 LabelLayer 컨테이너 — KakaoLabelLayer composable이 소유합니다.
internal class LabelLayerNode(
    val labelLayer: LabelLayer,
    private val labelManager: LabelManager,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()

    override fun onRemoved() {
        children.forEach { it.onRemoved() }
        children.clear()
        labelManager.remove(labelLayer)
    }

    override fun onCleared() {
        children.forEach { it.onCleared() }
        children.clear()
        labelManager.remove(labelLayer)
    }
}

// 커스텀 RouteLineLayer 컨테이너 — KakaoRouteLineLayer composable이 소유합니다.
internal class RouteLineLayerNode(
    val routeLineLayer: RouteLineLayer,
    private val routeLineManager: RouteLineManager,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()

    override fun onRemoved() {
        children.forEach { it.onRemoved() }
        children.clear()
        routeLineManager.remove(routeLineLayer)
    }

    override fun onCleared() {
        children.forEach { it.onCleared() }
        children.clear()
        routeLineManager.remove(routeLineLayer)
    }
}

internal class PoiNode(
    var label: Label,
    var onClick: (Label) -> Boolean,
    val state: KakaoMapState,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()

    override fun onAttached() {
        state.registerLabelClickHandler(label) { this.onClick(it) }
    }

    override fun onRemoved() {
        state.unregisterLabelClickHandler(label)
        label.remove()
    }

    override fun onCleared() {
        state.unregisterLabelClickHandler(label)
        label.remove()
    }
}

internal class RouteLineNode(
    var routeLine: RouteLine,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()

    override fun onRemoved() {
        routeLine.remove()
    }

    override fun onCleared() {
        routeLine.remove()
    }
}
