package com.sonms.kakao.maps.open.map.compose.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * [com.sonms.kakao.maps.open.map.compose.overlay.route.KakaoMultiRouteLine]에 전달하는
 * 단일 구간의 좌표와 스타일을 정의하는 모델입니다.
 *
 * @param positions 이 구간을 구성할 위도/경도 좌표 목록입니다. 2개 미만이면 해당 구간은 무시됩니다.
 * @param lineColor 선 색상입니다.
 * @param lineWidth 선 두께입니다.
 * @param strokeWidth 외곽선 두께입니다.
 * @param strokeColor 외곽선 색상입니다.
 * @param pattern 이 구간에 반복 배치할 패턴입니다. null이면 사용하지 않습니다.
 */
@Immutable
data class KakaoRouteSegment(
    val positions: List<KakaoLatLng>,
    val lineColor: Color = Color(0xFF3478F6),
    val lineWidth: Float = 12f,
    val strokeWidth: Float = 0f,
    val strokeColor: Color = Color.Transparent,
    val pattern: KakaoRoutePattern? = null,
)
