package com.sonms.kakao.maps.open.map.compose.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * [com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoPoi]의 텍스트 스타일을 정의합니다.
 *
 * @param textSize 텍스트 크기입니다. SP 단위로 지정합니다.
 * @param textColor 텍스트 색상입니다.
 * @param strokeWidth 텍스트 외곽선 두께입니다. 0이면 외곽선을 표시하지 않습니다.
 * @param strokeColor 텍스트 외곽선 색상입니다.
 */
@Immutable
data class KakaoPoiTextStyle(
    val textSize: TextUnit = 14.sp,
    val textColor: Color = Color.Black,
    val strokeWidth: Int = 0,
    val strokeColor: Color = Color.Transparent,
) {
    companion object {
        val Default = KakaoPoiTextStyle()
    }
}
