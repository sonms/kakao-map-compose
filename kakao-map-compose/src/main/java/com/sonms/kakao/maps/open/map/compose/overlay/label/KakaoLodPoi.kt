package com.sonms.kakao.maps.open.map.compose.overlay.label

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.isSpecified
import com.kakao.vectormap.label.LabelLayerOptions
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.label.LodLabel
import com.kakao.vectormap.label.LodLabelLayer
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapApplier
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.LodPoiNode
import com.sonms.kakao.maps.open.map.compose.model.KakaoLodPoiItem

/**
 * 카카오 지도 위에 대량의 LOD POI를 표시하는 Composable입니다.
 *
 * 개별 [KakaoPoi] 대신 SDK의 [LodLabelLayer]를 사용하여 수백~수천 개 마커를 효율적으로 표시합니다.
 * zoom level에 따라 표시 밀도가 자동으로 조정됩니다.
 *
 * [com.sonms.kakao.maps.open.map.compose.core.KakaoMap]의 content 안에서 선언하면,
 * [items] 변경에 따라 마커가 자동으로 갱신됩니다.
 *
 * @param items 표시할 POI 항목 목록입니다.
 * @param visible 레이어 전체의 표시 여부입니다.
 * @param onClick POI 클릭 시 호출되는 콜백입니다. true를 반환하면 이벤트가 소비됩니다.
 * @param clickable 레이어 전체의 클릭 가능 여부입니다. 기본값은 [onClick] 존재 여부를 따릅니다.
 */
@Composable
@KakaoMapComposable
@Suppress("ComposableTargetMismatch")
fun KakaoLodPoi(
    items: List<KakaoLodPoiItem>,
    visible: Boolean = true,
    onClick: ((LodLabel) -> Boolean)? = null,
    clickable: Boolean = onClick != null,
) {
    val mapApplier = currentComposer.applier as? KakaoMapApplier ?: return
    val labelManager = mapApplier.mapRoot.kakaoMap.labelManager ?: return
    val context = LocalContext.current
    val density = LocalDensity.current
    val state = mapApplier.mapRoot.state

    ComposeNode<LodPoiNode, KakaoMapApplier>(
        factory = {
            val layer = checkNotNull(
                labelManager.addLodLayer(
                    LabelLayerOptions.from()
                        .setVisible(visible)
                        .setClickable(clickable),
                ),
            ) { "labelManager.addLodLayer() returned null" }
            if (items.isNotEmpty()) {
                layer.addLodLabels(items.toLabelOptions(context, density))
            }
            LodPoiNode(
                lodLabelLayer = layer,
                labelManager = labelManager,
                onClick = onClick,
                state = state,
            )
        },
        update = {
            set(items) { newItems ->
                lodLabelLayer.removeAll()
                if (newItems.isNotEmpty()) {
                    lodLabelLayer.addLodLabels(newItems.toLabelOptions(context, density))
                }
            }
            set(visible) { lodLabelLayer.setVisible(it) }
            set(clickable) { lodLabelLayer.setClickable(it) }
            set(onClick) { newOnClick ->
                this.onClick = newOnClick
                if (newOnClick != null) {
                    state.registerLodLabelClickHandler(lodLabelLayer) { newOnClick(it) }
                } else {
                    state.unregisterLodLabelClickHandler(lodLabelLayer)
                }
            }
        },
    )
}

private fun List<KakaoLodPoiItem>.toLabelOptions(context: Context, density: Density): List<LabelOptions> =
    map { item ->
        val widthPx = if (item.iconSize.isSpecified) with(density) { item.iconSize.width.roundToPx() } else 0
        val heightPx = if (item.iconSize.isSpecified) with(density) { item.iconSize.height.roundToPx() } else 0
        val sdkTextStyle = if (item.text != null) item.textStyle.toSdkTextStyle() else null
        val base = if (item.labelId == null) {
            LabelOptions.from(item.position.toLatLng())
        } else {
            LabelOptions.from(item.labelId, item.position.toLatLng())
        }
        base
            .setStyles(labelStyleFrom(context, item.icon, widthPx, heightPx, sdkTextStyle))
            .setRank(item.rank)
            .setTag(item.tag)
            .also { options ->
                if (item.text != null) {
                    options.setTexts(LabelTextBuilder().setTexts(item.text))
                }
            }
    }
