package com.sonms.kakao.maps.open.map.compose.overlay.route

import com.sonms.kakao.maps.open.map.compose.model.KakaoLatLng
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 경로 구간 상의 누적 거리 배열(하버사인 공식)을 계산합니다.
 *
 * 반환값의 index 0은 항상 0.0이고, 마지막 index는 전체 경로 길이(m)입니다.
 */
internal fun computeCumulativeDistances(positions: List<KakaoLatLng>): DoubleArray {
    if (positions.isEmpty()) return doubleArrayOf()
    val cum = DoubleArray(positions.size)
    for (i in 1 until positions.size) {
        cum[i] = cum[i - 1] + haversine(positions[i - 1], positions[i])
    }
    return cum
}

/**
 * [progress](0.0~1.0) 시점까지의 경로 좌표 목록을 반환합니다.
 *
 * 이진 탐색으로 현재 구간을 찾고, 구간 내 선형 보간 좌표를 끝점으로 추가합니다.
 */
internal fun interpolateRoute(
    positions: List<KakaoLatLng>,
    progress: Float,
    cumulative: DoubleArray,
): List<KakaoLatLng> {
    if (positions.size < 2 || progress <= 0f) return emptyList()
    if (progress >= 1f) return positions
    val total = cumulative.last()
    if (total == 0.0) return positions
    val targetDist = total * progress

    // 이진 탐색: cumulative[lo] <= targetDist < cumulative[lo+1]
    var lo = 0
    var hi = cumulative.size - 1
    while (lo < hi - 1) {
        val mid = (lo + hi) ushr 1
        if (cumulative[mid] <= targetDist) lo = mid else hi = mid
    }
    val segIdx = lo.coerceIn(0, positions.size - 2)
    val segLen = cumulative[segIdx + 1] - cumulative[segIdx]
    val t = if (segLen > 0.0) {
        ((targetDist - cumulative[segIdx]) / segLen).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    val a = positions[segIdx]
    val b = positions[segIdx + 1]
    val tip = KakaoLatLng(
        latitude = a.latitude + t * (b.latitude - a.latitude),
        longitude = a.longitude + t * (b.longitude - a.longitude),
    )
    return positions.subList(0, segIdx + 1) + tip
}

/**
 * [poi]를 경로에 수직 투영하여 경로 전체 길이 대비 위치(0.0~1.0)를 반환합니다.
 *
 * 모든 구간에 대해 수직 투영점과 POI 사이의 거리를 비교하고, 가장 가까운 구간의 투영 위치를 사용합니다.
 * 위경도 평면 근사(Euclidean)를 사용하므로 수십 km 내의 경로에서 충분한 정확도를 제공합니다.
 */
internal fun computePoiFraction(
    poi: KakaoLatLng,
    positions: List<KakaoLatLng>,
    cumulative: DoubleArray,
): Float {
    if (positions.size < 2) return 0f
    val total = cumulative.last()
    if (total == 0.0) return 0f
    var minDistSq = Double.MAX_VALUE
    var bestDist = 0.0
    for (i in 0 until positions.size - 1) {
        val ax = positions[i].longitude
        val ay = positions[i].latitude
        val bx = positions[i + 1].longitude
        val by = positions[i + 1].latitude
        val abx = bx - ax
        val aby = by - ay
        val apx = poi.longitude - ax
        val apy = poi.latitude - ay
        val lenSq = abx * abx + aby * aby
        val t = if (lenSq > 0.0) ((apx * abx + apy * aby) / lenSq).coerceIn(0.0, 1.0) else 0.0
        val projX = ax + t * abx
        val projY = ay + t * aby
        val dx = poi.longitude - projX
        val dy = poi.latitude - projY
        val distSq = dx * dx + dy * dy
        if (distSq < minDistSq) {
            minDistSq = distSq
            bestDist = cumulative[i] + t * (cumulative[i + 1] - cumulative[i])
        }
    }
    return (bestDist / total).toFloat().coerceIn(0f, 1f)
}

private fun haversine(a: KakaoLatLng, b: KakaoLatLng): Double {
    val r = 6_371_000.0
    val dLat = Math.toRadians(b.latitude - a.latitude)
    val dLon = Math.toRadians(b.longitude - a.longitude)
    val lat1 = Math.toRadians(a.latitude)
    val lat2 = Math.toRadians(b.latitude)
    val sinDlat = sin(dLat / 2)
    val sinDlon = sin(dLon / 2)
    val h = sinDlat * sinDlat + cos(lat1) * cos(lat2) * sinDlon * sinDlon
    return 2.0 * r * asin(sqrt(h))
}
