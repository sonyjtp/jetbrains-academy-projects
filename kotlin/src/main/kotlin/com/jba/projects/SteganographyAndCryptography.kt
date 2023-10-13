package cryptography

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

fun main() {
    var input: String
    while (true) {
        println("Task (hide, show, exit):")
        input = readln()
        try {
            if (!handleInput(Input.valueOf(input.uppercase()))) break
        } catch (e: IllegalArgumentException) {
            println("Wrong task: $input")
        }
    }

}

private fun handleInput(input: Input): Boolean = when (input) {
    Input.HIDE -> {
        hide()
        true
    }

    Input.SHOW -> {
        println("Obtaining message from image.")
        true
    }

    Input.EXIT -> {
        println("Bye!")
        false
    }
}

private fun hide() {
    try {
        println("Input image file:")
        val inputPath = File(readln())
        println("Output image file:")
        val outputPath = File(readln())
        write(read(inputPath), outputPath)
        println("Input Image: ${inputPath.invariantSeparatorsPath}")
        println("Output Image: ${outputPath.invariantSeparatorsPath}")
        println("Image ${outputPath.invariantSeparatorsPath} is saved")
    } catch (e: IOException) {
        println(e.message)
    }
}

private fun read(inputPath: File) = ImageIO.read(inputPath) ?: throw IOException("Can't read input file!")


private fun write(image: BufferedImage, outputPath: File) {
    try {
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                image.setRGB(i, j, image.getRGB(i, j) or 0x010101)
            }
        }
        ImageIO.write(image, "png", outputPath)
    } catch (e: Exception) {
        throw IOException("Can't write to output file!")
    }
}


private enum class Input { HIDE, SHOW, EXIT }

