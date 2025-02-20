import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.lzf.quickcheck.screens.home.YoloV5Ncnn

class Yolo(private val context: Context) {
    private val yolov5ncnn: YoloV5Ncnn = YoloV5Ncnn()
    private val colors = intArrayOf(
        Color.rgb(54, 67, 244),
        Color.rgb(99, 30, 233),
        Color.rgb(176, 39, 156),
        Color.rgb(183, 58, 103),
        Color.rgb(181, 81, 63),
        Color.rgb(243, 150, 33),
        Color.rgb(244, 169, 3),
        Color.rgb(212, 188, 0),
        Color.rgb(136, 150, 0),
        Color.rgb(80, 175, 76),
        Color.rgb(74, 195, 139),
        Color.rgb(57, 220, 205),
        Color.rgb(59, 235, 255),
        Color.rgb(7, 193, 255),
        Color.rgb(0, 152, 255),
        Color.rgb(34, 87, 255),
        Color.rgb(72, 85, 121),
        Color.rgb(158, 158, 158),
        Color.rgb(139, 125, 96)
    )

    //
    fun whereIsHuman(image: Bitmap): Array<YoloV5Ncnn.Obj?>? {
        val appContext = context.applicationContext
        val assetManager = appContext.assets
        yolov5ncnn.Init(assetManager) // 初始化 YOLOv5 模型
        val objects = yolov5ncnn.Detect(image, false) // 使用 CPU 进行推理
        return objects
    }

    @SuppressLint("DefaultLocale")
    fun onlyRect(image: Bitmap): Pair<Bitmap, Boolean> {


// 使用 ApplicationContext 以避免持有 Activity 引用
        val appContext = context.applicationContext
        val assetManager = appContext.assets
        yolov5ncnn.Init(assetManager) // 初始化 YOLOv5 模型
        val objects = yolov5ncnn.Detect(image, false) // 使用 CPU 进行推理
        var hasPerson = false
        // 创建一个可修改的 Bitmap 用于绘制边界框和标签
        val width = image.width
        val height = image.height

        val transparentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(transparentBitmap) // 创建一个画布对象，用于在 Bitmap 上绘制

        val paint = Paint() // 创建一个画笔对象，用于绘制边界框
        paint.style = Paint.Style.STROKE // 设置为描边样式
        paint.strokeWidth = 12f // 设置边框宽度
        val textbgpaint = Paint() // 创建一个画笔对象，用于绘制文字背景
        textbgpaint.color = Color.WHITE // 设置背景颜色为白色
        textbgpaint.style = Paint.Style.FILL // 设置为填充样式

        val textpaint = Paint() // 创建一个画笔对象，用于绘制文本
        textpaint.color = Color.BLACK // 设置文本颜色为黑色
        textpaint.textSize = 104f // 设置文本大小为26
        textpaint.textAlign = Paint.Align.LEFT // 设置文本对齐方式为左对齐

        if (objects != null) {
            for (i in objects.indices)  // 遍历检测到的所有对象
            {

                if (objects[i]?.label == "person") {
                    hasPerson = true
                    paint.color = colors[i % 19] // 设置边界框的颜色

                    // 绘制边界框
                    objects[i]?.let {
                        canvas.drawRect(
                            it.x,
                            it.y,
                            it.x + it.w,
                            it.y + it.h,
                            paint,
                        );

                    }
                    // 绘制文本背景
                    run {
                        val text = objects[i]?.label + " = " + String.format(
                            "%.1f",
                            (objects[i]?.prob ?: 0) as Float * 100
                        ) + "%" // 创建文本内容
                        val text_width = textpaint.measureText(text) // 计算文本的宽度
                        val text_height = -textpaint.ascent() + textpaint.descent() // 计算文本的高度
                        var x = objects[i]?.x // 获取文本的 x 坐标
                        var y = (objects[i]?.y ?: 0) as Float - text_height // 获取文本的 y 坐标
                        if (y < 0) // 防止文本超出图像的顶部
                            y = 0f
                        if (x != null) {
                            if (x + text_width > transparentBitmap.width) // 防止文本超出图像的右边
                                x = transparentBitmap.width - text_width
                        }
                        // 绘制文本背景
                        x?.let {
                            canvas.drawRect(
                                it,
                                y,
                                x + text_width,
                                y + text_height,
                                textbgpaint
                            )
                        }
                        // 绘制文本
                        x?.let { canvas.drawText(text, it, y - textpaint.ascent(), textpaint) }
                    }
                }
            }
        }
        return Pair(transparentBitmap, hasPerson)

    }

    // 模拟处理图片并识别人脸的函数
    @SuppressLint("DefaultLocale", "RestrictedApi")
    fun processImage(image: Bitmap): Pair<Bitmap, Boolean> {


// 使用 ApplicationContext 以避免持有 Activity 引用
        val appContext = context.applicationContext
        val assetManager = appContext.assets
        yolov5ncnn.Init(assetManager) // 初始化 YOLOv5 模型
        val objects = yolov5ncnn.Detect(image, false) // 使用 CPU 进行推理
        var hasPerson = false
        // 创建一个可修改的 Bitmap 用于绘制边界框和标签
        val rgba: Bitmap = image.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(rgba) // 创建一个画布对象，用于在 Bitmap 上绘制

        val paint = Paint() // 创建一个画笔对象，用于绘制边界框
        paint.style = Paint.Style.STROKE // 设置为描边样式
        paint.strokeWidth = 12f // 设置边框宽度
        val textbgpaint = Paint() // 创建一个画笔对象，用于绘制文字背景
        textbgpaint.color = Color.WHITE // 设置背景颜色为白色
        textbgpaint.style = Paint.Style.FILL // 设置为填充样式

        val textpaint = Paint() // 创建一个画笔对象，用于绘制文本
        textpaint.color = Color.BLACK // 设置文本颜色为黑色
        textpaint.textSize = 104f // 设置文本大小为26
        textpaint.textAlign = Paint.Align.LEFT // 设置文本对齐方式为左对齐

        if (objects != null) {
            for (i in objects.indices)  // 遍历检测到的所有对象
            {

                if (objects[i]?.label == "person") {
                    hasPerson = true
                    paint.color = colors[i % 19] // 设置边界框的颜色

                    // 绘制边界框
                    objects[i]?.let {
                        canvas.drawRect(
                            it.x,
                            it.y,
                            it.x + it.w,
                            it.y + it.h,
                            paint,
                        );

                    }
                    // 绘制文本背景
                    run {
                        val text = objects[i]?.label + " = " + String.format(
                            "%.1f",
                            (objects[i]?.prob ?: 0) as Float * 100
                        ) + "%" // 创建文本内容
                        val text_width = textpaint.measureText(text) // 计算文本的宽度
                        val text_height = -textpaint.ascent() + textpaint.descent() // 计算文本的高度
                        var x = objects[i]?.x // 获取文本的 x 坐标
                        var y = (objects[i]?.y ?: 0) as Float - text_height // 获取文本的 y 坐标
                        if (y < 0) // 防止文本超出图像的顶部
                            y = 0f
                        if (x != null) {
                            if (x + text_width > rgba.width) // 防止文本超出图像的右边
                                x = rgba.width - text_width
                        }
                        // 绘制文本背景
                        x?.let {
                            canvas.drawRect(
                                it,
                                y,
                                x + text_width,
                                y + text_height,
                                textbgpaint
                            )
                        }
                        // 绘制文本
                        x?.let { canvas.drawText(text, it, y - textpaint.ascent(), textpaint) }
                    }
                }
            }
        }
        return Pair(rgba, hasPerson)
    }
}