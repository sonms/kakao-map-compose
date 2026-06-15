package com.sonms.kakao.maps.open.map.compose.core

import com.kakao.vectormap.KakaoMap as KakaoMapSdk
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.label.LodLabel
import com.kakao.vectormap.label.LodLabelLayer
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineManager
import com.kakao.vectormap.shape.Polygon
import com.kakao.vectormap.shape.Polyline
import com.kakao.vectormap.shape.ShapeLayer
import com.kakao.vectormap.shape.ShapeManager

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
    private val state: KakaoMapState,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()
    private var isDisposed = false

    override fun onRemoved() {
        if (isDisposed) return
        isDisposed = true
        children.forEach { it.onRemoved() }
        children.clear()
        if (state.map != null) labelManager.remove(labelLayer)
    }

    override fun onCleared() {
        if (isDisposed) return
        isDisposed = true
        children.forEach { it.onCleared() }
        children.clear()
        if (state.map != null) labelManager.remove(labelLayer)
    }
}

// 커스텀 RouteLineLayer 컨테이너 — KakaoRouteLineLayer composable이 소유합니다.
internal class RouteLineLayerNode(
    val routeLineLayer: RouteLineLayer,
    private val routeLineManager: RouteLineManager,
    private val state: KakaoMapState,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()
    private var isDisposed = false

    override fun onRemoved() {
        if (isDisposed) return
        isDisposed = true
        children.forEach { it.onRemoved() }
        children.clear()
        if (state.map != null) routeLineManager.remove(routeLineLayer)
    }

    override fun onCleared() {
        if (isDisposed) return
        isDisposed = true
        children.forEach { it.onCleared() }
        children.clear()
        if (state.map != null) routeLineManager.remove(routeLineLayer)
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
        if (state.map != null) label.remove()
    }

    override fun onCleared() {
        state.unregisterLabelClickHandler(label)
        if (state.map != null) label.remove()
    }
}

internal class RouteLineNode(
    var routeLine: RouteLine,
    private val routeLineLayer: RouteLineLayer,
    val state: KakaoMapState,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()

    override fun onRemoved() {
        // RouteLine.remove()는 내부 lineMap을 정리하지 않아 재생성 시 충돌.
        // RouteLineLayer.remove()를 사용해야 lineMap에서도 제거됨.
        if (state.map != null) routeLineLayer.remove(routeLine)
    }

    override fun onCleared() {
        if (state.map != null) routeLineLayer.remove(routeLine)
    }
}

internal class ShapeLayerNode(
    val shapeLayer: ShapeLayer,
    private val shapeManager: ShapeManager,
    private val state: KakaoMapState,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()
    private var isDisposed = false

    override fun onRemoved() {
        if (isDisposed) return
        isDisposed = true
        children.forEach { it.onRemoved() }
        children.clear()
        if (state.map != null) shapeManager.remove(shapeLayer)
    }

    override fun onCleared() {
        if (isDisposed) return
        isDisposed = true
        children.forEach { it.onCleared() }
        children.clear()
        if (state.map != null) shapeManager.remove(shapeLayer)
    }
}

internal class PolylineNode(
    var polyline: Polyline,
    private val shapeLayer: ShapeLayer,
    val state: KakaoMapState,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()

    override fun onRemoved() {
        // Polyline.remove()는 내부 polylineMap을 정리하지 않아 재생성 시 충돌.
        // ShapeLayer.remove()를 사용해야 polylineMap에서도 제거됨.
        if (state.map != null) shapeLayer.remove(polyline)
    }

    override fun onCleared() {
        if (state.map != null) shapeLayer.remove(polyline)
    }
}

internal class PolygonNode(
    var polygon: Polygon,
    private val shapeLayer: ShapeLayer,
    val state: KakaoMapState,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()

    override fun onRemoved() {
        // Polygon.remove()는 내부 polygonMap을 정리하지 않아 재생성 시 충돌.
        // ShapeLayer.remove()를 사용해야 polygonMap에서도 제거됨.
        if (state.map != null) shapeLayer.remove(polygon)
    }

    override fun onCleared() {
        if (state.map != null) shapeLayer.remove(polygon)
    }
}

internal class LodPoiNode(
    val lodLabelLayer: LodLabelLayer,
    private val labelManager: LabelManager,
    var onClick: ((LodLabel) -> Boolean)?,
    val state: KakaoMapState,
) : KakaoMapNode {
    override val children: MutableList<KakaoMapNode> = mutableListOf()

    override fun onAttached() {
        val handler = onClick ?: return
        state.registerLodLabelClickHandler(lodLabelLayer) { handler(it) }
    }

    override fun onRemoved() {
        state.unregisterLodLabelClickHandler(lodLabelLayer)
        if (state.map != null) {
            lodLabelLayer.removeAll()
            labelManager.remove(lodLabelLayer)
        }
    }

    override fun onCleared() {
        state.unregisterLodLabelClickHandler(lodLabelLayer)
        if (state.map != null) {
            lodLabelLayer.removeAll()
            labelManager.remove(lodLabelLayer)
        }
    }
}
