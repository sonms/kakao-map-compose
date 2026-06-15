package com.sonms.kakao.maps.open.map.compose.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sonms.kakao.maps.open.map.compose.camera.KakaoCameraPositionState
import com.sonms.kakao.maps.open.map.compose.cluster.KakaoClusterBadge
import com.sonms.kakao.maps.open.map.compose.cluster.rememberKakaoClusterState
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.model.KakaoClusterItem
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.model.KakaoOverlayImage
import com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoPoi

// 마포구 일대 6×5 격자
val clusterFocusPositions = listOf(
    KakaoLatLng.from(37.5620, 126.9060),
    KakaoLatLng.from(37.5440, 126.9340),
)

private val clusterItems: List<KakaoClusterItem> = buildList {
    val latitudes  = listOf(37.5620, 37.5588, 37.5556, 37.5524, 37.5492, 37.5460)
    val longitudes = listOf(126.9060, 126.9116, 126.9172, 126.9228, 126.9284)

    latitudes.forEachIndexed { row, lat ->
        longitudes.forEachIndexed { col, lng ->
            add(
                KakaoClusterItem(
                    position = KakaoLatLng.from(lat, lng),
                    itemId = "cluster-item-$row-$col",
                ),
            )
        }
    }
}

/**
 * 클러스터 데모.
 *
 * - 마포구 일대에 6×5 격자로 30개 항목을 배치합니다.
 * - 줌 아웃 시 가까운 항목들이 원형 배지로 클러스터링됩니다.
 * - 줌 인 시 개별 POI 마커로 분리됩니다.
 */
@Composable
@KakaoMapComposable
fun ClusterDemoContent(cameraPositionState: KakaoCameraPositionState) {
    val clusterState = rememberKakaoClusterState(
        items = clusterItems,
        cameraPositionState = cameraPositionState,
        clusterRadius = 60.dp,
    )

    clusterState.clusters.forEachIndexed { index, cluster ->
        key(index) {
            if (cluster.isCluster) {
                val badge = remember(cluster.size) { KakaoClusterBadge.create(cluster.size) }
                KakaoPoi(
                    position = cluster.center,
                    icon = KakaoOverlayImage.FromBitmap(badge),
                    iconSize = DpSize(48.dp, 48.dp),
                )
            } else {
                KakaoPoi(position = cluster.items.first().position)
            }
        }
    }
}
