package com.sonms.kakao.maps.open.map.compose.overlay.route

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.model.KakaoRoutePattern
import com.sonms.kakao.maps.open.map.compose.model.RouteAnimationPoi
import com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoPoi
import kotlinx.coroutines.flow.first

/**
 * 경로선을 [durationMs] 시간 동안 점진적으로 그리는 애니메이션 Composable입니다.
 *
 * - 경로선은 선형 보간으로 매 프레임 좌표를 갱신합니다.
 * - [pois]의 각 POI는 경로선이 해당 위치를 지나칠 때 0 → 원래 크기로 확대되는 애니메이션과 함께 등장합니다.
 * - POI 아이콘 크기는 5단계로 이산화하여 SDK `setStyles()` 호출을 최소화합니다.
 * - [positions]가 변경되면 애니메이션이 처음부터 재시작됩니다.
 *
 * @param positions 경로를 구성하는 위도/경도 좌표 목록입니다. 2개 미만이면 렌더링하지 않습니다.
 * @param durationMs 경로선 전체를 그리는 데 걸리는 시간(ms)입니다.
 * @param lineWidth 경로선 두께입니다.
 * @param lineColor 경로선 색상입니다.
 * @param strokeWidth 경로선 외곽선 두께입니다.
 * @param strokeColor 경로선 외곽선 색상입니다.
 * @param pattern 경로선 패턴입니다.
 * @param lineId 경로선 SDK 식별자입니다.
 * @param visible 경로선 표시 여부입니다.
 * @param zOrder 경로선 표시 순서입니다.
 * @param pois 경로 위에 표시할 POI 목록입니다.
 */
@Composable
@KakaoMapComposable
@Suppress("ComposableTargetMismatch")
fun KakaoAnimatedRouteLine(
    positions: List<KakaoLatLng>,
    durationMs: Int = 1500,
    lineWidth: Float = 12f,
    lineColor: Color = Color(0xFF3478F6),
    strokeWidth: Float = 0f,
    strokeColor: Color = Color.Transparent,
    pattern: KakaoRoutePattern? = null,
    lineId: String? = null,
    visible: Boolean = true,
    zOrder: Int = 0,
    pois: List<RouteAnimationPoi> = emptyList(),
) {
    val routeProgress = remember(positions) { Animatable(0f) }
    val cumulative = remember(positions) { computeCumulativeDistances(positions) }
    val poisWithFraction = remember(pois, positions, cumulative) {
        pois.map { poi ->
            poi to (poi.routeFraction ?: computePoiFraction(poi.position, positions, cumulative))
        }
    }

    LaunchedEffect(positions, durationMs) {
        if (positions.size >= 2) {
            routeProgress.snapTo(0f)
            routeProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMs, easing = LinearEasing),
            )
        }
    }

    val progress = routeProgress.value
    val currentPositions = interpolateRoute(positions, progress, cumulative)

    KakaoRouteLine(
        positions = currentPositions,
        lineWidth = lineWidth,
        lineColor = lineColor,
        strokeWidth = strokeWidth,
        strokeColor = strokeColor,
        pattern = pattern,
        lineId = lineId,
        visible = visible,
        zOrder = zOrder,
    )

    poisWithFraction.forEachIndexed { index, (poi, fraction) ->
        key(poi.labelId ?: index) {
            val scaleAnim = remember { Animatable(0f) }

            LaunchedEffect(fraction, positions) {
                scaleAnim.snapTo(0f)
                snapshotFlow { routeProgress.value }.first { it >= fraction }
                scaleAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(poi.appearDurationMs, easing = FastOutSlowInEasing),
                )
            }

            // 5단계 이산화: SDK setStyles() 호출을 최대 5회로 제한합니다.
            val scaleStep = (scaleAnim.value * 5).toInt().coerceIn(0, 5)
            if (scaleStep > 0) {
                val scaleFrac = scaleStep / 5f
                KakaoPoi(
                    position = poi.position,
                    icon = poi.icon,
                    iconSize = DpSize(poi.fullSize * scaleFrac, poi.fullSize * scaleFrac),
                    labelId = poi.labelId,
                    onClick = poi.onClick,
                    visible = true,
                )
            }
        }
    }
}
