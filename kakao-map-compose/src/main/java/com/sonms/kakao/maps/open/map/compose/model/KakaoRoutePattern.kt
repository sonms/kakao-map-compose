package com.sonms.kakao.maps.open.map.compose.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.DpSize

/**
 * 경로선 위에 반복 배치되는 패턴과 심볼을 정의하는 모델입니다.
 *
 * [com.sonms.kakao.maps.open.map.compose.overlay.route.KakaoRouteLine]의 `pattern` 파라미터에 사용됩니다.
 *
 * - [pattern]: 선을 따라 일정 간격으로 반복되는 이미지입니다 (점, 대시 등).
 * - [symbol]: 방향성 심볼 이미지입니다 (화살표 머리 등). null이면 사용하지 않습니다.
 * - [distance]: 패턴 반복 간격입니다.
 * - [patternSize]: 패턴 이미지의 렌더링 크기입니다. [DpSize.Unspecified]이면 intrinsic 크기를 사용합니다.
 *   벡터 drawable은 이 값을 지정해야 선명하게 렌더링됩니다.
 * - [symbolSize]: 심볼 이미지의 렌더링 크기입니다. [DpSize.Unspecified]이면 intrinsic 크기를 사용합니다.
 * - [pinStart]: true이면 선의 시작점에 패턴을 고정합니다.
 * - [pinEnd]: true이면 선의 끝점에 패턴을 고정합니다.
 */
@Immutable
data class KakaoRoutePattern(
    val pattern: KakaoOverlayImage,
    val symbol: KakaoOverlayImage? = null,
    val distance: Float,
    val patternSize: DpSize = DpSize.Unspecified,
    val symbolSize: DpSize = DpSize.Unspecified,
    val pinStart: Boolean = false,
    val pinEnd: Boolean = false,
)
