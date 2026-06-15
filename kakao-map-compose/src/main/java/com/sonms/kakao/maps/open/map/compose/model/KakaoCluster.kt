package com.sonms.kakao.maps.open.map.compose.model

import androidx.compose.runtime.Stable

/**
 * 격자 클러스터링 결과로 생성되는 클러스터 또는 단일 항목을 나타냅니다.
 *
 * - [isCluster]가 true이면 [center]에 배지 마커를 표시합니다.
 * - [isCluster]가 false이면 [items]의 첫 번째 항목 아이콘을 그대로 표시합니다.
 *
 * @param center 클러스터에 포함된 모든 항목의 위치 중심(무게중심)입니다.
 * @param items 이 클러스터에 속하는 원본 항목 목록입니다.
 */
@Stable
class KakaoCluster(
    val center: KakaoLatLng,
    val items: List<KakaoClusterItem>,
) {
    val size: Int = items.size
    val isCluster: Boolean = items.size > 1
}
