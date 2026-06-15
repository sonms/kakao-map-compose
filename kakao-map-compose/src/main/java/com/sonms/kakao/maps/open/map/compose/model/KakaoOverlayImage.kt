package com.sonms.kakao.maps.open.map.compose.model

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.sonms.kakao.maps.open.map.compose.R

/**
 * POI 아이콘의 소스를 나타내는 sealed class입니다.
 *
 * [com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoPoi]와
 * [KakaoLodPoiItem]의 `icon` 파라미터에 사용됩니다.
 *
 * - [FromResource]: drawable 리소스 ID로 아이콘을 지정합니다.
 * - [FromBitmap]: [Bitmap] 객체로 아이콘을 지정합니다. 호출자가 Bitmap 수명을 관리해야 합니다.
 */
@Stable
sealed class KakaoOverlayImage {

    @Immutable
    data class FromResource(@param:DrawableRes val resId: Int) : KakaoOverlayImage()

    @Stable
    data class FromBitmap(val bitmap: Bitmap) : KakaoOverlayImage()

    companion object {
        /** 기본 마커 아이콘입니다. */
        val Default: KakaoOverlayImage = FromResource(R.drawable.kakao_map_default_marker)

        fun resource(@DrawableRes resId: Int): KakaoOverlayImage = FromResource(resId)
        fun bitmap(bitmap: Bitmap): KakaoOverlayImage = FromBitmap(bitmap)
    }
}
