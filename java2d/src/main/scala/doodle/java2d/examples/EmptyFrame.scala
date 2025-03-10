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

package doodle.java2d.examples

import cats.effect.*
import doodle.core.*
import doodle.java2d.*
import doodle.java2d.effect.Frame
import doodle.syntax.all.*

// This just renders an empty frame, so we can check it renders correctly.
//
// See issue #179
object EmptyFrame extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val frame = Frame.default
      .withSize(width = 300, height = 300)
      .withBackground(Color.midnightBlue)
    frame
      .canvas()
      .use { canvas =>
        canvas.closed >> IO.pure(ExitCode.Success)
      }
  }
}
