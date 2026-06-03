package com.sonms.kakao.maps.open.map.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kakao.vectormap.KakaoMap

@Stable
class KakaoMapState {
    var map: KakaoMap? by mutableStateOf(null)
        internal set
}

@Composable
fun rememberKakaoMapState(): KakaoMapState = remember { KakaoMapState() }
