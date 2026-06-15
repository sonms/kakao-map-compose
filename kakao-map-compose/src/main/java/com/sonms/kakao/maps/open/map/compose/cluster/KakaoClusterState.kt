package com.sonms.kakao.maps.open.map.compose.cluster

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sonms.kakao.maps.open.map.compose.camera.KakaoCameraPositionState
import com.sonms.kakao.maps.open.map.compose.model.KakaoCluster
import com.sonms.kakao.maps.open.map.compose.model.KakaoClusterItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

/**
 * [rememberKakaoClusterState]가 계산한 클러스터 목록을 보유하는 상태 홀더입니다.
 */
@Stable
class KakaoClusterState internal constructor(
    val clusterRadius: Dp,
) {
    /**
     * 현재 줌 레벨에서 계산된 클러스터 목록입니다.
     *
     * 줌 레벨이 변경될 때마다 자동으로 갱신됩니다.
     */
    var clusters: List<KakaoCluster> by mutableStateOf(emptyList())
        internal set
}

/**
 * [items]를 [cameraPositionState]의 줌 레벨에 따라 자동으로 클러스터링하는 상태 홀더를 생성합니다.
 *
 * - 줌 레벨이 변경될 때마다 격자 기반 클러스터링을 [Dispatchers.Default]에서 비동기 재계산합니다.
 * - [items]가 변경되면 현재 줌 레벨로 즉시 재계산합니다.
 * - `collectLatest`를 사용하므로 빠른 줌 변화 시 이전 계산은 취소됩니다.
 *
 * 사용 예:
 * ```kotlin
 * val clusterState = rememberKakaoClusterState(
 *     items = poiList,
 *     cameraPositionState = cameraPositionState,
 * )
 *
 * KakaoMap(cameraPositionState = cameraPositionState) {
 *     clusterState.clusters.forEachIndexed { index, cluster ->
 *         key(index) {
 *             if (cluster.isCluster) {
 *                 val badge = remember(cluster.size) { KakaoClusterBadge.create(cluster.size) }
 *                 KakaoPoi(
 *                     position = cluster.center,
 *                     icon = KakaoOverlayImage.FromBitmap(badge),
 *                     iconSize = DpSize(48.dp, 48.dp),
 *                 )
 *             } else {
 *                 KakaoPoi(position = cluster.items.first().position)
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @param items 클러스터링할 항목 목록입니다.
 * @param cameraPositionState 줌 레벨 변화를 감지하는 카메라 상태입니다.
 * @param clusterRadius 같은 클러스터로 묶는 화면 상의 반경입니다. 클수록 더 많이 묶입니다.
 */
@Composable
fun rememberKakaoClusterState(
    items: List<KakaoClusterItem>,
    cameraPositionState: KakaoCameraPositionState,
    clusterRadius: Dp = 60.dp,
): KakaoClusterState {
    val density = LocalDensity.current
    val state = remember { KakaoClusterState(clusterRadius) }
    val currentItems by rememberUpdatedState(items)
    val currentRadius by rememberUpdatedState(clusterRadius)

    LaunchedEffect(Unit) {
        snapshotFlow {
            Triple(currentItems, cameraPositionState.position.zoomLevel, currentRadius)
        }
            .distinctUntilChanged()
            .collectLatest { (latestItems, zoomLevel, radius) ->
                val radiusPx = with(density) { radius.toPx() }
                val newClusters = withContext(Dispatchers.Default) {
                    gridCluster(latestItems, zoomLevel, radiusPx)
                }
                state.clusters = newClusters
            }
    }

    return state
}
