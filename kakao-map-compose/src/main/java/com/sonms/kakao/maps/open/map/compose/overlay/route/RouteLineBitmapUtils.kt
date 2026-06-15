package com.sonms.kakao.maps.open.map.compose.overlay.route

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kotlin.math.ceil
import kotlin.math.sqrt

internal fun createDotBitmap(color: Int, sizePx: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
    canvas.drawOval(RectF(0f, 0f, sizePx.toFloat(), sizePx.toFloat()), paint)
    return bitmap
}

/**
 * 위쪽(y=0)이 팁, 아래쪽(y=[heightPx])이 기저부인 이등변삼각형 비트맵을 생성합니다.
 * [rotateArrowBitmap]으로 경로 방향에 맞게 회전한 뒤 라벨 아이콘으로 사용합니다.
 */
internal fun createArrowBitmap(color: Int, widthPx: Int, heightPx: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.FILL
    }
    val path = Path().apply {
        moveTo(widthPx / 2f, 0f)
        lineTo(widthPx.toFloat(), heightPx.toFloat())
        lineTo(0f, heightPx.toFloat())
        close()
    }
    canvas.drawPath(path, paint)
    return bitmap
}

/**
 * [bitmap]을 [bearingDegrees]만큼 시계 방향으로 회전한 새 비트맵을 반환합니다.
 *
 * 원본 비트맵을 대각선 크기의 정사각형 중앙에 배치한 뒤 중심 기준으로 회전하므로
 * 어떤 각도에서도 콘텐츠가 잘리지 않습니다.
 * 반환된 비트맵의 중심이 원본 화살표의 기하학적 중심(팁-기저부 중점)과 일치합니다.
 */
internal fun rotateArrowBitmap(bitmap: Bitmap, bearingDegrees: Float): Bitmap {
    val diagonal = ceil(sqrt((bitmap.width * bitmap.width + bitmap.height * bitmap.height).toDouble())).toInt()
    val result = Bitmap.createBitmap(diagonal, diagonal, Bitmap.Config.ARGB_8888)
    val cx = diagonal / 2f
    val cy = diagonal / 2f
    val matrix = Matrix().apply { postRotate(bearingDegrees, cx, cy) }
    val canvas = Canvas(result)
    canvas.save()
    canvas.concat(matrix)
    canvas.drawBitmap(bitmap, cx - bitmap.width / 2f, cy - bitmap.height / 2f, null)
    canvas.restore()
    return result
}
