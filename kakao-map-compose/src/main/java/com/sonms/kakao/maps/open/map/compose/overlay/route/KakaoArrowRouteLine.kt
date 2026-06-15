package com.sonms.kakao.maps.open.map.compose.overlay.route

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapApplier
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.PoiNode
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * 종점에 화살표 머리가 붙는 경로선을 표시하는 Composable입니다.
 *
 * 내부적으로 [KakaoRouteLine]과 회전된 화살표 라벨을 조합합니다.
 * 화살표 머리는 마지막 두 좌표의 방위각으로 자동 회전되며, 중앙 앵커로 배치되어
 * 기저부가 종점을 덮고 팁이 진행 방향으로 돌출됩니다.
 *
 * @param positions 경로선을 구성할 위도/경도 좌표 목록입니다.
 * @param lineColor 경로선 및 화살표 머리의 색상입니다.
 * @param lineWidth 경로선 두께입니다.
 * @param strokeWidth 경로선 외곽선 두께입니다.
 * @param strokeColor 경로선 외곽선 색상입니다.
 * @param headSizeRatio 화살표 머리 너비 비율입니다. [lineWidth] × [headSizeRatio] dp 크기로 생성됩니다.
 *   네이버 지도 ArrowheadPathOverlay 기본값(2.5f)보다 작게 설정되어 있습니다.
 *   비트맵 오버레이 방식은 네이티브 렌더링보다 시각적으로 더 크게 보이기 때문입니다.
 * @param lineId SDK RouteLine에 부여할 식별자입니다. 변경 시 기존 RouteLine을 제거하고 재생성합니다.
 * @param visible 경로선 및 화살표 표시 여부입니다.
 * @param zOrder 경로선 표시 순서입니다. 값이 클수록 위에 표시됩니다.
 * @param tag 경로선에 연결할 문자열 태그입니다.
 */
@Composable
@KakaoMapComposable
@Suppress("ComposableTargetMismatch")
fun KakaoArrowRouteLine(
    positions: List<KakaoLatLng>,
    lineColor: Color = Color(0xFF3478F6),
    lineWidth: Float = 12f,
    strokeWidth: Float = 0f,
    strokeColor: Color = Color.Transparent,
    headSizeRatio: Float = 2.0f,
    lineId: String? = null,
    visible: Boolean = true,
    zOrder: Int = 0,
    tag: String? = null,
) {
    val density = LocalDensity.current
    val arrowBitmap = remember(lineColor, lineWidth, headSizeRatio, density) {
        val lineWidthPx = with(density) { lineWidth.dp.roundToPx() }.coerceAtLeast(1)
        val headWidthPx = (lineWidthPx * headSizeRatio).toInt().coerceAtLeast(1)
        val headHeightPx = (headWidthPx * 1.4f).toInt().coerceAtLeast(1)
        createArrowBitmap(lineColor.toArgb(), headWidthPx, headHeightPx)
    }

    val bearing = remember(positions) {
        if (positions.size >= 2) {
            bearingDegrees(positions[positions.size - 2], positions.last())
        } else 0f
    }

    val rotatedBitmap = remember(arrowBitmap, bearing) {
        rotateArrowBitmap(arrowBitmap, bearing)
    }

    KakaoRouteLine(
        positions = positions,
        lineColor = lineColor,
        lineWidth = lineWidth,
        strokeWidth = strokeWidth,
        strokeColor = strokeColor,
        lineId = lineId,
        visible = visible,
        zOrder = zOrder,
        tag = tag,
    )

    if (positions.size >= 2) {
        ArrowHeadLabel(
            position = positions.last(),
            bitmap = rotatedBitmap,
            visible = visible,
        )
    }
}

@Composable
@KakaoMapComposable
@Suppress("ComposableTargetMismatch")
private fun ArrowHeadLabel(
    position: KakaoLatLng,
    bitmap: Bitmap,
    visible: Boolean,
) {
    val mapApplier = currentComposer.applier as? KakaoMapApplier ?: return
    val root = mapApplier.mapRoot
    val labelLayer = root.kakaoMap.labelManager?.layer ?: return

    ComposeNode<PoiNode, KakaoMapApplier>(
        factory = {
            val label = labelLayer.addLabel(
                LabelOptions.from(position.toLatLng())
                    .setStyles(LabelStyle.from(bitmap).setAnchorPoint(0.5f, 0.5f))
                    .setClickable(false)
                    .setVisible(visible),
            )
            PoiNode(label = label, onClick = { false }, state = root.state)
        },
        update = {
            set(position) { label.moveTo(it.toLatLng()) }
            set(bitmap) { label.setStyles(LabelStyle.from(it).setAnchorPoint(0.5f, 0.5f)) }
            set(visible) { if (it) label.show() else label.hide() }
        },
    )
}

private fun bearingDegrees(from: KakaoLatLng, to: KakaoLatLng): Float {
    val lat1 = Math.toRadians(from.latitude)
    val lat2 = Math.toRadians(to.latitude)
    val dLon = Math.toRadians(to.longitude - from.longitude)
    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
    return ((Math.toDegrees(atan2(y, x)).toFloat() + 360f) % 360f)
}
