package com.sonms.kakao.maps.open.map.compose.overlay.label

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.isSpecified
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.label.LabelTextStyle
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapApplier
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.LabelLayerNode
import com.sonms.kakao.maps.open.map.compose.core.PoiNode
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.model.KakaoOverlayImage
import com.sonms.kakao.maps.open.map.compose.model.KakaoPoiTextStyle

/**
 * 카카오 지도 위에 POI(Label)를 표시하는 Composable입니다.
 *
 * [com.sonms.kakao.maps.open.map.compose.core.KakaoMap]의 content 안에서 선언하면,
 * 파라미터 변경에 따라 지도 위 Label이 자동으로 생성, 갱신, 제거됩니다.
 *
 * @param position POI를 표시할 위도/경도 좌표입니다.
 * @param icon POI 아이콘입니다. [KakaoOverlayImage.resource] 또는 [KakaoOverlayImage.bitmap]으로 생성합니다.
 * @param iconSize 아이콘 렌더링 크기입니다. [DpSize.Unspecified]이면 drawable의 intrinsic 크기를 사용합니다.
 *   벡터 drawable은 이 값을 지정해야 선명하게 렌더링됩니다.
 * @param text POI 위에 표시할 텍스트입니다. null이면 텍스트를 표시하지 않습니다.
 * @param textStyle 텍스트 크기, 색상, 외곽선 등 텍스트 스타일입니다. [text]가 null이면 무시됩니다.
 * @param labelId SDK Label에 부여할 식별자입니다. 변경 시 기존 Label을 제거하고 재생성합니다.
 * @param onClick POI 클릭 시 호출되는 콜백입니다. true를 반환하면 이벤트가 소비됩니다.
 * @param clickable POI 클릭 가능 여부입니다. 기본값은 [onClick] 존재 여부를 따릅니다.
 * @param visible POI 표시 여부입니다.
 * @param rank POI의 표시 우선순위입니다.
 * @param tag POI에 연결할 문자열 태그입니다.
 */
@Composable
@KakaoMapComposable
@Suppress("ComposableTargetMismatch")
fun KakaoPoi(
    position: KakaoLatLng,
    icon: KakaoOverlayImage = KakaoOverlayImage.Default,
    iconSize: DpSize = DpSize.Unspecified,
    text: String? = null,
    textStyle: KakaoPoiTextStyle = KakaoPoiTextStyle.Default,
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
    val density = LocalDensity.current
    val iconWidthPx = if (iconSize.isSpecified) with(density) { iconSize.width.roundToPx() } else 0
    val iconHeightPx = if (iconSize.isSpecified) with(density) { iconSize.height.roundToPx() } else 0

    // icon, iconSize(px), density, text, textStyle을 하나의 키로 묶어
    // 어느 하나라도 변경되면 setStyles() + changeText()가 단 한 번만 호출되도록 합니다.
    val styleKey = LabelStyleKey(icon, iconWidthPx, iconHeightPx, text, textStyle)

    key(labelId) {
        ComposeNode<PoiNode, KakaoMapApplier>(
            factory = {
                val sdkTextStyle = if (text != null) textStyle.toSdkTextStyle() else null
                val labelStyle = labelStyleFrom(context, icon, iconWidthPx, iconHeightPx, sdkTextStyle)
                val options = if (labelId == null) {
                    LabelOptions.from(position.toLatLng())
                } else {
                    LabelOptions.from(labelId, position.toLatLng())
                }
                options
                    .setStyles(labelStyle)
                    .setClickable(clickable)
                    .setVisible(visible)
                    .setRank(rank)
                    .setTag(tag)
                if (text != null) {
                    options.setTexts(LabelTextBuilder().setTexts(text))
                }
                val label = labelLayer.addLabel(options)
                PoiNode(
                    label = label,
                    onClick = onClick ?: { false },
                    state = root.state,
                )
            },
            update = {
                set(position) { label.moveTo(it.toLatLng()) }
                set(styleKey) { key ->
                    val sdkTextStyle = if (key.text != null) key.textStyle.toSdkTextStyle() else null
                    label.setStyles(labelStyleFrom(context, key.icon, key.iconWidthPx, key.iconHeightPx, sdkTextStyle))
                    label.changeText(LabelTextBuilder().setTexts(key.text ?: ""))
                }
                set(clickable) { label.setClickable(it) }
                set(visible) { if (it) label.show() else label.hide() }
                set(rank) { label.changeRank(it) }
                set(tag) { label.tag = it }
                set(onClick) { this.onClick = it ?: { false } }
            },
        )
    }
}

// icon, iconSize(px), text, textStyle을 한 번에 비교하기 위한 내부 키 클래스입니다.
private data class LabelStyleKey(
    val icon: KakaoOverlayImage,
    val iconWidthPx: Int,
    val iconHeightPx: Int,
    val text: String?,
    val textStyle: KakaoPoiTextStyle,
)

internal fun labelStyleFrom(
    context: Context,
    image: KakaoOverlayImage,
    widthPx: Int = 0,
    heightPx: Int = 0,
    sdkTextStyle: LabelTextStyle? = null,
): LabelStyle {
    val bitmap: Bitmap = when (image) {
        is KakaoOverlayImage.FromResource -> {
            val drawable = checkNotNull(
                ResourcesCompat.getDrawable(context.resources, image.resId, context.theme),
            ) { "Drawable resource ${image.resId} could not be loaded." }
            if (widthPx > 0 && heightPx > 0) {
                drawable.toBitmap(width = widthPx, height = heightPx)
            } else {
                drawable.toBitmap()
            }
        }
        is KakaoOverlayImage.FromBitmap -> {
            if (widthPx > 0 && heightPx > 0) {
                Bitmap.createScaledBitmap(image.bitmap, widthPx, heightPx, true)
            } else {
                image.bitmap
            }
        }
    }
    val style = LabelStyle.from(bitmap).setAnchorPoint(0.5f, 1.0f)
    return if (sdkTextStyle != null) style.setTextStyles(sdkTextStyle) else style
}

internal fun KakaoPoiTextStyle.toSdkTextStyle(): LabelTextStyle =
    if (strokeWidth > 0) {
        LabelTextStyle.from(textSize.value.toInt(), textColor.toArgb(), strokeWidth, strokeColor.toArgb())
    } else {
        LabelTextStyle.from(textSize.value.toInt(), textColor.toArgb())
    }
