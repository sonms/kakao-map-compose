package com.sonms.kakao.maps.open.map.compose.overlay.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.key
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapApplier
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.RouteLineLayerNode

/**
 * 카카오 지도 위에 커스텀 RouteLineLayer를 생성하는 컨테이너 Composable입니다.
 *
 * [KakaoMapApplier] 기반 서브 컴포지션 안에서만 동작합니다.
 * [content] 블록 안에서 호출된 [KakaoRouteLine]은 이 레이어를 사용합니다.
 * [layerId]가 바뀌면 기존 레이어의 모든 오버레이를 제거하고 새 레이어를 생성합니다.
 * [zOrder]가 바뀌어도 새 레이어를 생성합니다.
 *
 * @param layerId SDK RouteLineLayer에 부여할 식별자입니다.
 * @param zOrder 레이어 표시 우선순위입니다.
 * @param content 이 레이어에 속할 [KakaoRouteLine] 오버레이 Composable 영역입니다.
 */
@Composable
@KakaoMapComposable
fun KakaoRouteLineLayer(
    layerId: String,
    zOrder: Int = 1,
    content: @Composable @KakaoMapComposable () -> Unit,
) {
    val mapApplier = currentComposer.applier as? KakaoMapApplier ?: return
    val routeLineManager = mapApplier.mapRoot.kakaoMap.routeLineManager ?: return

    key(layerId, zOrder) {
        ComposeNode<RouteLineLayerNode, KakaoMapApplier>(
            factory = {
                val layer = checkNotNull(routeLineManager.addLayer(layerId, zOrder)) {
                    "routeLineManager.addLayer() returned null for layerId=$layerId"
                }
                RouteLineLayerNode(
                    routeLineLayer = layer,
                    routeLineManager = routeLineManager,
                )
            },
            update = {},
        ) {
            content()
        }
    }
}
