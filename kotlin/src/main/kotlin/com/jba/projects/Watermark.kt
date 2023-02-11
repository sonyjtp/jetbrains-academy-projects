package com.jba.projects

import java.awt.Color
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.io.File
import javax.imageio.ImageIO

private const val TRANSPARENCY_MAX = 100
private val allowedPixelSizes = listOf(24, 32)
private val colorRange = (0..255)
private val wmPositionMethods = listOf("single", "grid")

fun main() {
    try {
        val outputFileName = buildBlender().blend()
        println("The watermarked image $outputFileName has been created.")
    } catch (ive: ImageValidationException) {
        println(ive.message)
    }
}

private fun buildBlender(): Blender {
    val blender = Blender()
    println("Input the image filename:")
    blender.srcBfr = getImageBuffer(FileType.SOURCE)
    println("Input the watermark image filename:")
    blender.wmBfr = getImageBuffer(FileType.WATERMARK)
    compareDimensions(blender.srcBfr, blender.wmBfr)
    if (blender.wmBfr.transparency == Transparency.TRANSLUCENT) handleAlpha(blender) else handleNonAlpha(blender)
    blender.weight = getTransparencyWeight()
    setWmPositionProps(blender)
    println("Input the output image filename (jpg or png extension):")
    blender.outFileName = getOutputFileName(readln())
    return blender
}

private fun handleAlpha(blender: Blender) {
    println("Do you want to use the watermark's Alpha channel?")
    blender.wmAlpha = readln().equals("yes", true)
}

private fun handleNonAlpha(blender: Blender) {
    println("Do you want to set a transparency color?")
    if (readln().equals("yes", ignoreCase = true)) {
        println("Input a transparency color ([Red] [Green] [Blue]):")
        val rgb = readln().split(" ")
        if (isValidTransparency(rgb)) {
            blender.transparencyConfig = TransparencyConfig(rgb[0].toInt(), rgb[1].toInt(), rgb[2].toInt())
        } else throw ImageValidationException(ErrorMessage.ERR_TRANSPARENCY.message)
    }
}

private fun isValidTransparency(rgb: List<String>) = rgb.size == 3 && rgb[0].toInt() in colorRange
        && rgb[1].toInt() in colorRange && rgb[2].toInt() in colorRange

private fun getImageBuffer(fileType: FileType): BufferedImage {
    val filename = readln()
    doesFileExist(filename)
    val image = Image(File(filename))
    val validationMessage = validateImage(image, fileType)
    if (validationMessage.isNotEmpty()) throw ImageValidationException(validationMessage)
    return image.bufferedImage
}

private fun doesFileExist(fileName: String) {
    if (fileName.isEmpty() || !File(fileName).exists())
        throw ImageValidationException("The file $fileName doesn't exist.")
}

private fun validateImage(image: Image, fileType: FileType): String {
    val colorModel = image.bufferedImage.colorModel
    return when (fileType) {
        FileType.SOURCE -> when {
            colorModel.numColorComponents != 3 -> ErrorMessage.ERR_SRC_COLOR_COMPONENT.message
            colorModel.pixelSize !in allowedPixelSizes -> ErrorMessage.ERR_SRC_BIT_SIZE.message
            else -> ""
        }

        FileType.WATERMARK -> when {
            colorModel.numColorComponents != 3 -> ErrorMessage.ERR_WM_COLOR_COMPONENT.message
            colorModel.pixelSize !in allowedPixelSizes -> ErrorMessage.ERR_WM_BIT_SIZE.message
            else -> ""
        }
    }
}

private fun compareDimensions(srcBfr: BufferedImage, wmBfr: BufferedImage) {
    if (srcBfr.height < wmBfr.height || srcBfr.width < wmBfr.width)
        throw ImageValidationException(ErrorMessage.ERR_LARGER_WATERMARK_FILE.message)
}

private fun getTransparencyWeight(): Int {
    println("Input the watermark transparency percentage (Integer 0-100):")
    return readln().toIntOrNull()?.let {
        if (it !in (0..TRANSPARENCY_MAX))
            throw ImageValidationException(ErrorMessage.ERR_TRANSPARENCY_THRESHOLD.message)
        it
    } ?: run { throw ImageValidationException(ErrorMessage.ERR_TRANSPARENCY_DATATYPE.message) }
}

