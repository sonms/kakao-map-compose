package com.sonms.kakao.maps.open.map.compose.model

import androidx.compose.runtime.Immutable
import com.kakao.vectormap.LatLng

/**
 * Compose 안정성 추론을 위해 사용하는 위도/경도 값 객체입니다.
 *
 * SDK의 [LatLng]를 직접 파라미터로 노출하지 않고 불변 데이터 클래스로 감싸서,
 * 재구성 시 불필요한 invalidation 가능성을 줄입니다.
 *
 * @param latitude 위도 값입니다.
 * @param longitude 경도 값입니다.
 */
@Immutable
data class KakaoLatLng(
    val latitude: Double,
    val longitude: Double,
) {
    /**
     * 카카오 지도 SDK에서 사용하는 [LatLng] 객체로 변환합니다.
     */
    fun toLatLng(): LatLng = LatLng.from(latitude, longitude)

    companion object {
        /**
         * 위도와 경도 값으로 [KakaoLatLng]를 생성합니다.
         *
         * @param latitude 위도 값입니다.
         * @param longitude 경도 값입니다.
         */
        fun from(latitude: Double, longitude: Double): KakaoLatLng =
            KakaoLatLng(latitude = latitude, longitude = longitude)

        /**
         * 카카오 지도 SDK의 [LatLng]를 Compose용 [KakaoLatLng]로 변환합니다.
         *
         * @param latLng 변환할 SDK 좌표 객체입니다.
         */
        fun from(latLng: LatLng): KakaoLatLng =
            KakaoLatLng(latitude = latLng.latitude, longitude = latLng.longitude)
    }
}
