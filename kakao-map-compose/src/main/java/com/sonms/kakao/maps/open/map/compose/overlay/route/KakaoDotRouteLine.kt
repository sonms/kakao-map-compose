package com.sonms.kakao.maps.open.map.compose.overlay.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.model.KakaoOverlayImage
import com.sonms.kakao.maps.open.map.compose.model.KakaoRoutePattern

/**
 * 카카오 지도 위에 점선 경로를 표시하는 Composable입니다.
 *
 * 내부적으로 [KakaoRouteLine]에 원형 패턴을 적용합니다.
 * 점선 외에 다른 조합(점선 + 화살표 등)이 필요하면 [KakaoRouteLine]에 [KakaoRoutePattern]을 직접 조합하세요.
 *
 * @param positions 경로선을 구성할 위도/경도 좌표 목록입니다.
 * @param dotColor 점의 색상입니다.
 * @param dotDiameter 점 하나의 지름입니다. 작은 값일수록 촘촘한 점선에 가까워집니다.
 * @param dotSpacing 인접한 두 점의 중심 사이 간격입니다. [dotDiameter]보다 크면 점이 분리되어 보입니다.
 * @param lineId SDK RouteLine에 부여할 식별자입니다. 변경 시 기존 RouteLine을 제거하고 재생성합니다.
 * @param visible 경로선 표시 여부입니다.
 * @param zOrder 경로선 표시 순서입니다. 값이 클수록 위에 표시됩니다.
 * @param tag 경로선에 연결할 문자열 태그입니다.
 */
@Composable
@KakaoMapComposable
@Suppress("ComposableTargetMismatch")
fun KakaoDotRouteLine(
    positions: List<KakaoLatLng>,
    dotColor: Color = Color(0xFF3478F6),
    dotDiameter: Dp = 6.dp,
    dotSpacing: Float = 8f,
    lineId: String? = null,
    visible: Boolean = true,
    zOrder: Int = 0,
    tag: String? = null,
) {
    val density = LocalDensity.current
    val dotBitmap = remember(dotColor, dotDiameter, density) {
        val sizePx = with(density) { dotDiameter.roundToPx() }.coerceAtLeast(1)
        createDotBitmap(dotColor.toArgb(), sizePx)
    }

    KakaoRouteLine(
        positions = positions,
        lineWidth = 0f,
        lineColor = Color.Transparent,
        pattern = KakaoRoutePattern(
            pattern = KakaoOverlayImage.bitmap(dotBitmap),
            distance = dotSpacing,
        ),
        lineId = lineId,
        visible = visible,
        zOrder = zOrder,
        tag = tag,
    )
}