private fun setWmPositionProps(blender: Blender) {
    println("Choose the position method (single, grid):")
    blender.wmMethod = readln().let { method ->
        if (method !in wmPositionMethods)
            throw ImageValidationException(ErrorMessage.ERR_WATERMARK_POSITION_METHOD.message)
        if (method.equals("single", ignoreCase = true)) {
            val dX = blender.srcBfr.width - blender.wmBfr.width
            val dY = blender.srcBfr.height - blender.wmBfr.height
            println("Input the watermark position ([x 0-$dX] [y 0-$dY]):")
            readln().split(" ").let { position ->
                if (position.size != 2 || position[0].toIntOrNull() == null || position[1].toIntOrNull() == null)
                    throw ImageValidationException(ErrorMessage.ERR_WATERMARK_POSITION.message)
                if (position[0].toInt() !in (0..dX) || position[1].toInt() !in (0..dY))
                    throw ImageValidationException(ErrorMessage.ERR_WATERMARK_POSITION_OUT_OF_RANGE.message)
                blender.wmPos = Pair(position[0].toInt(), position[1].toInt())
            }
        }
        method
    }
}

private fun getOutputFileName(filename: String): String {
    if (!filename.endsWith(".jpg") && !filename.endsWith(".png"))
        throw ImageValidationException(ErrorMessage.ERR_OUTPUT_FILE_EXTN.message)
    return filename
}

data class Image(private val file: File, var bufferedImage: BufferedImage = ImageIO.read(file))

class Blender {
    lateinit var srcBfr: BufferedImage
    lateinit var wmBfr: BufferedImage
    lateinit var outFileName: String
    var transparencyConfig: TransparencyConfig? = null
    lateinit var wmMethod: String
    lateinit var wmPos: Pair<Int, Int>
    var weight = -1
    var wmAlpha = false

    fun blend(): String {
        val outputBuffer = BufferedImage(this.srcBfr.width, this.srcBfr.height, TYPE_INT_RGB)
        for (x in 0 until this.srcBfr.width) {
            for (y in 0 until this.srcBfr.height) {
                outputBuffer.setRGB(x, y, setOutputRgb(x, y))
            }
        }
        val outFile = File(this.outFileName)
        outFile.createNewFile()
        ImageIO.write(outputBuffer, outFile.extension, outFile)
        return outFileName
    }

    private fun setOutputRgb(x: Int, y: Int): Int {
        val srcColor = Color(this.srcBfr.getRGB(x, y))
        val wmColor = when {
            wmMethod == "grid" -> getGridColor(x, y)
            x in (wmPos.first until wmPos.first + wmBfr.width)
                    && y in (wmPos.second until wmPos.second + wmBfr.height)
            -> Color(this.wmBfr.getRGB(x - wmPos.first, y - wmPos.second), wmAlpha)

            else -> srcColor
        }
        val outputColor = when {
            wmColor.alpha == 0
                    || transparencyConfig != null && isTransparent(wmColor)
            -> srcColor

            else -> blendPixel(srcColor, wmColor)
        }
        return outputColor.rgb
    }

    private fun getGridColor(x: Int, y: Int): Color {
        val newX = x % this.wmBfr.width
        val newY = y % this.wmBfr.height
        return Color(this.wmBfr.getRGB(newX, newY), wmAlpha)
    }

    private fun isTransparent(wColor: Color) = wColor.red == this.transparencyConfig!!.red
            && wColor.green == this.transparencyConfig!!.green
            && wColor.blue == this.transparencyConfig!!.blue

    private fun blendPixel(sColor: Color, wColor: Color) = Color(
        mixColor(sColor.red, wColor.red),
        mixColor(sColor.green, wColor.green),
        mixColor(sColor.blue, wColor.blue)
    )

    private fun mixColor(sColorComp: Int, wColorComp: Int): Int {
        return (this.weight * wColorComp + (TRANSPARENCY_MAX - this.weight) * sColorComp) / TRANSPARENCY_MAX
    }
}

data class TransparencyConfig(val red: Int, val green: Int, val blue: Int)

class ImageValidationException(errorMessage: String) : Exception(errorMessage)

enum class FileType { SOURCE, WATERMARK }

enum class ErrorMessage(val message: String) {
    ERR_SRC_COLOR_COMPONENT("The number of image color components isn't 3."),
    ERR_WM_COLOR_COMPONENT("The number of watermark color components isn't 3."),
    ERR_SRC_BIT_SIZE("The image isn't 24 or 32-bit."),
    ERR_WM_BIT_SIZE("The watermark isn't 24 or 32-bit."),
    ERR_LARGER_WATERMARK_FILE("The watermark's dimensions are larger."),
    ERR_TRANSPARENCY_DATATYPE("The transparency percentage isn't an integer number."),
    ERR_TRANSPARENCY_THRESHOLD("The transparency percentage is out of range."),
    ERR_OUTPUT_FILE_EXTN("The output file extension isn't \"jpg\" or \"png\"."),
    ERR_TRANSPARENCY("The transparency color input is invalid."),
    ERR_WATERMARK_POSITION_METHOD("The position method input is invalid."),
    ERR_WATERMARK_POSITION("The position input is invalid."),
    ERR_WATERMARK_POSITION_OUT_OF_RANGE("The position input is out of range.")
    ;
}
