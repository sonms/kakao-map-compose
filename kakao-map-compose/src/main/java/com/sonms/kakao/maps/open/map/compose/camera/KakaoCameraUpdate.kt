package com.sonms.kakao.maps.open.map.compose.camera

import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLngBounds

/**
 * Compose API에서 자주 쓰는 카메라 이동 요청을 만드는 팩토리입니다.
 */
object KakaoCameraUpdate {
    /**
     * 특정 좌표로 카메라 중심을 이동하는 업데이트를 생성합니다.
     *
     * @param position 이동할 중심 좌표입니다.
     */
    fun newCenterPosition(position: KakaoLatLng): CameraUpdate =
        CameraUpdateFactory.newCenterPosition(position.toLatLng())

    /**
     * 특정 좌표와 줌 레벨로 카메라를 이동하는 업데이트를 생성합니다.
     *
     * @param position 이동할 중심 좌표입니다.
     * @param zoomLevel 적용할 줌 레벨입니다.
     */
    fun newCenterPosition(
        position: KakaoLatLng,
        zoomLevel: Int,
    ): CameraUpdate =
        CameraUpdateFactory.newCenterPosition(position.toLatLng(), zoomLevel)

    /**
     * 특정 위치, 줌, 회전, 기울기를 포함한 전체 카메라 상태로 이동하는 업데이트를 생성합니다.
     *
     * @param position 이동할 카메라 위치입니다.
     */
    fun newCameraPosition(position: KakaoCameraPosition): CameraUpdate =
        CameraUpdateFactory.newCameraPosition(position.toCameraPosition())

    /**
     * 카메라를 지정한 회전 각도로 회전하는 업데이트를 생성합니다.
     *
     * @param rotationAngle 적용할 회전 각도(도)입니다.
     */
    fun rotateTo(rotationAngle: Double): CameraUpdate =
        CameraUpdateFactory.rotateTo(rotationAngle)

    /**
     * 카메라를 지정한 기울기 각도로 기울이는 업데이트를 생성합니다.
     *
     * @param tiltAngle 적용할 기울기 각도(도)입니다.
     */
    fun tiltTo(tiltAngle: Double): CameraUpdate =
        CameraUpdateFactory.tiltTo(tiltAngle)

    /**
     * 여러 좌표가 모두 화면 안에 들어오도록 카메라를 이동하는 업데이트를 생성합니다.
     *
     * @param positions 화면 안에 포함할 좌표 목록입니다.
     * @param padding 화면 가장자리와 좌표 사이에 둘 여백(px)입니다.
     */
    fun fitBounds(
        positions: List<KakaoLatLng>,
        padding: Int = 0,
    ): CameraUpdate =
        fitBounds(KakaoLatLngBounds.from(positions), padding)

    /**
     * 여러 좌표가 모두 화면 안에 들어오도록 가로/세로 여백을 개별 지정하는 업데이트를 생성합니다.
     *
     * @param positions 화면 안에 포함할 좌표 목록입니다.
     * @param paddingX 좌우 여백(px)입니다.
     * @param paddingY 상하 여백(px)입니다.
     */
    fun fitBounds(
        positions: List<KakaoLatLng>,
        paddingX: Int,
        paddingY: Int,
    ): CameraUpdate =
        CameraUpdateFactory.fitMapPoints(
            positions.map { it.toLatLng() }.toTypedArray(),
            paddingX,
            paddingY,
        )

    /**
     * 지정한 경계가 화면 안에 들어오도록 카메라를 이동하는 업데이트를 생성합니다.
     *
     * @param bounds 화면 안에 포함할 좌표 경계입니다.
     * @param padding 화면 가장자리와 경계 사이에 둘 여백(px)입니다.
     */
    fun fitBounds(
        bounds: KakaoLatLngBounds,
        padding: Int = 0,
    ): CameraUpdate =
        CameraUpdateFactory.fitMapPoints(bounds.toLatLngBounds(), padding)

    /**
     * 카메라를 한 단계 확대하는 업데이트를 생성합니다.
     */
    fun zoomIn(): CameraUpdate = CameraUpdateFactory.zoomIn()

    /**
     * 카메라를 한 단계 축소하는 업데이트를 생성합니다.
     */
    fun zoomOut(): CameraUpdate = CameraUpdateFactory.zoomOut()

    /**
     * 특정 줌 레벨로 카메라를 이동하는 업데이트를 생성합니다.
     *
     * @param zoomLevel 적용할 줌 레벨입니다.
     */
    fun zoomTo(zoomLevel: Int): CameraUpdate = CameraUpdateFactory.zoomTo(zoomLevel)
}
