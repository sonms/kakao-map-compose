package com.sonms.kakao.maps.open.map.compose.overlay.label

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.sonms.kakao.maps.open.map.compose.R
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapApplier
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.LabelLayerNode
import com.sonms.kakao.maps.open.map.compose.core.PoiNode
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng

/**
 * 카카오 지도 위에 POI(Label)를 표시하는 Composable입니다.
 *
 * [KakaoMapApplier] 기반 서브 컴포지션 안에서만 동작합니다. 지도 SDK의 [Label]을
 * [ComposeNode]로 관리하여 파라미터 변경 시 [PoiNode]의 update 블록이 SDK를 직접 갱신합니다.
 *
 * @param position POI를 표시할 위도/경도 좌표입니다.
 * @param iconResId POI 아이콘으로 사용할 drawable 리소스 ID입니다.
 * @param labelId SDK Label에 부여할 식별자입니다. 변경 시 기존 Label을 제거하고 재생성합니다.
 * @param onClick POI 클릭 시 호출되는 콜백입니다. true를 반환하면 이벤트가 소비됩니다.
 * @param clickable POI 클릭 가능 여부입니다. 기본값은 [onClick] 존재 여부를 따릅니다.
 * @param visible POI 표시 여부입니다.
 * @param rank POI의 표시 우선순위입니다.
 * @param tag POI에 연결할 문자열 태그입니다.
 */
@Composable
@KakaoMapComposable
fun KakaoPoi(
    position: KakaoLatLng,
    @DrawableRes iconResId: Int = R.drawable.kakao_map_default_marker,
    labelId: String? = null,
    onClick: ((Label) -> Boolean)? = null,
    clickable: Boolean = onClick != null,
    visible: Boolean = true,
    rank: Long = 0L,
    tag: String? = null,
) {
    val mapApplier = currentComposer.applier as? KakaoMapApplier ?: return
    val root = mapApplier.mapRoot
    val labelLayer = (mapApplier.current as? LabelLayerNode)?.labelLayer
        ?: root.kakaoMap.labelManager?.layer
        ?: return
    val context = LocalContext.current

    key(labelId) {
        ComposeNode<PoiNode, KakaoMapApplier>(
            factory = {
                val options = if (labelId == null) {
                    LabelOptions.from(position.toLatLng())
                } else {
                    LabelOptions.from(labelId, position.toLatLng())
                }
                val label = labelLayer.addLabel(
                    options
                        .setStyles(labelStyleFromDrawable(context, iconResId))
                        .setClickable(clickable)
                        .setVisible(visible)
                        .setRank(rank)
                        .setTag(tag),
                )
                PoiNode(
                    label = label,
                    onClick = onClick ?: { false },
                    state = root.state,
                )
            },
            update = {
                set(position) { label.moveTo(it.toLatLng()) }
                set(iconResId) { label.setStyles(labelStyleFromDrawable(context, it)) }
                set(clickable) { label.setClickable(it) }
                set(visible) { if (it) label.show() else label.hide() }
                set(rank) { label.changeRank(it) }
                set(tag) { label.tag = it }
                set(onClick) { this.onClick = it ?: { false } }
            },
        )
    }
}

private fun labelStyleFromDrawable(
    context: Context,
    @DrawableRes iconResId: Int,
): LabelStyle {
    val drawable = checkNotNull(
        ResourcesCompat.getDrawable(context.resources, iconResId, context.theme),
    ) {
        "Drawable resource $iconResId could not be loaded."
    }
    return LabelStyle.from(drawable.toBitmap())
        .setAnchorPoint(0.5f, 1.0f)
}
