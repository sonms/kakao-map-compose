package com.sonms.kakao.maps.open.map.compose.overlay.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.kakao.vectormap.route.RouteLineOptions
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapApplier
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.RouteLineLayerNode
import com.sonms.kakao.maps.open.map.compose.core.RouteLineNode
import com.sonms.kakao.maps.open.map.compose.model.KakaoRouteSegment
import java.util.UUID

/**
 * 구간마다 다른 스타일(색상, 두께, 패턴)을 적용할 수 있는 멀티 파트 경로선을 표시하는 Composable입니다.
 *
 * 내부적으로 단일 SDK [com.kakao.vectormap.route.RouteLine] 객체에 여러 [com.kakao.vectormap.route.RouteLineSegment]를
 * 전달하여 구간 경계에서 시각적 이음새가 생기지 않도록 합니다.
 * 2개 미만의 좌표를 가진 구간은 자동으로 제외되며, 유효한 구간이 하나도 없으면 아무것도 그리지 않습니다.
 *
 * 구간을 이어 붙이려면 이전 구간의 마지막 좌표와 다음 구간의 첫 좌표를 같은 값으로 맞춰야 자연스럽게 연결됩니다.
 *
 * @param segments 각 구간의 좌표와 스타일 목록입니다.
 * @param lineId SDK RouteLine에 부여할 식별자입니다. 변경 시 기존 RouteLine을 제거하고 재생성합니다.
 * @param visible 경로선 표시 여부입니다.
 * @param zOrder 경로선 표시 순서입니다. 값이 클수록 위에 표시됩니다.
 * @param tag 경로선에 연결할 문자열 태그입니다.
 */
@Composable
@KakaoMapComposable
@Suppress("ComposableTargetMismatch")
fun KakaoMultiRouteLine(
    segments: List<KakaoRouteSegment>,
    lineId: String? = null,
    visible: Boolean = true,
    zOrder: Int = 0,
    tag: String? = null,
) {
    val mapApplier = currentComposer.applier as? KakaoMapApplier ?: return
    val routeLineLayer = (mapApplier.current as? RouteLineLayerNode)?.routeLineLayer
        ?: mapApplier.mapRoot.kakaoMap.routeLineManager?.layer
        ?: return
    val effectiveLineId = remember(lineId) { lineId ?: UUID.randomUUID().toString() }
    val context = LocalContext.current
    val density = LocalDensity.current

    val segmentSpecs = remember(segments, density) {
        segments
            .filter { it.positions.size >= 2 }
            .map { seg ->
                RouteLineSegmentSpec(
                    positions = seg.positions,
                    lineWidth = seg.lineWidth,
                    lineColor = seg.lineColor,
                    strokeWidth = seg.strokeWidth,
                    strokeColor = seg.strokeColor,
                    sdkPattern = seg.pattern?.toSdkPattern(context, density),
                )
            }
    }
    val canDraw = segmentSpecs.isNotEmpty()

    key(lineId) {
        if (canDraw) {
            ComposeNode<RouteLineNode, KakaoMapApplier>(
                factory = {
                    val routeLine = routeLineLayer.addRouteLine(
                        RouteLineOptions.from(effectiveLineId, segmentSpecs.map { it.toSegment() })
                            .setVisible(visible)
                            .setZOrder(zOrder)
                            .setTag(tag),
                    )
                    RouteLineNode(
                        routeLine = routeLine,
                        routeLineLayer = routeLineLayer,
                        state = mapApplier.mapRoot.state,
                    )
                },
                update = {
                    set(segmentSpecs) { routeLine.changeSegments(it.map { s -> s.toSegment() }) }
                    set(visible) { if (it) routeLine.show() else routeLine.hide() }
                    set(zOrder) { routeLine.zOrder = it }
                    set(tag) { routeLine.tag = it }
                },
            )
        }
    }
}
