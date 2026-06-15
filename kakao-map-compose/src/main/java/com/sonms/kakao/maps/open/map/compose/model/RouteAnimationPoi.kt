package com.sonms.kakao.maps.open.map.compose.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kakao.vectormap.label.Label

/**
 * [com.sonms.kakao.maps.open.map.compose.overlay.route.KakaoAnimatedRouteLine]에서
 * 경로선이 지나칠 때 등장 애니메이션을 실행할 POI 항목입니다.
 *
 * @param position POI 위치입니다.
 * @param icon POI 아이콘입니다.
 * @param fullSize 최종 아이콘 크기입니다. 애니메이션은 0 → 이 크기 순서로 진행됩니다.
 * @param labelId SDK Label 식별자입니다. null이면 경로선 내부에서 자동으로 관리합니다.
 * @param appearDurationMs POI 등장 애니메이션 시간(ms)입니다.
 * @param onClick POI 클릭 콜백입니다. true를 반환하면 이벤트가 소비됩니다.
 * @param routeFraction 경로 전체에서 이 POI가 등장할 위치(0.0~1.0)입니다.
 *   null이면 [position]을 경로에 수직 투영하여 자동 계산합니다.
 */
@Stable
data class RouteAnimationPoi(
    val position: KakaoLatLng,
    val icon: KakaoOverlayImage = KakaoOverlayImage.Default,
    val fullSize: Dp = 40.dp,
    val labelId: String? = null,
    val appearDurationMs: Int = 300,
    val onClick: ((Label) -> Boolean)? = null,
    val routeFraction: Float? = null,
)
