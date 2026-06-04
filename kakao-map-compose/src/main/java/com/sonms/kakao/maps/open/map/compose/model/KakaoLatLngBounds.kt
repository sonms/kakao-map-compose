package com.sonms.kakao.maps.open.map.compose.model

import androidx.compose.runtime.Immutable
import com.kakao.vectormap.LatLngBounds

/**
 * 여러 좌표를 화면에 맞추기 위해 사용하는 위도/경도 경계 값 객체입니다.
 *
 * @param southwest 남서쪽 끝 좌표입니다.
 * @param northeast 북동쪽 끝 좌표입니다.
 */
@Immutable
data class KakaoLatLngBounds(
    val southwest: KakaoLatLng,
    val northeast: KakaoLatLng,
) {
    /**
     * 카카오 지도 SDK에서 사용하는 [LatLngBounds] 객체로 변환합니다.
     */
    fun toLatLngBounds(): LatLngBounds =
        LatLngBounds(southwest.toLatLng(), northeast.toLatLng())

    companion object {
        /**
         * 좌표 목록 전체를 포함하는 [KakaoLatLngBounds]를 생성합니다.
         *
         * @param positions 경계 계산에 사용할 좌표 목록입니다.
         */
        fun from(positions: List<KakaoLatLng>): KakaoLatLngBounds {
            require(positions.isNotEmpty()) {
                "positions must not be empty."
            }

            var minLatitude = positions.first().latitude
            var maxLatitude = positions.first().latitude
            var minLongitude = positions.first().longitude
            var maxLongitude = positions.first().longitude

            for (position in positions.drop(1)) {
                minLatitude = minOf(minLatitude, position.latitude)
                maxLatitude = maxOf(maxLatitude, position.latitude)
                minLongitude = minOf(minLongitude, position.longitude)
                maxLongitude = maxOf(maxLongitude, position.longitude)
            }

            return KakaoLatLngBounds(
                southwest = KakaoLatLng(minLatitude, minLongitude),
                northeast = KakaoLatLng(maxLatitude, maxLongitude),
            )
        }
    }
}
