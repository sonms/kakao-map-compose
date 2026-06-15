package com.sonms.kakao.maps.open.map.compose.ui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.model.KakaoOverlayImage
import com.sonms.kakao.maps.open.map.compose.model.KakaoPoiTextStyle
import com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoPoi
import com.sonms.kakao.maps.open.map.compose.sample.R

// 데모 카메라 포커스 범위 (경복궁 ~ 남대문)
val poiFocusPositions = listOf(
    KakaoLatLng.from(37.5797, 126.9770),
    KakaoLatLng.from(37.5597, 126.9752),
)

/**
 * POI + 텍스트 레이블 데모.
 *
 * - 기본 마커 (아이콘만)
 * - 아이콘 + 텍스트 레이블
 * - 외곽선 텍스트 스타일
 * - 클릭 시 위치 이동 (Compose 상태 기반)
 */
@Composable
@KakaoMapComposable
fun PoiDemoContent(onToast: (String) -> Unit) {
    // 클릭 시 Compose 상태를 변경하여 POI를 이동합니다.
    // SDK의 label.moveTo()를 직접 호출하면 다음 재구성 시 원래 위치로 되돌아갑니다.
    var deoksugungPosition by remember {
        mutableStateOf(KakaoLatLng.from(37.5656, 126.9752))
    }

    // 1. 기본 마커 — 아이콘만 표시 (텍스트 없음)
    KakaoPoi(
        position = KakaoLatLng.from(37.5797, 126.9770),
        labelId = "gyeongbokgung",
        onClick = {
            onToast("경복궁 클릭")
            true
        },
    )

    // 2. 아이콘 + 텍스트 레이블 — 가장 기본적인 사용 형태
    KakaoPoi(
        position = KakaoLatLng.from(37.5757, 126.9769),
        labelId = "gwanghwamun",
        text = "광화문",
        textStyle = KakaoPoiTextStyle(
            textSize = 13.sp,
            textColor = Color(0xFF1A1A1A),
            strokeWidth = 2,
            strokeColor = Color.White,
        ),
        onClick = {
            onToast("광화문 클릭")
            true
        },
    )

    // 3. 외곽선 강조 텍스트 — 다크 배경에서 가독성을 높일 때 사용
    KakaoPoi(
        position = KakaoLatLng.from(37.5700, 126.9830),
        labelId = "cheonggyecheon",
        text = "청계천",
        textStyle = KakaoPoiTextStyle(
            textSize = 14.sp,
            textColor = Color(0xFF3478F6),
            strokeWidth = 3,
            strokeColor = Color.White,
        ),
        onClick = {
            onToast("청계천 클릭")
            true
        },
    )

    // 4. 커스텀 아이콘 + 텍스트 — 앱 아이콘을 마커로 사용
    KakaoPoi(
        position = KakaoLatLng.from(37.5740, 126.9855),
        labelId = "insadong",
        icon = KakaoOverlayImage.FromResource(R.drawable.ic_launcher_background),
        iconSize = DpSize(36.dp, 36.dp),
        text = "인사동",
        textStyle = KakaoPoiTextStyle(
            textSize = 12.sp,
            textColor = Color(0xFFE53935),
            strokeWidth = 2,
            strokeColor = Color.White,
        ),
        onClick = {
            onToast("인사동 클릭")
            true
        },
    )

    // 5. 클릭 시 이동 — Compose 상태 변경으로 POI 위치를 갱신
    //    클릭하면 광화문 위치로 이동합니다.
    KakaoPoi(
        position = deoksugungPosition,
        labelId = "deoksugung",
        text = "덕수궁 (클릭 시 이동)",
        textStyle = KakaoPoiTextStyle(
            textSize = 12.sp,
            textColor = Color(0xFF9C27B0),
            strokeWidth = 2,
            strokeColor = Color.White,
        ),
        onClick = {
            deoksugungPosition = KakaoLatLng.from(37.5757, 126.9769)
            onToast("덕수궁 → 광화문으로 이동")
            true
        },
    )

    // 6. 남대문 — 하단에 위치, 카메라 bounds 확인용
    KakaoPoi(
        position = KakaoLatLng.from(37.5597, 126.9752),
        labelId = "namdaemun",
        text = "남대문",
        textStyle = KakaoPoiTextStyle(
            textSize = 13.sp,
            textColor = Color(0xFF1A1A1A),
            strokeWidth = 2,
            strokeColor = Color.White,
        ),
    )
}
