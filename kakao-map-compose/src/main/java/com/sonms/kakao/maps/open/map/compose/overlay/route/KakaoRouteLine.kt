package com.sonms.kakao.maps.open.map.compose.overlay.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapApplier
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.RouteLineLayerNode
import com.sonms.kakao.maps.open.map.compose.core.RouteLineNode
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng

/**
 * 카카오 지도 위에 경로선을 표시하는 Composable입니다.
 *
 * 좌표가 2개 미만이면 SDK RouteLine을 생성하지 않으며, 이후 2개 이상이 되면 자동으로 재생성합니다.
 * [com.sonms.kakao.maps.open.map.compose.core.KakaoMap]의 content 안에서 선언하면,
 * 파라미터 변경에 따라 지도 위 경로선이 자동으로 생성, 갱신, 제거됩니다.
 *
 * @param positions 경로선을 구성할 위도/경도 좌표 목록입니다.
 * @param lineWidth 경로선의 선 두께입니다.
 * @param lineColor 경로선의 선 색상입니다.
 * @param strokeWidth 경로선 외곽선 두께입니다.
 * @param strokeColor 경로선 외곽선 색상입니다.
 * @param lineId SDK RouteLine에 부여할 식별자입니다. 변경 시 기존 RouteLine을 제거하고 재생성합니다.
 * @param visible 경로선 표시 여부입니다.
 * @param zOrder 경로선 표시 순서입니다. 값이 클수록 위에 표시됩니다.
 * @param tag 경로선에 연결할 문자열 태그입니다.
 */
@Composable
@KakaoMapComposable
@Suppress("ComposableTargetMismatch")
fun KakaoRouteLine(
    positions: List<KakaoLatLng>,
    lineWidth: Float = 12f,
    lineColor: Color = Color(0xFF3478F6),
    strokeWidth: Float = 0f,
    strokeColor: Color = Color.Transparent,
    lineId: String? = null,
    visible: Boolean = true,
    zOrder: Int = 0,
    tag: String? = null,
) {
    val mapApplier = currentComposer.applier as? KakaoMapApplier ?: return
    val routeLineLayer = (mapApplier.current as? RouteLineLayerNode)?.routeLineLayer
        ?: mapApplier.mapRoot.kakaoMap.routeLineManager?.layer
        ?: return
    val canDraw = positions.size >= MIN_ROUTE_LINE_POINT_COUNT

    val segmentSpec = RouteLineSegmentSpec(positions, lineWidth, lineColor, strokeWidth, strokeColor)

    key(lineId) {
        if (canDraw) {
            ComposeNode<RouteLineNode, KakaoMapApplier>(
                factory = {
                    val options = if (lineId == null) {
                        RouteLineOptions.from(segmentSpec.toSegment())
                    } else {
                        RouteLineOptions.from(lineId, segmentSpec.toSegment())
                    }
                    val routeLine = routeLineLayer.addRouteLine(
                        options
                            .setVisible(visible)
                            .setZOrder(zOrder)
                            .setTag(tag),
                    )
                    RouteLineNode(routeLine = routeLine)
                },
                update = {
                    // positions/style 파라미터를 하나의 spec으로 묶어 changeSegments() 단일 호출
                    set(segmentSpec) { routeLine.changeSegments(it.toSegment()) }
                    set(visible) { if (it) routeLine.show() else routeLine.hide() }
                    set(zOrder) { routeLine.zOrder = it }
                    set(tag) { routeLine.tag = it }
                },
            )
        }
    }
}

private data class RouteLineSegmentSpec(
    val positions: List<KakaoLatLng>,
    val lineWidth: Float,
    val lineColor: Color,
    val strokeWidth: Float,
    val strokeColor: Color,
) {
    fun toSegment(): RouteLineSegment =
        RouteLineSegment.from(
            positions.map { it.toLatLng() },
            RouteLineStyle.from(lineWidth, lineColor.toArgb(), strokeWidth, strokeColor.toArgb()),
        )
}

private const val MIN_ROUTE_LINE_POINT_COUNT = 2
