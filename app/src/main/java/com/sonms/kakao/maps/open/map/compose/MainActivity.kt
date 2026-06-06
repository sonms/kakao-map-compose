package com.sonms.kakao.maps.open.map.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Alignment
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.kakao.vectormap.KakaoMapSdk
import com.sonms.kakao.maps.open.map.compose.camera.KakaoCameraUpdate
import com.sonms.kakao.maps.open.map.compose.camera.rememberKakaoCameraPositionState
import com.sonms.kakao.maps.open.map.compose.core.KakaoMap
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoPoi
import com.sonms.kakao.maps.open.map.compose.overlay.route.KakaoRouteLine
import com.sonms.kakao.maps.open.map.compose.sample.BuildConfig
import com.sonms.kakao.maps.open.map.compose.ui.theme.KakaomapcomposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
        enableEdgeToEdge()
        setContent {
            KakaomapcomposeTheme {
                var mapInstanceKey by rememberSaveable { mutableIntStateOf(0) }
                var showMap by rememberSaveable { mutableStateOf(true) }
                var showOverlays by rememberSaveable { mutableStateOf(true) }
                val cameraPositionState = rememberKakaoCameraPositionState()
                val routePositions = remember {
                    listOf(
                        KakaoLatLng.from(37.5665, 126.9780),
                        KakaoLatLng.from(37.5700, 126.9820),
                        KakaoLatLng.from(37.3947, 127.1112),
                    )
                }

                LaunchedEffect(mapInstanceKey, routePositions) {
                    cameraPositionState.animate(
                        update = KakaoCameraUpdate.fitBounds(routePositions, padding = 250),
                        durationMs = 1000,
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (showMap) {
                        key(mapInstanceKey) {
                            KakaoMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                onPoiClick = { _, layerId, poiId ->
                                    showToast("SDK POI clicked: $poiId ($layerId)")
                                },
                            ) {
                                if (showOverlays) {
                                    KakaoPoi(
                                        position = routePositions[0],
                                        labelId = "city-hall",
                                        onClick = {
                                            showToast("Custom label clicked: city-hall")
                                            true
                                        },
                                    )
                                    KakaoPoi(
                                        position = routePositions[1],
                                        labelId = "route-middle",
                                        onClick = {
                                            showToast("Custom label clicked: route-middle")
                                            true
                                        },
                                    )
                                    KakaoPoi(
                                        position = routePositions[2],
                                        labelId = "pangyo",
                                        onClick = {
                                            it.moveTo(routePositions[0].toLatLng())
                                            showToast("Custom label moved: pangyo")
                                            true
                                        },
                                    )
                                    KakaoRouteLine(
                                        positions = routePositions,
                                        lineWidth = 14f,
                                        lineColor = Color(0xFF3478F6),
                                        strokeWidth = 4f,
                                        strokeColor = Color.White,
                                        lineId = "sample-route-line",
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .safeDrawingPadding()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Button(
                            onClick = {
                                showMap = false
                                mapInstanceKey += 1
                                showMap = true
                            },
                        ) {
                            Text("Recreate map")
                        }
                        Button(
                            onClick = { showOverlays = !showOverlays },
                        ) {
                            Text(if (showOverlays) "Hide overlays" else "Show overlays")
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
