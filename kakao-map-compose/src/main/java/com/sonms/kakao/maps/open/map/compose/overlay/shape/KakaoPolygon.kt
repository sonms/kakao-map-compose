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
import com.kakao.vectormap.shape.PolygonOptions
import com.kakao.vectormap.shape.PolygonStyle
import com.kakao.vectormap.shape.PolygonStyles
import com.kakao.vectormap.shape.PolygonStylesSet
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapApplier
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.PolygonNode
import com.sonms.kakao.maps.open.map.compose.core.ShapeLayerNode
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng

/**
 * 카카오 지도 위에 폴리곤(채워진 다각형)을 표시하는 Composable입니다.
 *
 * 좌표가 3개 미만이면 SDK Polygon을 생성하지 않으며, 이후 3개 이상이 되면 자동으로 재생성합니다.
 * [com.sonms.kakao.maps.open.map.compose.core.KakaoMap]의 content 안에서 선언하면,
 * 파라미터 변경에 따라 지도 위 다각형이 자동으로 생성, 갱신, 제거됩니다.
 *
 * @param positions 다각형을 구성할 위도/경도 꼭짓점 목록입니다. 마지막 점과 첫 번째 점은 자동으로 연결됩니다.
 * @param fillColor 다각형 채우기 색상입니다.
 * @param strokeWidth 다각형 외곽선 두께입니다.
 * @param strokeColor 다각형 외곽선 색상입니다.
 * @param polygonId SDK Polygon에 부여할 식별자입니다. 변경 시 기존 Polygon을 제거하고 재생성합니다.
 * @param visible 다각형 표시 여부입니다.
 * @param zOrder 다각형 표시 순서입니다. 값이 클수록 위에 표시됩니다.
 * @param tag 다각형에 연결할 태그입니다.
 */
@Composable
@KakaoMapComposable
@Suppress("ComposableTargetMismatch")
fun KakaoPolygon(
    positions: List<KakaoLatLng>,
    fillColor: Color = Color(0x553478F6),
    strokeWidth: Float = 2f,
    strokeColor: Color = Color(0xFF3478F6),
    polygonId: String? = null,
    visible: Boolean = true,
    zOrder: Int = 0,
    tag: String? = null,
) {
    val mapApplier = currentComposer.applier as? KakaoMapApplier ?: return
    val shapeLayer = (mapApplier.current as? ShapeLayerNode)?.shapeLayer
        ?: mapApplier.mapRoot.kakaoMap.shapeManager?.layer
        ?: return
    val canDraw = positions.size >= MIN_POLYGON_POINT_COUNT
    val effectiveId = remember(polygonId) { polygonId ?: UUID.randomUUID().toString() }
    val spec = PolygonShapeSpec(positions, fillColor, strokeWidth, strokeColor)

    key(polygonId) {
        if (canDraw) {
            ComposeNode<PolygonNode, KakaoMapApplier>(
                factory = {
                    val options = polygonOptionsWithId(effectiveId, spec.toMapPoints(), spec.toStylesSet())
                    val polygon = shapeLayer.addPolygon(
                        options
                            .setVisible(visible)
                            .setZOrder(zOrder)
                            .setTag(tag),
                    )
                    PolygonNode(
                        polygon = polygon,
                        shapeLayer = shapeLayer,
                        state = mapApplier.mapRoot.state,
                    )
                },
                update = {
                    set(spec) { newSpec ->
                        polygon.changeStylesAndMapPoints(
                            newSpec.toStylesSet(),
                            listOf(newSpec.toMapPoints()),
                        )
                    }
                    set(visible) { if (it) polygon.show() else polygon.hide() }
                    set(zOrder) { polygon.setZOrder(it) }
                    set(tag) { polygon.setTag(it) }
                },
            )
        }
    }
}

private data class PolygonShapeSpec(
    val positions: List<KakaoLatLng>,
    val fillColor: Color,
    val strokeWidth: Float,
    val strokeColor: Color,
) {
    fun toMapPoints(): MapPoints {
        // SDK가 자동으로 폴리곤을 닫지 않으므로 마지막 점이 첫 번째 점과 다를 경우 직접 닫아줍니다.
        val pts = if (positions.first() != positions.last()) positions + positions.first() else positions
        return MapPoints.fromLatLng(pts.map { it.toLatLng() })
    }

    fun toStylesSet(): PolygonStylesSet =
        PolygonStylesSet.from(PolygonStyles.from(toStyle()))

    private fun toStyle(): PolygonStyle =
        PolygonStyle.from(fillColor.toArgb(), strokeWidth, strokeColor.toArgb())
}

private fun polygonOptionsWithId(
    id: String,
    mapPoints: MapPoints,
    stylesSet: PolygonStylesSet,
): PolygonOptions = PolygonOptions.from(id).setMapPoints(mapPoints).setStylesSet(stylesSet)

private const val MIN_POLYGON_POINT_COUNT = 3
