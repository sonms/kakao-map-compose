package com.sonms.kakao.maps.open.map.compose.cluster

import com.sonms.kakao.maps.open.map.compose.model.KakaoCluster
import com.sonms.kakao.maps.open.map.compose.model.KakaoClusterItem
import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import kotlin.math.ln
import kotlin.math.sin

/**
 * 격자(grid) 기반 클러스터링을 수행합니다.
 *
 * 각 항목을 웹 메르카토르 세계 픽셀로 변환한 뒤, [cellSizePx] 크기의 격자 셀에 할당합니다.
 * 같은 셀에 속한 항목들이 하나의 클러스터를 형성하며, 클러스터 중심은 항목들의 위도/경도 무게중심입니다.
 *
 * 시간 복잡도: O(n) — HashMap을 사용한 단일 순회.
 */
internal fun gridCluster(
    items: List<KakaoClusterItem>,
    zoomLevel: Int,
    cellSizePx: Float,
): List<KakaoCluster> {
    if (items.isEmpty()) return emptyList()
    // 2^zoom * 256 = 줌 레벨에서의 전체 세계 너비(px). coerceIn으로 Long 오버플로 방지.
    val worldScale = 256.0 * (1L shl zoomLevel.coerceIn(0, 23)).toDouble()
    val grid = HashMap<Long, MutableList<KakaoClusterItem>>(items.size * 2)

    for (item in items) {
        val (wx, wy) = mercatorWorldPixels(item.position, worldScale)
        val cellX = (wx / cellSizePx).toLong()
        val cellY = (wy / cellSizePx).toLong()
        // 두 좌표를 Long 하나로 인코딩 (각각 32비트 범위 이내)
        val key = (cellX shl 32) or (cellY and 0xFFFFFFFFL)
        grid.getOrPut(key) { mutableListOf() }.add(item)
    }

    return grid.values.map { cellItems ->
        val lat = cellItems.sumOf { it.position.latitude } / cellItems.size
        val lng = cellItems.sumOf { it.position.longitude } / cellItems.size
        KakaoCluster(center = KakaoLatLng(lat, lng), items = cellItems)
    }
}

/** 위도/경도를 웹 메르카토르 세계 픽셀 좌표로 변환합니다. */
private fun mercatorWorldPixels(pos: KakaoLatLng, worldScale: Double): Pair<Double, Double> {
    val x = worldScale * (pos.longitude + 180.0) / 360.0
    val sinLat = sin(Math.toRadians(pos.latitude.coerceIn(-85.051129, 85.051129)))
    val y = worldScale * (0.5 - ln((1.0 + sinLat) / (1.0 - sinLat)) / (4 * Math.PI))
    return x to y
}
