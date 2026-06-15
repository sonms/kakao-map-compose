package com.sonms.kakao.maps.open.map.compose.cluster

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * 클러스터 배지 비트맵을 생성하는 유틸리티 객체입니다.
 *
 * 반환된 [Bitmap]은 [com.sonms.kakao.maps.open.map.compose.model.KakaoOverlayImage.FromBitmap]으로
 * 감싸서 [com.sonms.kakao.maps.open.map.compose.overlay.label.KakaoPoi]의 `icon` 파라미터에
 * 전달합니다.
 *
 * ```kotlin
 * val badge = remember(cluster.size) { KakaoClusterBadge.create(cluster.size) }
 * KakaoPoi(
 *     position = cluster.center,
 *     icon = KakaoOverlayImage.FromBitmap(badge),
 *     iconSize = DpSize(48.dp, 48.dp),
 * )
 * ```
 */
object KakaoClusterBadge {

    /**
     * 원형 배지 비트맵을 생성합니다.
     *
     * @param count 클러스터에 포함된 항목 수입니다. 99 초과면 "99+"로 표시됩니다.
     * @param sizePx 비트맵의 픽셀 크기입니다. 표시 크기는 [KakaoPoi]의 `iconSize`로 제어합니다.
     * @param backgroundColor 원형 배경색입니다.
     * @param textColor 숫자 텍스트 색상입니다.
     */
    fun create(
        count: Int,
        sizePx: Int = 120,
        backgroundColor: Color = Color(0xFF3478F6),
        textColor: Color = Color.White,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val r = sizePx / 2f

        paint.color = backgroundColor.toArgb()
        canvas.drawCircle(r, r, r, paint)

        paint.color = textColor.toArgb()
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = sizePx * 0.33f
        paint.typeface = Typeface.DEFAULT_BOLD

        val label = if (count > 99) "99+" else count.toString()
        canvas.drawText(label, r, r - (paint.descent() + paint.ascent()) / 2f, paint)

        return bitmap
    }
}
