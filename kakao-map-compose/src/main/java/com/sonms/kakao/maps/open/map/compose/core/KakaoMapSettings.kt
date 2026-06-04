package com.sonms.kakao.maps.open.map.compose

import androidx.compose.runtime.Immutable
import com.kakao.vectormap.MapType
import com.kakao.vectormap.PoiScale

/**
 * 카카오 지도 자체의 표시 속성과 지도 데이터 관련 설정을 담는 값 객체입니다.
 *
 * 위치 추적이나 현재 위치 표시는 포함하지 않습니다. 해당 기능은 권한과 위치 제공자가 필요하므로
 * 별도 설계 단계에서 다룹니다.
 *
 * @param mapType 지도 타입입니다.
 * @param isPoiVisible SDK 기본 POI 표시 여부입니다.
 * @param isPoiClickable SDK 기본 POI 클릭 가능 여부입니다.
 * @param poiScale SDK 기본 POI 크기입니다.
 * @param isCameraAnimationEnabled SDK 카메라 애니메이션 활성화 여부입니다.
 * @param isRoadviewLineOverlayEnabled 로드뷰 라인 오버레이 표시 여부입니다.
 * @param isHillshadingOverlayEnabled 지형 음영 오버레이 표시 여부입니다.
 * @param isBicycleRoadOverlayEnabled 자전거 도로 오버레이 표시 여부입니다.
 * @param isSkyviewHybridOverlayEnabled 스카이뷰 하이브리드 오버레이 표시 여부입니다.
 */
@Immutable
data class KakaoMapProperties(
    val mapType: MapType = MapType.NORMAL,
    val isPoiVisible: Boolean = true,
    val isPoiClickable: Boolean = true,
    val poiScale: PoiScale = PoiScale.REGULAR,
    val isCameraAnimationEnabled: Boolean = true,
    val isRoadviewLineOverlayEnabled: Boolean = false,
    val isHillshadingOverlayEnabled: Boolean = false,
    val isBicycleRoadOverlayEnabled: Boolean = false,
    val isSkyviewHybridOverlayEnabled: Boolean = false,
)

/**
 * 지도 위젯과 제스처처럼 사용자 인터페이스 성격의 설정을 담는 값 객체입니다.
 *
 * @param isCompassEnabled 나침반 위젯 표시 여부입니다.
 * @param isCompassBackToNorthOnClickEnabled 나침반 클릭 시 북쪽 방향으로 복귀할지 여부입니다.
 * @param isScaleBarEnabled 축척 위젯 표시 여부입니다.
 * @param isScaleBarAutoHideEnabled 축척 위젯 자동 숨김 여부입니다.
 * @param isPanGestureEnabled 한 손가락 이동 제스처 활성화 여부입니다.
 * @param isZoomGestureEnabled 확대/축소 제스처 활성화 여부입니다.
 * @param isRotateGestureEnabled 회전 제스처 활성화 여부입니다.
 * @param isTiltGestureEnabled 기울기 제스처 활성화 여부입니다.
 * @param isOneFingerZoomGestureEnabled 한 손가락 확대/축소 제스처 활성화 여부입니다.
 */
@Immutable
data class KakaoMapUiSettings(
    val isCompassEnabled: Boolean = true,
    val isCompassBackToNorthOnClickEnabled: Boolean = true,
    val isScaleBarEnabled: Boolean = true,
    val isScaleBarAutoHideEnabled: Boolean = true,
    val isPanGestureEnabled: Boolean = true,
    val isZoomGestureEnabled: Boolean = true,
    val isRotateGestureEnabled: Boolean = true,
    val isTiltGestureEnabled: Boolean = true,
    val isOneFingerZoomGestureEnabled: Boolean = true,
)
