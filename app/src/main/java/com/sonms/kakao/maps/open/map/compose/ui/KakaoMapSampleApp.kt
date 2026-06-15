package com.sonms.kakao.maps.open.map.compose.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sonms.kakao.maps.open.map.compose.camera.KakaoCameraUpdate
import com.sonms.kakao.maps.open.map.compose.camera.rememberKakaoCameraPositionState
import com.sonms.kakao.maps.open.map.compose.core.KakaoMap
import com.sonms.kakao.maps.open.map.compose.ui.demo.AnimatedRouteDemoContent
import com.sonms.kakao.maps.open.map.compose.ui.demo.animatedRouteFocusPositions
import com.sonms.kakao.maps.open.map.compose.ui.demo.ClusterDemoContent
import com.sonms.kakao.maps.open.map.compose.ui.demo.clusterFocusPositions
import com.sonms.kakao.maps.open.map.compose.ui.demo.LodPoiDemoContent
import com.sonms.kakao.maps.open.map.compose.ui.demo.lodPoiFocusPositions
import com.sonms.kakao.maps.open.map.compose.ui.demo.PoiDemoContent
import com.sonms.kakao.maps.open.map.compose.ui.demo.poiFocusPositions
import com.sonms.kakao.maps.open.map.compose.ui.demo.RouteDemoContent
import com.sonms.kakao.maps.open.map.compose.ui.demo.routeFocusPositions
import com.sonms.kakao.maps.open.map.compose.ui.demo.ShapeDemoContent
import com.sonms.kakao.maps.open.map.compose.ui.demo.shapeFocusPositions

enum class Demo(val label: String) {
    POI("POI"),
    ROUTE("경로선"),
    ANIMATED_ROUTE("애니메이션"),
    SHAPES("도형"),
    LOD_POI("대량 POI"),
    CLUSTER("클러스터"),
}

@Composable
fun KakaoMapSampleApp(onToast: (String) -> Unit) {
    val cameraPositionState = rememberKakaoCameraPositionState()
    var currentDemo by rememberSaveable { mutableStateOf(Demo.POI) }
    var animatedRouteRestartKey by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(currentDemo) {
        val (positions, padding) = when (currentDemo) {
            Demo.POI -> poiFocusPositions to 200
            Demo.ROUTE -> routeFocusPositions to 200
            Demo.ANIMATED_ROUTE -> animatedRouteFocusPositions to 150
            Demo.SHAPES -> shapeFocusPositions to 250
            Demo.LOD_POI -> lodPoiFocusPositions to 100
            Demo.CLUSTER -> clusterFocusPositions to 100
        }
        cameraPositionState.animate(
            update = KakaoCameraUpdate.fitBounds(positions, padding = padding),
            durationMs = 600,
        )
    }

    Box(Modifier.fillMaxSize()) {
        KakaoMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onPoiClick = { _, layerId, poiId ->
                onToast("SDK POI 클릭: $poiId / $layerId")
            },
        ) {
            when (currentDemo) {
                Demo.POI -> PoiDemoContent(onToast = onToast)
                Demo.ROUTE -> RouteDemoContent()
                Demo.ANIMATED_ROUTE -> AnimatedRouteDemoContent(restartKey = animatedRouteRestartKey)
                Demo.SHAPES -> ShapeDemoContent()
                Demo.LOD_POI -> LodPoiDemoContent(onToast = onToast)
                Demo.CLUSTER -> ClusterDemoContent(cameraPositionState = cameraPositionState)
            }
        }

        // 애니메이션 경로 데모에서만 재시작 버튼 표시
        if (currentDemo == Demo.ANIMATED_ROUTE) {
            Button(
                onClick = { animatedRouteRestartKey++ },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 56.dp),
            ) {
                Text("재시작")
            }
        }

        DemoTabBar(
            currentDemo = currentDemo,
            onDemoSelected = { currentDemo = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
        )
    }
}

@Composable
private fun DemoTabBar(
    currentDemo: Demo,
    onDemoSelected: (Demo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Demo.entries.forEach { demo ->
                FilterChip(
                    selected = demo == currentDemo,
                    onClick = { onDemoSelected(demo) },
                    label = { Text(demo.label) },
                )
            }
        }
    }
}
