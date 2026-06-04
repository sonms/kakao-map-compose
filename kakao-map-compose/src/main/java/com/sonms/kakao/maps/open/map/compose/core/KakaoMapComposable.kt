package com.sonms.kakao.maps.open.map.compose.core

import androidx.compose.runtime.ComposableTargetMarker

/**
 * 카카오 지도 오버레이 전용 Composable 범위를 표시하는 marker annotation입니다.
 *
 * [KakaoMap]의 content 블록 안에서만 사용해야 하는 Composable을 구분하기 위해 사용합니다.
 */
@Retention(AnnotationRetention.BINARY)
@ComposableTargetMarker(description = "Kakao Map Composable")
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
)
annotation class KakaoMapComposable
