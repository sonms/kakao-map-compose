package com.sonms.kakao.maps.open.map.compose.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.model.KakaoLodPoiItem
import com.sonms.kakao.maps.open.map.compose.model.KakaoPoiTextStyle
import com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoLodPoi

// 서울 중구 일대 5×5 격자
val lodPoiFocusPositions = listOf(
    KakaoLatLng.from(37.5680, 126.9880),
    KakaoLatLng.from(37.5560, 127.0040),
)

private val lodPoiItems: List<KakaoLodPoiItem> = buildList {
    val latitudes  = listOf(37.5680, 37.5650, 37.5620, 37.5590, 37.5560)
    val longitudes = listOf(126.9880, 126.9920, 126.9960, 127.0000, 127.0040)
    val districts  = listOf("을지로1가", "을지로2가", "을지로3가", "을지로4가", "을지로5가")

    latitudes.forEachIndexed { row, lat ->
        longitudes.forEachIndexed { col, lng ->
            add(
                KakaoLodPoiItem(
                    position = KakaoLatLng.from(lat, lng),
                    labelId = "lod-$row-$col",
                    text = districts[col],
                    textStyle = KakaoPoiTextStyle(
                        textSize = 11.sp,
                        textColor = Color(0xFF1A1A1A),
                        strokeWidth = 2,
                        strokeColor = Color.White,
                    ),
                    rank = (row * 5 + col).toLong(),
                ),
            )
        }
    }
}

/**
 * 대량 LOD POI 데모.
 *
 * - 서울 중구 일대에 5×5 격자로 25개 마커를 배치합니다.
 * - SDK의 [com.kakao.vectormap.label.LodLabelLayer]를 사용하여 줌 레벨에 따라 표시 밀도가 자동 조정됩니다.
 * - 마커 클릭 시 Toast를 표시합니다.
 */
@Composable
@KakaoMapComposable
fun LodPoiDemoContent(onToast: (String) -> Unit) {
    KakaoLodPoi(
        items = lodPoiItems,
        onClick = { lodLabel ->
            onToast("LOD POI 클릭: ${lodLabel.labelId}")
            true
        },
    )
}
