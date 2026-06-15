package com.sonms.kakao.maps.open.map.compose.overlay.shape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.UUID
import com.kakao.vectormap.shape.MapPoints
import com.kakao.vectormap.shape.PolylineOptions
import com.kakao.vectormap.shape.PolylineStyle
import com.kakao.vectormap.shape.PolylineStyles
import com.kakao.vectormap.shape.PolylineStylesSet
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapApplier
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.PolylineNode
import com.sonms.kakao.maps.open.map.compose.core.ShapeLayerNode
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng

/**
 * 카카오 지도 위에 폴리라인(선)을 표시하는 Composable입니다.
 *
 * 좌표가 2개 미만이면 SDK Polyline을 생성하지 않으며, 이후 2개 이상이 되면 자동으로 재생성합니다.
 * [com.sonms.kakao.maps.open.map.compose.core.KakaoMap]의 content 안에서 선언하면,
 * 파라미터 변경에 따라 지도 위 선이 자동으로 생성, 갱신, 제거됩니다.
 *
 * @param positions 폴리라인을 구성할 위도/경도 좌표 목록입니다.
 * @param lineWidth 선의 두께입니다.
 * @param lineColor 선의 색상입니다.
 * @param strokeWidth 선 외곽선 두께입니다.
 * @param strokeColor 선 외곽선 색상입니다.
 * @param polylineId SDK Polyline에 부여할 식별자입니다. 변경 시 기존 Polyline을 제거하고 재생성합니다.
 * @param visible 폴리라인 표시 여부입니다.
 * @param zOrder 폴리라인 표시 순서입니다. 값이 클수록 위에 표시됩니다.
 * @param tag 폴리라인에 연결할 태그입니다.
 */
@Composable
@KakaoMapComposable
@Suppress("ComposableTargetMismatch")
fun KakaoPolyline(
    positions: List<KakaoLatLng>,
    lineWidth: Float = 4f,
    lineColor: Color = Color(0xFF3478F6),
    strokeWidth: Float = 0f,
    strokeColor: Color = Color.Transparent,
    polylineId: String? = null,
    visible: Boolean = true,
    zOrder: Int = 0,
    tag: String? = null,
) {
    val mapApplier = currentComposer.applier as? KakaoMapApplier ?: return
    val shapeLayer = (mapApplier.current as? ShapeLayerNode)?.shapeLayer
        ?: mapApplier.mapRoot.kakaoMap.shapeManager?.layer
        ?: return
    val canDraw = positions.size >= MIN_POLYLINE_POINT_COUNT
    val effectiveId = remember(polylineId) { polylineId ?: UUID.randomUUID().toString() }
    val spec = PolylineShapeSpec(positions, lineWidth, lineColor, strokeWidth, strokeColor)

    key(polylineId) {
        if (canDraw) {
            ComposeNode<PolylineNode, KakaoMapApplier>(
                factory = {
                    val options = polylineOptionsWithId(effectiveId, spec.toMapPoints(), spec.toStylesSet())
                    val polyline = shapeLayer.addPolyline(
                        options
                            .setVisible(visible)
                            .setZOrder(zOrder)
                            .setTag(tag),
                    )
                    PolylineNode(
                        polyline = polyline,
                        shapeLayer = shapeLayer,
                        state = mapApplier.mapRoot.state,
                    )
                },
                update = {
                    set(spec) { newSpec ->
                        polyline.changeStylesAndMapPoints(
                            newSpec.toStylesSet(),
                            listOf(newSpec.toMapPoints()),
                        )
                    }
                    set(visible) { if (it) polyline.show() else polyline.hide() }
                    set(zOrder) { polyline.setZOrder(it) }
                    set(tag) { polyline.setTag(it) }
                },
            )
        }
    }
}

private data class PolylineShapeSpec(
    val positions: List<KakaoLatLng>,
    val lineWidth: Float,
    val lineColor: Color,
    val strokeWidth: Float,
    val strokeColor: Color,
) {
    fun toMapPoints(): MapPoints =
        MapPoints.fromLatLng(positions.map { it.toLatLng() })

    fun toStylesSet(): PolylineStylesSet =
        PolylineStylesSet.from(PolylineStyles.from(toStyle()))

    private fun toStyle(): PolylineStyle =
        PolylineStyle.from(lineWidth, lineColor.toArgb(), strokeWidth, strokeColor.toArgb())
}

private fun polylineOptionsWithId(
    id: String,
    mapPoints: MapPoints,
    stylesSet: PolylineStylesSet,
): PolylineOptions = PolylineOptions.from(id).setMapPoints(mapPoints).setStylesSet(stylesSet)

private const val MIN_POLYLINE_POINT_COUNT = 2
