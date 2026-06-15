package com.sonms.kakao.maps.open.map.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kakao.vectormap.KakaoMapSdk
import com.sonms.kakao.maps.open.map.compose.sample.BuildConfig
import com.sonms.kakao.maps.open.map.compose.ui.KakaoMapSampleApp
import com.sonms.kakao.maps.open.map.compose.ui.theme.KakaomapcomposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
        enableEdgeToEdge()
        setContent {
            KakaomapcomposeTheme {
                KakaoMapSampleApp(
                    onToast = { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
    }
}
