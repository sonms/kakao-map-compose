package com.sonms.kakao.maps.open.map.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kakao.vectormap.KakaoMapSdk
import com.sonms.kakao.maps.open.map.compose.camera.KakaoCameraUpdate
import com.sonms.kakao.maps.open.map.compose.camera.rememberKakaoCameraPositionState
import com.sonms.kakao.maps.open.map.compose.core.KakaoMap
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.overlay.KakaoPoi
import com.sonms.kakao.maps.open.map.compose.overlay.KakaoRouteLine
import com.sonms.kakao.maps.open.map.compose.sample.BuildConfig
import com.sonms.kakao.maps.open.map.compose.ui.theme.KakaomapcomposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
        enableEdgeToEdge()
        setContent {
            KakaomapcomposeTheme {
                val cameraPositionState = rememberKakaoCameraPositionState()
                val routePositions = remember {
                    listOf(
                        KakaoLatLng.from(37.5665, 126.9780),
                        KakaoLatLng.from(37.5700, 126.9820),
                        KakaoLatLng.from(37.3947, 127.1112),
                    )
                }

                LaunchedEffect(routePositions) {
                    cameraPositionState.animate(
                        update = KakaoCameraUpdate.fitBounds(routePositions, padding = 250),
                        durationMs = 1000,
                    )
                }

                KakaoMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                ) {
                    KakaoPoi(position = routePositions[0])
                    KakaoPoi(position = routePositions[1])
                    KakaoPoi(
                        position = routePositions[2],
                        onClick = {
                            it.moveTo(routePositions[0].toLatLng())
                            true
                        },
                    )
                    KakaoRouteLine(
                        positions = routePositions,
                        lineWidth = 14f,
                        lineColor = Color(0xFF3478F6),
                        strokeWidth = 4f,
                        strokeColor = Color.White,
                    )
                }
            }
        }
    }
}
