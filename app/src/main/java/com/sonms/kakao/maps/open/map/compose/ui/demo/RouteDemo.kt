package com.sonms.kakao.maps.open.map.compose.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.model.KakaoPoiTextStyle
import com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoPoi
import com.sonms.kakao.maps.open.map.compose.overlay.route.KakaoArrowRouteLine
import com.sonms.kakao.maps.open.map.compose.overlay.route.KakaoDotRouteLine
import com.sonms.kakao.maps.open.map.compose.overlay.route.KakaoMultiRouteLine
import com.sonms.kakao.maps.open.map.compose.overlay.route.KakaoRouteLine
import com.sonms.kakao.maps.open.map.compose.model.KakaoRouteSegment

// 서울 시청 인근 ~ 종로 ~ 동대문 구간
val routeFocusPositions = listOf(
    KakaoLatLng.from(37.5665, 126.9780),
    KakaoLatLng.from(37.5714, 127.0079),
)

/**
 * 경로선 종류 데모.
 *
 * - 기본 경로선 (실선, 외곽선 포함)
 * - 점선 경로
 * - 화살표 경로 (종점에 방향 표시)
 * - 멀티 경로 (구간별 색상 — 교통 상황)
 * - 출발지/도착지 POI 텍스트 레이블
 */
@Composable
@KakaoMapComposable
fun RouteDemoContent() {
    // ── 공통 좌표 ──────────────────────────────────────────────────────────

    // 기본 경로: 시청 → 종로 → 동대문
    val mainRoute = listOf(
        KakaoLatLng.from(37.5665, 126.9780), // 시청
        KakaoLatLng.from(37.5700, 126.9900), // 종로
        KakaoLatLng.from(37.5714, 127.0079), // 동대문
    )

    // 점선 경로: 메인 경로 남쪽 오프셋
    val dotRoute = listOf(
        KakaoLatLng.from(37.5640, 126.9790),
        KakaoLatLng.from(37.5675, 126.9910),
        KakaoLatLng.from(37.5688, 127.0089),
    )

    // 화살표 경로: 메인 경로 북쪽 오프셋
    val arrowRoute = listOf(
        KakaoLatLng.from(37.5690, 126.9785),
        KakaoLatLng.from(37.5724, 126.9895),
        KakaoLatLng.from(37.5738, 127.0074),
    )

    // 멀티 경로: 시청 → 광화문 → 종로 → 종로3가 구간별 교통 색상
    val multiRoutePositions = listOf(
        KakaoLatLng.from(37.5665, 126.9780), // 시청 (원활)
        KakaoLatLng.from(37.5757, 126.9769), // 광화문 (서행)
        KakaoLatLng.from(37.5731, 126.9910), // 종로3가 (정체)
        KakaoLatLng.from(37.5714, 127.0010), // 종로5가
    )

    // ── 1. 기본 실선 경로 ──────────────────────────────────────────────────
    KakaoRouteLine(
        positions = mainRoute,
        lineWidth = 14f,
        lineColor = Color(0xFF3478F6),
        strokeWidth = 3f,
        strokeColor = Color.White,
        lineId = "demo-main-route",
    )

    // ── 2. 점선 경로 ───────────────────────────────────────────────────────
    KakaoDotRouteLine(
        positions = dotRoute,
        dotColor = Color(0xFFFF6B35),
        lineId = "demo-dot-route",
    )

    // ── 3. 화살표 경로 (종점 방향 표시) ───────────────────────────────────
    KakaoArrowRouteLine(
        positions = arrowRoute,
        lineColor = Color(0xFF9C27B0),
        lineWidth = 12f,
        lineId = "demo-arrow-route",
    )

    // ── 4. 멀티 경로 (구간별 교통 색상) ───────────────────────────────────
    KakaoMultiRouteLine(
        segments = listOf(
            KakaoRouteSegment(
                positions = multiRoutePositions.subList(0, 2),
                lineColor = Color(0xFF00C853), // 초록 — 원활
                lineWidth = 12f,
                strokeWidth = 2f,
                strokeColor = Color.White,
            ),
            KakaoRouteSegment(
                positions = multiRoutePositions.subList(1, 3),
                lineColor = Color(0xFFFF9800), // 주황 — 서행
                lineWidth = 12f,
                strokeWidth = 2f,
                strokeColor = Color.White,
            ),
            KakaoRouteSegment(
                positions = multiRoutePositions.subList(2, 4),
                lineColor = Color(0xFFE53935), // 빨강 — 정체
                lineWidth = 12f,
                strokeWidth = 2f,
                strokeColor = Color.White,
            ),
        ),
        lineId = "demo-multi-route",
    )

    // ── 출발지 · 도착지 POI ────────────────────────────────────────────────
    val labelStyle = KakaoPoiTextStyle(
        textSize = 12.sp,
        textColor = Color(0xFF1A1A1A),
        strokeWidth = 2,
        strokeColor = Color.White,
    )
    KakaoPoi(
        position = mainRoute.first(),
        labelId = "route-start",
        text = "출발",
        textStyle = labelStyle,
    )
    KakaoPoi(
        position = mainRoute.last(),
        labelId = "route-end",
        text = "도착",
        textStyle = labelStyle,
    )
}
