package com.sonms.kakao.maps.open.map.compose.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.SideEffect
import com.kakao.vectormap.KakaoMap
import com.sonms.kakao.maps.open.map.compose.core.KakaoMapComposable
import com.sonms.kakao.maps.open.map.compose.core.LocalKakaoMapState

/**
 * 준비된 SDK [KakaoMap] 객체에 직접 접근하는 Composable effect입니다.
 *
 * 아직 Compose 래퍼로 제공하지 않는 SDK 기능을 임시로 사용하거나, 외부 SDK 연동을 붙일 때
 * escape hatch로 사용합니다. 정리 작업이 필요한 SDK 객체를 만들 때는 [DisposableKakaoMapEffect]를
 * 사용해야 합니다.
 *
 * @param effect 지도가 준비된 뒤 실행할 작업입니다.
 */
@Composable
@KakaoMapComposable
fun MapEffect(
    effect: (KakaoMap) -> Unit,
) {
    val map = LocalKakaoMapState.current?.map ?: return
    SideEffect {
        effect(map)
    }
}

/**
 * 준비된 SDK [KakaoMap] 객체에 직접 접근하고, composition에서 빠질 때 정리할 수 있는 effect입니다.
 *
 * @param keys effect 재시작 기준이 되는 키입니다.
 * @param effect SDK 지도 객체를 받아 작업하고 [DisposableEffectScope.onDispose]를 반환하는 블록입니다.
 */
@Composable
@KakaoMapComposable
fun DisposableKakaoMapEffect(
    vararg keys: Any?,
    effect: DisposableEffectScope.(KakaoMap) -> DisposableEffectResult,
) {
    val map = LocalKakaoMapState.current?.map ?: return
    DisposableEffect(map, *keys) {
        effect(map)
    }
}
