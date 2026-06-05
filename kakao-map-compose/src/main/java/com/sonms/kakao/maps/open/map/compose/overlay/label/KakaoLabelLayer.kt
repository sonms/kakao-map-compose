package com.sonms.kakao.maps.open.map.compose.overlay.label

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.key
import com.kakao.vectormap.label.LabelLayerOptions
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapApplier
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.LabelLayerNode

/**
 * 카카오 지도 위에 커스텀 LabelLayer를 생성하는 컨테이너 Composable입니다.
 *
 * [KakaoMapApplier] 기반 서브 컴포지션 안에서만 동작합니다.
 * [content] 블록 안에서 호출된 [KakaoPoi]는 이 레이어를 사용합니다.
 * [layerId]가 바뀌면 기존 레이어의 모든 오버레이를 제거하고 새 레이어를 생성합니다.
 *
 * @param layerId SDK LabelLayer에 부여할 식별자입니다.
 * @param zOrder 레이어 표시 우선순위입니다.
 * @param content 이 레이어에 속할 [KakaoPoi] 오버레이 Composable 영역입니다.
 */
@Composable
@KakaoMapComposable
fun KakaoLabelLayer(
    layerId: String,
    zOrder: Int = 1,
    content: @Composable @KakaoMapComposable () -> Unit,
) {
    val mapApplier = currentComposer.applier as? KakaoMapApplier ?: return
    val labelManager = mapApplier.mapRoot.kakaoMap.labelManager ?: return

    key(layerId) {
        ComposeNode<LabelLayerNode, KakaoMapApplier>(
            factory = {
                val layer = checkNotNull(
                    labelManager.addLayer(
                        LabelLayerOptions.from(layerId).setZOrder(zOrder),
                    ),
                ) { "labelManager.addLayer() returned null for layerId=$layerId" }
                LabelLayerNode(
                    labelLayer = layer,
                    labelManager = labelManager,
                )
            },
            update = {
                set(zOrder) { labelLayer.zOrder = it }
            },
        ) {
            content()
        }
    }
}
