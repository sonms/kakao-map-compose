package com.sonms.kakao.maps.open.map.compose.camera

import androidx.compose.runtime.Immutable
import com.kakao.vectormap.camera.CameraPosition
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng

/**
 * Compose 안정성 추론을 위해 사용하는 카메라 위치 값 객체입니다.
 *
 * SDK의 [CameraPosition]을 직접 파라미터로 노출하지 않고 불변 데이터 클래스로 감싸서
 * 재구성 시 불필요한 invalidation 가능성을 줄입니다.
 *
 * @param target 카메라가 바라보는 중심 좌표입니다.
 * @param zoomLevel 카메라 줌 레벨입니다.
 * @param tiltAngle 카메라 기울기 각도입니다. 기본값은 0.0입니다.
 * @param rotationAngle 카메라 회전 각도입니다. 기본값은 0.0입니다.
 */
@Immutable
data class KakaoCameraPosition(
    val target: KakaoLatLng,
    val zoomLevel: Int,
    val tiltAngle: Double = 0.0,
    val rotationAngle: Double = 0.0,
) {
    /**
     * 카카오 지도 SDK에서 사용하는 [CameraPosition] 객체로 변환합니다.
     */
    fun toCameraPosition(): CameraPosition =
        CameraPosition.from(target.latitude, target.longitude, zoomLevel, tiltAngle, rotationAngle, 0.0)

    companion object {
        internal val Default = KakaoCameraPosition(
            target = KakaoLatLng(37.5665, 126.9780),
            zoomLevel = 15,
        )

        /**
         * 카카오 지도 SDK의 [CameraPosition]을 Compose용 [KakaoCameraPosition]으로 변환합니다.
         *
         * @param cameraPosition 변환할 SDK 카메라 위치 객체입니다.
         */
        fun from(cameraPosition: CameraPosition): KakaoCameraPosition = KakaoCameraPosition(
            target = KakaoLatLng.Companion.from(cameraPosition.position),
            zoomLevel = cameraPosition.zoomLevel,
            tiltAngle = cameraPosition.tiltAngle,
            rotationAngle = cameraPosition.rotationAngle,
        )
    }
}
