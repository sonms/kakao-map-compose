package com.sonms.kakao.maps.open.map.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.label.Label

/**
 * [KakaoMap] Composable이 생성한 카카오 지도 SDK 객체와 오버레이 이벤트 상태를 보관합니다.
 */
@Stable
class KakaoMapState {
    /**
     * 준비된 카카오 지도 SDK 객체입니다.
     *
     * 지도가 아직 준비되지 않았거나 종료된 상태에서는 null입니다.
     */
    var map: KakaoMap? by mutableStateOf(null)
        internal set

    private val labelClickHandlers = mutableMapOf<Label, (Label) -> Boolean>()

    /**
     * 특정 [Label]에 대한 클릭 콜백을 등록합니다.
     *
     * @param label 클릭 콜백을 연결할 SDK Label입니다.
     * @param handler 클릭 이벤트를 처리할 콜백입니다.
     */
    internal fun registerLabelClickHandler(label: Label, handler: (Label) -> Boolean) {
        labelClickHandlers[label] = handler
    }

    /**
     * 특정 [Label]에 등록된 클릭 콜백을 제거합니다.
     *
     * @param label 클릭 콜백을 제거할 SDK Label입니다.
     */
    internal fun unregisterLabelClickHandler(label: Label) {
        labelClickHandlers.remove(label)
    }

    /**
     * SDK에서 전달된 클릭 이벤트를 개별 [Label] 콜백으로 전달합니다.
     *
     * @param label 클릭된 SDK Label입니다.
     * @return 이벤트가 소비되었으면 true, 아니면 false입니다.
     */
    internal fun dispatchLabelClick(label: Label): Boolean =
        labelClickHandlers[label]?.invoke(label) ?: false

    /**
     * 지도 종료 시 남아 있는 모든 Label 클릭 콜백을 제거합니다.
     */
    internal fun clearLabelClickHandlers() {
        labelClickHandlers.clear()
    }
}

/**
 * Compose 재구성 사이에 유지되는 [KakaoMapState]를 생성합니다.
 */
@Composable
fun rememberKakaoMapState(): KakaoMapState = remember { KakaoMapState() }
