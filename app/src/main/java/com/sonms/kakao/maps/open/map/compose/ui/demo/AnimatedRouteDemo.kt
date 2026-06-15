package com.sonms.kakao.maps.open.map.compose.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.model.KakaoPoiTextStyle
import com.sonms.kakao.maps.open.map.compose.model.RouteAnimationPoi
import com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoPoi
import com.sonms.kakao.maps.open.map.compose.overlay.route.KakaoAnimatedRouteLine

// 경복궁역 → 안국 → 종로3가 → 종로5가 → 동대문역사문화공원
val animatedRouteFocusPositions = listOf(
    KakaoLatLng.from(37.5760, 126.9769),
    KakaoLatLng.from(37.5701, 127.0079),
)

private val routePositions = listOf(
    KakaoLatLng.from(37.5760, 126.9769), // 경복궁역
    KakaoLatLng.from(37.5748, 126.9830), // 안국
    KakaoLatLng.from(37.5731, 126.9910), // 종로3가
    KakaoLatLng.from(37.5714, 126.9970), // 종로5가
    KakaoLatLng.from(37.5701, 127.0079), // 동대문역사문화공원
)

private val stationPois = listOf(
    RouteAnimationPoi(
        position = KakaoLatLng.from(37.5748, 126.9830),
        fullSize = 40.dp,
        labelId = "anim-anguk",
        appearDurationMs = 350,
    ),
    RouteAnimationPoi(
        position = KakaoLatLng.from(37.5731, 126.9910),
        fullSize = 40.dp,
        labelId = "anim-jongno3",
        appearDurationMs = 350,
    ),
    RouteAnimationPoi(
        position = KakaoLatLng.from(37.5714, 126.9970),
        fullSize = 40.dp,
        labelId = "anim-jongno5",
        appearDurationMs = 350,
    ),
    RouteAnimationPoi(
        position = KakaoLatLng.from(37.5701, 127.0079),
        fullSize = 40.dp,
        labelId = "anim-dmd",
        appearDurationMs = 350,
    ),
)

/**
 * 애니메이션 경로 데모.
 *
 * - 경로선이 2.5초에 걸쳐 점진적으로 그려집니다.
 * - 경로선이 각 역에 도달할 때 POI가 팝-인 애니메이션으로 등장합니다.
 * - [restartKey]가 변경되면 애니메이션이 처음부터 재시작됩니다.
 */
@Composable
@KakaoMapComposable
fun AnimatedRouteDemoContent(restartKey: Int) {
    // restartKey가 바뀔 때마다 KakaoAnimatedRouteLine을 재생성하여
    // Animatable이 0f 부터 다시 시작하도록 합니다.
    key(restartKey) {
        KakaoAnimatedRouteLine(
            positions = routePositions,
            durationMs = 2500,
            lineColor = Color(0xFF3478F6),
            lineWidth = 14f,
            strokeWidth = 3f,
            strokeColor = Color.White,
            lineId = "demo-animated-route",
            pois = stationPois,
        )
    }

    // 출발역 레이블은 항상 표시
    KakaoPoi(
        position = routePositions.first(),
        labelId = "anim-start",
        text = "경복궁역",
        textStyle = KakaoPoiTextStyle(
            textSize = 12.sp,
            textColor = Color(0xFF1A1A1A),
            strokeWidth = 2,
            strokeColor = Color.White,
        ),
    )
}
