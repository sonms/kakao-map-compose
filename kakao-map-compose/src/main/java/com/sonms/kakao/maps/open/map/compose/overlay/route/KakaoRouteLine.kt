package com.sonms.kakao.maps.open.map.compose.overlay.route

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.isSpecified
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import java.util.UUID
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLinePattern
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapApplier
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.RouteLineLayerNode
import com.sonms.kakao.maps.open.map.compose.core.RouteLineNode
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.model.KakaoOverlayImage
import com.sonms.kakao.maps.open.map.compose.model.KakaoRoutePattern

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
 * @param pattern 경로선 위에 반복 배치할 패턴 또는 심볼입니다. null이면 사용하지 않습니다.
 *   점선은 [pattern]에 점 이미지를, 화살표는 [KakaoRoutePattern.symbol]에 화살표 이미지를 지정합니다.
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
    pattern: KakaoRoutePattern? = null,
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
    val effectiveLineId = remember(lineId) { lineId ?: UUID.randomUUID().toString() }
    val context = LocalContext.current
    val density = LocalDensity.current
    val sdkPattern = remember(pattern, density) { pattern?.toSdkPattern(context, density) }
    val segmentSpec = RouteLineSegmentSpec(positions, lineWidth, lineColor, strokeWidth, strokeColor, sdkPattern)

    key(lineId) {
        if (canDraw) {
            ComposeNode<RouteLineNode, KakaoMapApplier>(
                factory = {
                    val options = RouteLineOptions.from(effectiveLineId, segmentSpec.toSegment())
                    val routeLine = routeLineLayer.addRouteLine(
                        options
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
                    set(segmentSpec) { routeLine.changeSegments(it.toSegment()) }
                    set(visible) { if (it) routeLine.show() else routeLine.hide() }
                    set(zOrder) { routeLine.zOrder = it }
                    set(tag) { routeLine.tag = it }
                },
            )
        }
    }
}

internal data class RouteLineSegmentSpec(
    val positions: List<KakaoLatLng>,
    val lineWidth: Float,
    val lineColor: Color,
    val strokeWidth: Float,
    val strokeColor: Color,
    val sdkPattern: RouteLinePattern?,
) {
    fun toSegment(): RouteLineSegment {
        val style = if (sdkPattern != null) {
            RouteLineStyle.from(lineWidth, lineColor.toArgb(), strokeWidth, strokeColor.toArgb(), sdkPattern)
        } else {
            RouteLineStyle.from(lineWidth, lineColor.toArgb(), strokeWidth, strokeColor.toArgb())
        }
        return RouteLineSegment.from(positions.map { it.toLatLng() }, style)
    }
}

internal fun KakaoRoutePattern.toSdkPattern(context: Context, density: Density): RouteLinePattern {
    val pw = if (patternSize.isSpecified) with(density) { patternSize.width.roundToPx() } else 0
    val ph = if (patternSize.isSpecified) with(density) { patternSize.height.roundToPx() } else 0
    val sw = if (symbolSize.isSpecified) with(density) { symbolSize.width.roundToPx() } else 0
    val sh = if (symbolSize.isSpecified) with(density) { symbolSize.height.roundToPx() } else 0
    val patternBitmap = pattern.toBitmapFrom(context, pw, ph)
    val sdkPattern = if (symbol != null) {
        RouteLinePattern.from(patternBitmap, symbol.toBitmapFrom(context, sw, sh), distance)
    } else {
        RouteLinePattern.from(patternBitmap, distance)
    }
    return sdkPattern
        .setPinStart(pinStart)
        .setPinEnd(pinEnd)
}

private fun KakaoOverlayImage.toBitmapFrom(context: Context, widthPx: Int, heightPx: Int): Bitmap =
    when (this) {
        is KakaoOverlayImage.FromResource -> {
            val drawable = checkNotNull(
                ResourcesCompat.getDrawable(context.resources, resId, context.theme),
            ) { "Drawable resource $resId could not be loaded." }
            if (widthPx > 0 && heightPx > 0) drawable.toBitmap(width = widthPx, height = heightPx)
            else drawable.toBitmap()
        }
        is KakaoOverlayImage.FromBitmap -> {
            if (widthPx > 0 && heightPx > 0) Bitmap.createScaledBitmap(bitmap, widthPx, heightPx, true)
            else bitmap
        }
    }

private const val MIN_ROUTE_LINE_POINT_COUNT = 2
