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

package doodle.examples

import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

/** All the examples from the Style documentation page, written in a backend
  * independent style.
  */
trait StyleExamples[Alg <: Debug & Layout & Path & Shape & Style]
    extends BaseExamples[Alg] {
  val basicStyle =
    circle(100)
      .beside(
        circle(100)
          .strokeColor(Color.purple)
          .strokeWidth(5.0)
          .fillColor(Color.lavender)
      )

  val strokeStyle =
    star(5, 50, 25)
      .strokeWidth(5.0)
      .strokeColor(Color.limeGreen)
      .strokeJoin(Join.bevel)
      .strokeCap(Cap.round)
      .strokeDash(Array(9.0, 7.0))
      .beside(
        star(5, 50, 25)
          .strokeWidth(5.0)
          .strokeColor(Color.greenYellow)
          .strokeJoin(Join.miter)
          .strokeCap(Cap.square)
          .strokeDash(Array(12.0, 9.0))
      )
      .beside(
        star(5, 50, 25)
          .strokeWidth(5.0)
          .strokeGradient(
            Gradient.dichromaticVertical(Color.crimson, Color.midnightBlue, 50)
          )
          .strokeJoin(Join.round)
          .strokeCap(Cap.butt)
          .strokeDash(Array(15.0, 5.0))
      )

  val fillStyle =
    square(100)
      .fillGradient(
        Gradient.linear(
          Point(-50, 0),
          Point(50, 0),
          List((Color.red, 0.0), (Color.yellow, 1.0)),
          Gradient.CycleMethod.repeat
        )
      )
      .strokeWidth(5.0)
      .margin(0.0, 5.0, 0.0, 0.0)
      .beside(
        circle(100)
          .fillGradient(
            Gradient.dichromaticRadial(Color.limeGreen, Color.darkBlue, 50)
          )
          .strokeWidth(5.0)
      )

  val strokeGradientStyle =
    square(100)
      .strokeGradient(
        Gradient.linear(
          Point(-50, -50),
          Point(50, 50),
          List(
            (Color.crimson, 0.0),
            (Color.gold, 0.5),
            (Color.deepSkyBlue, 1.0)
          ),
          Gradient.CycleMethod.repeat
        )
      )
      .strokeWidth(10.0)
      .noFill
      .margin(0.0, 5.0, 0.0, 0.0)
      .beside(
        circle(100)
          .strokeGradient(
            Gradient.radial(
              Point(0, 0),
              Point(0, 0),
              50,
              List((Color.magenta, 0.0), (Color.cyan, 1.0)),
              Gradient.CycleMethod.noCycle
            )
          )
          .strokeWidth(15.0)
          .noFill
      )

  //
  // If you add a new example, also add it in here
  val allPictures =
    List(
      basicStyle,
      strokeStyle,
      fillStyle,
      strokeGradientStyle
    )

}
