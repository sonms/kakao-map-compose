package com.sonms.kakao.maps.open.map.compose.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.model.KakaoPoiTextStyle
import com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoPoi
import com.sonms.kakao.maps.open.map.compose.overlay.shape.KakaoPolygon
import com.sonms.kakao.maps.open.map.compose.overlay.shape.KakaoPolyline

// 광화문 ~ 청계천 인근 구역
val shapeFocusPositions = listOf(
    KakaoLatLng.from(37.5780, 126.9730),
    KakaoLatLng.from(37.5670, 126.9870),
)

/**
 * 도형 오버레이 데모.
 *
 * - KakaoPolyline: 청계천 산책로 일부 (선)
 * - KakaoPolygon: 경복궁 궁역 (채색 다각형)
 * - 각 도형의 레이블 POI
 */
@Composable
@KakaoMapComposable
fun ShapeDemoContent() {
    // ── 폴리라인: 청계천 일부 구간 ────────────────────────────────────────
    val cheonggyecheonPath = listOf(
        KakaoLatLng.from(37.5700, 126.9777),
        KakaoLatLng.from(37.5697, 126.9830),
        KakaoLatLng.from(37.5693, 126.9885),
        KakaoLatLng.from(37.5689, 126.9930),
    )

    KakaoPolyline(
        positions = cheonggyecheonPath,
        lineWidth = 6f,
        lineColor = Color(0xFF29B6F6),  // 하늘색
        strokeWidth = 2f,
        strokeColor = Color(0xFF0277BD),
        polylineId = "demo-cheonggyecheon",
    )

    KakaoPoi(
        position = cheonggyecheonPath[1],
        labelId = "cheonggyecheon-label",
        text = "청계천",
        textStyle = KakaoPoiTextStyle(
            textSize = 12.sp,
            textColor = Color(0xFF0277BD),
            strokeWidth = 2,
            strokeColor = Color.White,
        ),
    )

    // ── 폴리곤: 경복궁 궁역 (사각형 근사) ────────────────────────────────
    val gyeongbokgungArea = listOf(
        KakaoLatLng.from(37.5820, 126.9745),
        KakaoLatLng.from(37.5820, 126.9810),
        KakaoLatLng.from(37.5770, 126.9810),
        KakaoLatLng.from(37.5770, 126.9745),
    )

    KakaoPolygon(
        positions = gyeongbokgungArea,
        fillColor = Color(0x443478F6),   // 반투명 파랑
        strokeWidth = 3f,
        strokeColor = Color(0xFF3478F6),
        polygonId = "demo-gyeongbokgung",
    )

    KakaoPoi(
        position = KakaoLatLng.from(37.5797, 126.9770),
        labelId = "gyeongbokgung-label",
        text = "경복궁",
        textStyle = KakaoPoiTextStyle(
            textSize = 13.sp,
            textColor = Color(0xFF1A1A1A),
            strokeWidth = 2,
            strokeColor = Color.White,
        ),
    )

    // ── 폴리곤: 광화문 광장 ───────────────────────────────────────────────
    val gwanghwamunSquare = listOf(
        KakaoLatLng.from(37.5765, 126.9763),
        KakaoLatLng.from(37.5765, 126.9775),
        KakaoLatLng.from(37.5748, 126.9775),
        KakaoLatLng.from(37.5748, 126.9763),
    )

    KakaoPolygon(
        positions = gwanghwamunSquare,
        fillColor = Color(0x44FF9800),   // 반투명 주황
        strokeWidth = 2f,
        strokeColor = Color(0xFFFF9800),
        polygonId = "demo-gwanghwamun-square",
    )

    KakaoPoi(
        position = KakaoLatLng.from(37.5757, 126.9769),
        labelId = "gwanghwamun-label",
        text = "광화문 광장",
        textStyle = KakaoPoiTextStyle(
            textSize = 12.sp,
            textColor = Color(0xFFE65100),
            strokeWidth = 2,
            strokeColor = Color.White,
        ),
    )
}
