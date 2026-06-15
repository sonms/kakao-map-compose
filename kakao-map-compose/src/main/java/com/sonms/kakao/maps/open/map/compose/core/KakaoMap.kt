package com.sonms.kakao.maps.open.map.compose.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap as KakaoMapSdk
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapOverlay
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LodLabel
import com.kakao.vectormap.label.LodLabelLayer
import com.sonms.kakao.maps.open.map.compose.camera.KakaoCameraPosition
import com.sonms.kakao.maps.open.map.compose.camera.KakaoCameraPositionState
import com.sonms.kakao.maps.open.map.compose.camera.rememberKakaoCameraPositionState
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng

internal val LocalKakaoMapState = compositionLocalOf<KakaoMapState?> { null }

/**
 * 카카오 지도 SDK의 [MapView]를 Compose에서 사용할 수 있도록 감싸는 루트 Composable입니다.
 *
 * [AndroidView]로 실제 지도 View를 생성하고, 호스트 Lifecycle에 맞춰 `resume`, `pause`,
 * `finish`를 호출합니다. 지도 준비가 끝난 뒤에만 [content]를 실행하므로
 * [com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoPoi],
 * [com.sonms.kakao.maps.open.map.compose.overlay.route.KakaoRouteLine] 같은 지도 오버레이를
 * 선언적으로 추가할 수 있습니다.
 *
 * @param modifier 지도 View에 적용할 Compose [Modifier]입니다.
 * @param state 지도 SDK 객체와 오버레이 이벤트 핸들러를 보관하는 상태 객체입니다.
 * @param cameraPositionState 카메라 위치와 이동 상태를 관리하는 상태 객체입니다.
 * @param properties 지도 타입, 기본 POI, 지도 오버레이 같은 지도 데이터 성격의 설정입니다.
 * @param uiSettings 나침반, 축척, 제스처 같은 UI 성격의 설정입니다.
 * @param onPoiClick SDK 기본 POI 클릭 시 호출되는 콜백입니다.
 * @param content 지도 준비 후 실행되는 카카오 지도 전용 오버레이 Composable 영역입니다.
 */
