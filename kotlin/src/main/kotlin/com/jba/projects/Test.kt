package com.jba.projects

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

fun main() {

    val imageFile = File("D:\\IdeaProjects\\jetbrains-academy-kotlin-developer\\img\\test.png")
    val outputFile = File("D:\\IdeaProjects\\jetbrains-academy-kotlin-developer\\img\\output.png")
    val bufferedImage = ImageIO.read(imageFile)
    for (x in 0 until bufferedImage.width)
        for (y in 0 until bufferedImage.height) {
//            val color = Color(bufferedImage.getRGB(x, y))
            if (bufferedImage.getRGB(x, y) > Color(100, 100, 100).rgb)
                bufferedImage.setRGB(x, y, Color(0, 0, 0).rgb)
//            println("($x, $y) -> (${color.red}, ${color.green}, ${color.blue})")
        }
    ImageIO.write(bufferedImage, "png", outputFile)

}


class SomeCollection<T>(val list: List<T>) {
    fun invert() {
        "[${this.list.reversed().joinToString(", ")}]"
    }
}