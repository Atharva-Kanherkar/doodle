/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package golden

import cats.effect.unsafe.implicits.global
import doodle.core.format.Png
import doodle.java2d.*
import doodle.syntax.all.*
import munit.*

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

trait Golden { self: FunSuite =>
  val goldenDir = "golden/src/test/golden"

  def errorMessage(
      actual: BufferedImage,
      expected: BufferedImage,
      diff: Option[BufferedImage]
  ): String = {

    val actualH = actual.getHeight()
    val actualW = actual.getWidth()
    val expectedH = expected.getHeight()
    val expectedW = expected.getWidth()

    val (_, actualBase64) = actual.toPicture[Algebra].base64[Png]()
    val (_, expectedBase64) = expected.toPicture[Algebra].base64[Png]()

    val diffMessage =
      diff
        .map { d =>
          val (_, diffBase64) = d.toPicture[Algebra].base64[Png]()

          s"""
           | The diff image, PNG and Base 64 encoded is
           |
           | ${diffBase64.value}
           """.stripMargin
        }
        .getOrElse("")

    s"""
     | The dimensions (width x height) of the actual image are ${actualW} by ${actualH}
     | The dimensions (width x height) of the expected image are ${expectedW} by ${expectedH}
     |
     | The actual image, PNG and Base 64 encoded is
     |
     | ${actualBase64.value}
     |
     |
     | The expected image, PNG and Base 64 encoded is
     |
     | ${expectedBase64.value}
     |
     | ${diffMessage}
     |
     | To convert a base64 string to a viewable picture, use
     |
     | Base64[Png](string).toPicture[Algebra, Drawing]
     """.stripMargin
  }

  def pixelAbsoluteError(a: Int, b: Int): Int = {
    @scala.annotation.tailrec
    def calculateError(i: Int, acc: Int): Int = {
      if i >= 4 then acc
      else {
        val shift = i * 8
        val mask = 0x000000ff << shift
        val aValue = (a & mask) >> shift
        val bValue = (b & mask) >> shift

        val newError = acc + Math.abs(aValue - bValue)
        calculateError(i + 1, newError)
      }
    }

    calculateError(0, 0)
  }

  def absoluteError(
      actual: BufferedImage,
      golden: BufferedImage,
      width: Int,
      height: Int
  ): (Double, BufferedImage) = {
    val diff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    // Sum of squared error
    var error = 0.0

    var x = 0
    while x < width do {
      var y = 0
      while y < height do {
        val pixelError = pixelAbsoluteError(
          actual.getRGB(x, y),
          golden.getRGB(x, y)
        )
        // Convert pixelError to black and white value for easier rendering
        val err =
          (256 * ((pixelError.toDouble) / (Int.MaxValue.toDouble))).toInt
        val pixel = (err << 16) | (err << 8) | err
        diff.setRGB(x, y, pixel)

        error = error + pixelError

        y = y + 1
      }
      x = x + 1
    }

    (error, diff)
  }

  def imageDiff(file: File, temp: File)(implicit loc: Location): Unit = {
    val actual = ImageIO.read(temp)
    val expected = ImageIO.read(file)

    val actualH = actual.getHeight()
    val actualW = actual.getWidth()
    val expectedH = expected.getHeight()
    val expectedW = expected.getWidth()

    assert(
      math.abs(actualH - expectedH) <= 1 &&
        math.abs(actualW - expectedW) <= 1,
      errorMessage(actual, expected, None)
    )

    val height = actualH.min(expectedH)
    val width = actualW.min(expectedW)

    // Fairly arbitrary threshold allowing a 4-bit difference in each pixel
    val threshold = height * width * 4 * 16 * 16
    val (error, diff) = absoluteError(actual, expected, width, height)

    assert(
      clue(error) < clue(threshold),
      errorMessage(actual, expected, Some(diff))
    )
  }
}
