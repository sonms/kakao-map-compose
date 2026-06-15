package com.sonms.kakao.maps.open.map.compose.model

import androidx.compose.runtime.Immutable

/**
 * [com.sonms.kakao.maps.open.map.compose.cluster.rememberKakaoClusterState]에 전달할
 * 클러스터링 대상 항목입니다.
 *
 * @param position 항목의 위도/경도 위치입니다.
 * @param icon 단일 마커로 표시될 때 사용할 아이콘입니다.
 * @param itemId 항목의 식별자입니다. [KakaoLatLng]만으로 구분이 어려울 때 사용합니다.
 */
@Immutable
data class KakaoClusterItem(
    val position: KakaoLatLng,
    val icon: KakaoOverlayImage = KakaoOverlayImage.Default,
    val itemId: String? = null,
)
