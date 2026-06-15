package com.sonms.kakao.maps.open.map.compose.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.DpSize

/**
 * [com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoLodPoi]에 전달하는 개별 POI 항목입니다.
 *
 * @param position POI의 위도/경도 좌표입니다.
 * @param labelId SDK LodLabel에 부여할 식별자입니다. null이면 SDK가 자동 부여합니다.
 * @param icon POI 아이콘입니다. [KakaoOverlayImage.resource] 또는 [KakaoOverlayImage.bitmap]으로 생성합니다.
 * @param iconSize 아이콘 렌더링 크기입니다. [DpSize.Unspecified]이면 drawable의 intrinsic 크기를 사용합니다.
 *   벡터 drawable은 이 값을 지정해야 선명하게 렌더링됩니다.
 * @param text POI 위에 표시할 텍스트입니다. null이면 텍스트를 표시하지 않습니다.
 * @param textStyle 텍스트 크기, 색상, 외곽선 등 텍스트 스타일입니다. [text]가 null이면 무시됩니다.
 * @param rank POI 표시 우선순위입니다.
 * @param tag POI에 연결할 태그입니다.
 */
@Immutable
data class KakaoLodPoiItem(
    val position: KakaoLatLng,
    val labelId: String? = null,
    val icon: KakaoOverlayImage = KakaoOverlayImage.Default,
    val iconSize: DpSize = DpSize.Unspecified,
    val text: String? = null,
    val textStyle: KakaoPoiTextStyle = KakaoPoiTextStyle.Default,
    val rank: Long = 0L,
    val tag: String? = null,
)
