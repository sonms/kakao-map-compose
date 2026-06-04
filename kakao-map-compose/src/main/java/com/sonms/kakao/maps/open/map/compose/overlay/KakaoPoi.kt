package com.sonms.kakao.maps.open.map.compose.overlay

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.sonms.kakao.maps.open.map.compose.R
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.LocalKakaoMapState
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng

/**
 * 카카오 지도 위에 POI(Label)를 표시하는 Composable입니다.
 *
 * 이 Composable은 UI 노드를 그리지 않고, 지도 준비 후 SDK의 [Label]을 생성/갱신/삭제하는
 * 부수 효과만 관리합니다. 따라서 현재 구현은 커스텀 Compose Applier 없이 동작합니다.
 *
 * @param position POI를 표시할 위도/경도 좌표입니다.
 * @param iconResId POI 아이콘으로 사용할 drawable 리소스 ID입니다.
 * @param labelId SDK Label에 부여할 식별자입니다. null이면 SDK가 기본 방식으로 생성합니다.
 * @param onClick POI 클릭 시 호출되는 콜백입니다. true를 반환하면 이벤트가 소비됩니다.
 * @param clickable POI 클릭 가능 여부입니다. 기본값은 [onClick] 존재 여부를 따릅니다.
 * @param visible POI 표시 여부입니다.
 * @param rank POI의 표시 우선순위입니다. 값이 클수록 SDK 정책에 따라 우선 표시될 수 있습니다.
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
    val state = LocalKakaoMapState.current
    val map = state?.map ?: return
    val context = LocalContext.current
    val currentOnClick by rememberUpdatedState(onClick)
    var label by remember(map, labelId) { mutableStateOf<Label?>(null) }

    DisposableEffect(map, labelId) {
        val layer = map.labelManager?.layer
            ?: return@DisposableEffect onDispose {}
        val options = if (labelId == null) {
            LabelOptions.from(position.toLatLng())
        } else {
            LabelOptions.from(labelId, position.toLatLng())
        }
        label = layer.addLabel(
            options
                .setStyles(labelStyleFromDrawable(context, iconResId))
                .setClickable(clickable)
                .setVisible(visible)
                .setRank(rank)
                .setTag(tag),
        )
        onDispose {
            label?.let {
                state.unregisterLabelClickHandler(it)
                it.remove()
            }
            label = null
        }
    }

    DisposableEffect(label, clickable) {
        val currentLabel = label
        if (currentLabel != null && clickable) {
            state.registerLabelClickHandler(currentLabel) { clickedLabel ->
                currentOnClick?.invoke(clickedLabel) ?: false
            }
        }
        onDispose {
            if (currentLabel != null) {
                state.unregisterLabelClickHandler(currentLabel)
            }
        }
    }

    DisposableEffect(label, iconResId) {
        label?.setStyles(labelStyleFromDrawable(context, iconResId))
        onDispose {}
    }

    SideEffect {
        label?.let {
            val sdkPosition = position.toLatLng()
            if (it.position != sdkPosition) {
                it.moveTo(sdkPosition)
            }
            it.setClickable(clickable)
            if (visible) {
                it.show()
            } else {
                it.hide()
            }
            if (it.rank != rank) {
                it.changeRank(rank)
            }
            it.tag = tag
        }
    }
}

/**
 * Drawable 리소스를 카카오 지도 SDK가 사용할 수 있는 [LabelStyle]로 변환합니다.
 *
 * 벡터 drawable을 리소스 ID로 직접 넘기면 SDK 내부에서 원시 리소스로 읽을 수 있으므로,
 * Compose/AppCompat Theme와 무관하게 표시되도록 bitmap으로 변환합니다.
 *
 * @param context drawable을 로드할 Android [Context]입니다.
 * @param iconResId 변환할 drawable 리소스 ID입니다.
 */
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
