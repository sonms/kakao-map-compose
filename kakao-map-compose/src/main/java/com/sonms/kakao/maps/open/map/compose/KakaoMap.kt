package com.sonms.kakao.maps.open.map.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kakao.vectormap.KakaoMap as KakaoMapSdk
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer

internal val LocalKakaoMapState = compositionLocalOf<KakaoMapState?> { null }

/**
 * 카카오 지도 SDK의 [MapView]를 Compose에서 사용할 수 있도록 감싸는 루트 Composable입니다.
 *
 * [AndroidView]로 실제 지도 View를 생성하고, 호스트 Lifecycle에 맞춰 `resume`, `pause`,
 * `finish`를 호출합니다. 지도 준비가 끝난 뒤에만 [content]를 실행하므로 내부의
 * [KakaoPoi] 같은 지도 오버레이는 준비된 [KakaoMapState.map]을 사용할 수 있습니다.
 *
 * @param modifier 지도 View에 적용할 Compose [Modifier]입니다.
 * @param state 지도 SDK 객체와 오버레이 이벤트 핸들러를 보관하는 상태 객체입니다.
 * @param content 지도 준비 후 실행되는 카카오 지도 전용 오버레이 Composable 영역입니다.
 */
@Composable
fun KakaoMap(
    modifier: Modifier = Modifier,
    state: KakaoMapState = rememberKakaoMapState(),
    content: @KakaoMapComposable @Composable () -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember(context) { MapView(context) }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
    )

    DisposableEffect(lifecycleOwner, mapView) {
        var finished = false

        /**
         * [MapView]를 한 번만 종료하고, SDK 참조와 클릭 핸들러를 정리합니다.
         */
        fun finishMapView() {
            if (!finished) {
                finished = true
                state.map = null
                state.clearLabelClickHandlers()
                mapView.finish()
            }
        }

        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    state.map = null
                    state.clearLabelClickHandlers()
                }
                override fun onMapError(error: Exception) {
                    state.map = null
                    state.clearLabelClickHandlers()
                }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(kakaoMap: KakaoMapSdk) {
                    kakaoMap.setOnLabelClickListener(
                        object : KakaoMapSdk.OnLabelClickListener {
                            override fun onLabelClicked(
                                kakaoMap: KakaoMapSdk,
                                labelLayer: LabelLayer,
                                label: Label,
                            ): Boolean = state.dispatchLabelClick(label)
                        },
                    )
                    state.map = kakaoMap
                }
            },
        )

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.resume()
                Lifecycle.Event.ON_PAUSE -> mapView.pause()
                Lifecycle.Event.ON_DESTROY -> finishMapView()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            finishMapView()
        }
    }

    if (state.map != null) {
        CompositionLocalProvider(LocalKakaoMapState provides state) {
            content()
        }
    }
}