@Composable
@Suppress("ComposableTargetMismatch")
fun KakaoMap(
    modifier: Modifier = Modifier,
    state: KakaoMapState = rememberKakaoMapState(),
    cameraPositionState: KakaoCameraPositionState = rememberKakaoCameraPositionState(),
    properties: KakaoMapProperties = KakaoMapProperties(),
    uiSettings: KakaoMapUiSettings = KakaoMapUiSettings(),
    onPoiClick: ((position: KakaoLatLng, layerId: String, poiId: String) -> Unit)? = null,
    content: @Composable @KakaoMapComposable () -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember(context) { MapView(context) }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
    )

    val currentCameraPositionState by rememberUpdatedState(cameraPositionState)
    val currentOnPoiClick by rememberUpdatedState(onPoiClick)

    DisposableEffect(lifecycleOwner, mapView) {
        var finished = false

        fun finishMapView() {
            if (!finished) {
                finished = true
                currentCameraPositionState.isMoving = false
                state.disposeMapContent()
                state.map = null
                mapView.finish()
            }
        }

        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    currentCameraPositionState.isMoving = false
                    state.map = null
                    state.clearLabelClickHandlers()
                    state.clearLodLabelClickHandlers()
                }
                override fun onMapError(error: Exception) {
                    currentCameraPositionState.isMoving = false
                    state.map = null
                    state.clearLabelClickHandlers()
                    state.clearLodLabelClickHandlers()
                }
            },
            object : KakaoMapReadyCallback() {
                override fun getPosition(): LatLng =
                    currentCameraPositionState.position.target.toLatLng()

                override fun getZoomLevel(): Int =
                    currentCameraPositionState.position.zoomLevel

                override fun onMapReady(kakaoMap: KakaoMapSdk) {
                    kakaoMap.setOnPoiClickListener(
                        object : KakaoMapSdk.OnPoiClickListener {
                            override fun onPoiClicked(
                                kakaoMap: KakaoMapSdk,
                                position: LatLng,
                                layerId: String,
                                poiId: String,
                            ) {
                                currentOnPoiClick?.invoke(
                                    KakaoLatLng.from(position),
                                    layerId,
                                    poiId,
                                )
                            }
                        },
                    )
                    kakaoMap.setOnLabelClickListener(
                        object : KakaoMapSdk.OnLabelClickListener {
                            override fun onLabelClicked(
                                kakaoMap: KakaoMapSdk,
                                labelLayer: LabelLayer,
                                label: Label,
                            ): Boolean = state.dispatchLabelClick(label)
                        },
                    )
                    kakaoMap.setOnLodLabelClickListener(
                        object : KakaoMapSdk.OnLodLabelClickListener {
                            override fun onLodLabelClicked(
                                kakaoMap: KakaoMapSdk,
                                lodLabelLayer: LodLabelLayer,
                                lodLabel: LodLabel,
                            ): Boolean = state.dispatchLodLabelClick(lodLabelLayer, lodLabel)
                        },
                    )
                    kakaoMap.setOnCameraMoveStartListener(
                        object : KakaoMapSdk.OnCameraMoveStartListener {
                            override fun onCameraMoveStart(
                                kakaoMap: KakaoMapSdk,
                                gestureType: GestureType,
                            ) {
                                currentCameraPositionState.isMoving = true
                            }
                        },
                    )
                    kakaoMap.setOnCameraMoveEndListener(
                        object : KakaoMapSdk.OnCameraMoveEndListener {
                            override fun onCameraMoveEnd(
                                kakaoMap: KakaoMapSdk,
                                cameraPosition: CameraPosition,
                                gestureType: GestureType,
                            ) {
                                currentCameraPositionState.isMoving = false
                                currentCameraPositionState.position =
                                    KakaoCameraPosition.from(cameraPosition)
                            }
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

    val map = state.map
    if (map != null) {
        DisposableEffect(map, properties) {
            map.applyProperties(properties)
            onDispose {}
        }

        DisposableEffect(map, uiSettings) {
            map.applyUiSettings(uiSettings)
            onDispose {}
        }

        // composition body에서 읽어야 mutableStateOf 변경 시 재컴포지션이 트리거됨.
        val moveVersion = cameraPositionState.pendingMoveVersion
        SideEffect {
            if (moveVersion > 0) {
                cameraPositionState.drainPendingMoves().forEach { (update, animation) ->
                    if (animation != null) map.moveCamera(update, animation)
                    else map.moveCamera(update)
                }
            }
        }
        CompositionLocalProvider(LocalKakaoMapState provides state) {
            val parentComposition = rememberCompositionContext()
            val currentContent by rememberUpdatedState(content)

            val mapComposition = remember(map) {
                Composition(
                    KakaoMapApplier(KakaoMapNodeRoot(map, state)),
                    parentComposition,
                )
            }
            DisposableEffect(map) {
                var disposed = false
                val disposeMapContent = {
                    if (!disposed) {
                        disposed = true
                        mapComposition.dispose()
                        state.clearLabelClickHandlers()
                        state.clearLodLabelClickHandlers()
                    }
                }
                state.setMapContentDisposer(disposeMapContent)
                onDispose {
                    state.clearMapContentDisposer(disposeMapContent)
                    disposeMapContent()
                }
            }
            LaunchedEffect(mapComposition) {
                mapComposition.setContent { currentContent() }
            }
        }
    }
}

/**
 * [KakaoMapProperties] 값을 SDK 지도 객체에 반영합니다.
 */
private fun KakaoMapSdk.applyProperties(properties: KakaoMapProperties) {
    changeMapType(properties.mapType)
    setPoiVisible(properties.isPoiVisible)
    setPoiClickable(properties.isPoiClickable)
    setPoiScale(properties.poiScale)
    setCameraAnimateEnable(properties.isCameraAnimationEnabled)

    setOverlayVisible(MapOverlay.ROADVIEW_LINE, properties.isRoadviewLineOverlayEnabled)
    setOverlayVisible(MapOverlay.HILLSHADING, properties.isHillshadingOverlayEnabled)
    setOverlayVisible(MapOverlay.BICYCLE_ROAD, properties.isBicycleRoadOverlayEnabled)
    setOverlayVisible(MapOverlay.SKYVIEW_HYBRID, properties.isSkyviewHybridOverlayEnabled)
}

/**
 * [KakaoMapUiSettings] 값을 SDK 지도 객체에 반영합니다.
 */
private fun KakaoMapSdk.applyUiSettings(uiSettings: KakaoMapUiSettings) {
    compass?.let {
        if (uiSettings.isCompassEnabled) {
            it.show()
        } else {
            it.hide()
        }
        it.isBackToNorthOnClick = uiSettings.isCompassBackToNorthOnClickEnabled
    }

    scaleBar?.let {
        if (uiSettings.isScaleBarEnabled) {
            it.show()
        } else {
            it.hide()
        }
        it.isAutoHide = uiSettings.isScaleBarAutoHideEnabled
    }

    setGestureEnable(GestureType.Pan, uiSettings.isPanGestureEnabled)
    setGestureEnable(GestureType.Zoom, uiSettings.isZoomGestureEnabled)
    setGestureEnable(GestureType.RotateZoom, uiSettings.isZoomGestureEnabled || uiSettings.isRotateGestureEnabled)
    setGestureEnable(GestureType.Rotate, uiSettings.isRotateGestureEnabled)
    setGestureEnable(GestureType.Tilt, uiSettings.isTiltGestureEnabled)
    setGestureEnable(GestureType.OneFingerZoom, uiSettings.isOneFingerZoomGestureEnabled)
}

/**
 * 지도 오버레이 표시 상태를 SDK에 반영합니다.
 */
private fun KakaoMapSdk.setOverlayVisible(
    overlay: MapOverlay,
    visible: Boolean,
) {
    if (visible) {
        showOverlay(overlay)
    } else {
        hideOverlay(overlay)
    }
}
