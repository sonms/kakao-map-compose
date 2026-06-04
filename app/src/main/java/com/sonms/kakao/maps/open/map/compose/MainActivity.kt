package com.sonms.kakao.maps.open.map.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kakao.vectormap.KakaoMapSdk
import com.sonms.kakao.maps.open.map.compose.sample.BuildConfig
import com.sonms.kakao.maps.open.map.compose.ui.theme.KakaomapcomposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
        enableEdgeToEdge()
        setContent {
            KakaomapcomposeTheme {
                KakaoMap(modifier = Modifier.fillMaxSize()) {
                    KakaoPoi(position = KakaoLatLng.from(37.5665, 126.9780))
                    KakaoPoi(position = KakaoLatLng.from(37.5700, 126.9820))
                    KakaoPoi(
                        position = KakaoLatLng.from(37.3947, 127.1112),
                        onClick = {
                            it.moveTo(KakaoLatLng.from(37.5665, 126.9780).toLatLng())
                            true
                        },
                    )
                    KakaoRouteLine(
                        positions = listOf(
                            KakaoLatLng.from(37.5665, 126.9780),
                            KakaoLatLng.from(37.5700, 126.9820),
                            KakaoLatLng.from(37.3947, 127.1112),
                        ),
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
