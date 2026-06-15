package com.sonms.kakao.maps.open.map.compose.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdate
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng

/**
 * 카카오 지도의 카메라 위치와 이동 상태를 관리하는 Compose 상태 홀더입니다.
 *
 * - [position]은 SDK 카메라 이동 이벤트가 끝날 때 자동으로 갱신됩니다.
 * - [move]와 [animateMove]로 프로그래매틱 카메라 이동을 요청할 수 있습니다.
 * - 같은 프레임 내에 여러 번 요청해도 내부 큐에 순서대로 누적되며, 다음 렌더 프레임에 일괄 처리됩니다.
 * - [com.sonms.kakao.maps.open.map.compose.core.KakaoMap] composable이 큐를 소비하여 실제 SDK 호출을 수행합니다.
 *
 * @param position 초기 카메라 위치입니다.
 */
@Stable
class KakaoCameraPositionState(
    position: KakaoCameraPosition = KakaoCameraPosition.Companion.Default,
) {
    /**
     * 현재 카메라 위치입니다.
     *
     * SDK 카메라 이동이 끝날 때 자동으로 갱신되며, 외부에서 직접 변경할 수 없습니다.
     */
    var position: KakaoCameraPosition by mutableStateOf(position)
        internal set

    /**
     * 카메라가 이동 중인지 여부입니다.
     *
     * SDK 카메라 이동 시작/종료 이벤트에 따라 자동으로 갱신됩니다.
     */
    var isMoving: Boolean by mutableStateOf(false)
        internal set

    // 같은 프레임 내 여러 번 호출해도 큐에 누적되므로 앞선 요청이 유실되지 않습니다.
    // MAX_PENDING_MOVES를 초과하면 가장 오래된 요청을 버려 메모리 무한 증가를 방지합니다.
    private val _pendingMoves: ArrayDeque<Pair<CameraUpdate, CameraAnimation?>> = ArrayDeque()

    // SideEffect 재실행 트리거. 큐에 항목을 추가할 때마다 증가시켜 재컴포지션을 유발합니다.
    internal var pendingMoveVersion: Int by mutableStateOf(0)
        private set

    internal fun drainPendingMoves(): List<Pair<CameraUpdate, CameraAnimation?>> {
        val snapshot = _pendingMoves.toList()
        _pendingMoves.clear()
        return snapshot
    }

    /**
     * 애니메이션 없이 카메라를 즉시 이동합니다.
     *
     * @param update 이동할 카메라 업데이트입니다. [com.kakao.vectormap.camera.CameraUpdateFactory]로 생성합니다.
     */
    fun move(update: CameraUpdate) {
        if (_pendingMoves.size >= MAX_PENDING_MOVES) _pendingMoves.removeFirst()
        _pendingMoves.addLast(update to null)
        pendingMoveVersion++
    }

    /**
     * 애니메이션과 함께 카메라를 이동합니다.
     *
     * @param update 이동할 카메라 업데이트입니다. [com.kakao.vectormap.camera.CameraUpdateFactory]로 생성합니다.
     * @param animation 적용할 카메라 애니메이션입니다. 기본값은 300ms입니다.
     */
    fun animateMove(
        update: CameraUpdate,
        animation: CameraAnimation = CameraAnimation.from(300),
    ) {
        if (_pendingMoves.size >= MAX_PENDING_MOVES) _pendingMoves.removeFirst()
        _pendingMoves.addLast(update to animation)
        pendingMoveVersion++
    }

    /**
     * 지정한 시간의 기본 애니메이션과 함께 카메라를 이동합니다.
     *
     * @param update 이동할 카메라 업데이트입니다.
     * @param durationMs 애니메이션 시간(ms)입니다.
     */
    fun animate(
        update: CameraUpdate,
        durationMs: Int = 300,
    ) {
        animateMove(update = update, animation = CameraAnimation.from(durationMs))
    }

    companion object {
        private const val MAX_PENDING_MOVES = 16

        val Saver: Saver<KakaoCameraPositionState, *> = listSaver(
            save = {
                listOf(
                    it.position.target.latitude,
                    it.position.target.longitude,
                    it.position.zoomLevel,
                    it.position.tiltAngle,
                    it.position.rotationAngle,
                )
            },
            restore = {
                KakaoCameraPositionState(
                    position = KakaoCameraPosition(
                        target = KakaoLatLng(it[0] as Double, it[1] as Double),
                        zoomLevel = it[2] as Int,
                        tiltAngle = it[3] as Double,
                        rotationAngle = it[4] as Double,
                    ),
                )
            },
        )
    }
}

/**
 * 화면 회전 시 카메라 위치를 복원하는 [KakaoCameraPositionState]를 생성합니다.
 *
 * @param init 초기 상태를 설정하는 람다입니다.
 */
@Composable
fun rememberKakaoCameraPositionState(
    init: KakaoCameraPositionState.() -> Unit = {},
): KakaoCameraPositionState = rememberSaveable(saver = KakaoCameraPositionState.Saver) {
    KakaoCameraPositionState().apply(init)
}
