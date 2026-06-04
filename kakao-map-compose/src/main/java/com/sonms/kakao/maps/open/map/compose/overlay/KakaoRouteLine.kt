package com.sonms.kakao.maps.open.map.compose.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.LocalKakaoMapState
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng

/**
 * 카카오 지도 위에 경로선을 표시하는 Composable입니다.
 *
 * 좌표가 2개 미만이면 SDK RouteLine을 생성하지 않습니다. 현재 구현은 POI와 동일하게
 * 커스텀 Applier 없이 SDK 객체 생성/갱신/삭제를 부수 효과로 관리합니다.
 *
 * @param positions 경로선을 구성할 위도/경도 좌표 목록입니다.
 * @param lineWidth 경로선의 선 두께입니다.
 * @param lineColor 경로선의 선 색상입니다.
 * @param strokeWidth 경로선 외곽선 두께입니다.
 * @param strokeColor 경로선 외곽선 색상입니다.
 * @param lineId SDK RouteLine에 부여할 식별자입니다. null이면 SDK가 기본 방식으로 생성합니다.
 * @param visible 경로선 표시 여부입니다.
 * @param zOrder 경로선 표시 순서입니다. 값이 클수록 위에 표시됩니다.
 * @param tag 경로선에 연결할 문자열 태그입니다.
 */
@Composable
@KakaoMapComposable
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
    val state = LocalKakaoMapState.current
    val map = state?.map ?: return
    val canDraw = positions.size >= MIN_ROUTE_LINE_POINT_COUNT
    var routeLine by remember(map, lineId) { mutableStateOf<RouteLine?>(null) }

    DisposableEffect(map, lineId, canDraw) {
        if (!canDraw) {
            routeLine = null
            return@DisposableEffect onDispose {}
        }

        val layer = map.routeLineManager?.layer
            ?: return@DisposableEffect onDispose {}
        val segment = routeLineSegment(
            positions = positions,
            lineWidth = lineWidth,
            lineColor = lineColor,
            strokeWidth = strokeWidth,
            strokeColor = strokeColor,
        )
        val options = if (lineId == null) {
            RouteLineOptions.from(segment)
        } else {
            RouteLineOptions.from(lineId, segment)
        }
        routeLine = layer.addRouteLine(
            options
                .setVisible(visible)
                .setZOrder(zOrder)
                .setTag(tag),
        )

        onDispose {
            routeLine?.remove()
            routeLine = null
        }
    }

    DisposableEffect(
        routeLine,
        positions,
        lineWidth,
        lineColor,
        strokeWidth,
        strokeColor,
        visible,
        zOrder,
        tag,
        canDraw,
    ) {
        val currentRouteLine = routeLine
        if (currentRouteLine == null || !canDraw) {
            return@DisposableEffect onDispose {}
        }

        currentRouteLine.changeSegments(
            routeLineSegment(
                positions = positions,
                lineWidth = lineWidth,
                lineColor = lineColor,
                strokeWidth = strokeWidth,
                strokeColor = strokeColor,
            ),
        )
        if (visible) {
            currentRouteLine.show()
        } else {
            currentRouteLine.hide()
        }
        if (currentRouteLine.zOrder != zOrder) {
            currentRouteLine.zOrder = zOrder
        }
        currentRouteLine.tag = tag
        onDispose {}
    }
}

/**
 * Compose API 파라미터를 카카오 지도 SDK의 [RouteLineSegment]로 변환합니다.
 *
 * @param positions 경로선을 구성할 위도/경도 좌표 목록입니다.
 * @param lineWidth 경로선의 선 두께입니다.
 * @param lineColor 경로선의 선 색상입니다.
 * @param strokeWidth 경로선 외곽선 두께입니다.
 * @param strokeColor 경로선 외곽선 색상입니다.
 */
private fun routeLineSegment(
    positions: List<KakaoLatLng>,
    lineWidth: Float,
    lineColor: Color,
    strokeWidth: Float,
    strokeColor: Color,
): RouteLineSegment =
    RouteLineSegment.from(
        positions.map { it.toLatLng() },
        RouteLineStyle.from(lineWidth, lineColor.toArgb(), strokeWidth, strokeColor.toArgb()),
    )

private const val MIN_ROUTE_LINE_POINT_COUNT = 2
