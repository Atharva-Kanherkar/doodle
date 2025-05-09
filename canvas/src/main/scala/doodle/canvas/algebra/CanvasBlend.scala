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
package canvas
package algebra

import doodle.algebra.Blend
import doodle.algebra.generic.Finalized
import doodle.algebra.generic.Renderable // Import Renderable object for apply method
// Remove unused imports
// import doodle.core.BoundingBox
// import doodle.core.Transform // Import Transform alias
import cats.Eval
import org.scalajs.dom

trait CanvasBlend extends Blend {
  self: CanvasAlgebra =>

  // Import the necessary functionality
  import self.Drawing

  private def blend[A](
      image: Drawing[A],
      blendMode: String
  ): Drawing[A] = {
    Finalized { transform => // transform: List[ContextTransform]
      image.run(transform).map { case (bb, rdr) => // rdr: Renderable[CanvasDrawing, A] == State[Transform, CanvasDrawing[A]]
        // Create a new Renderable that wraps the original
        val newRenderable: Renderable[CanvasDrawing, A] =
          Renderable { tx => // tx: Transform (This is the Transform the State receives)
            // Run the original Renderable with the Transform to get the Eval[CanvasDrawing[A]]
            val originalDrawingEval: Eval[CanvasDrawing[A]] = rdr.runA(tx) // Eval[CanvasRenderingContext2D => A]

            // Map over the Eval to create the new CanvasDrawing function
            originalDrawingEval.map { originalDrawing => // originalDrawing: CanvasRenderingContext2D => A
              // Explicitly construct a CanvasDrawing using the companion object's apply method
              canvas.algebra.CanvasDrawing.apply { (ctx: dom.CanvasRenderingContext2D) => // ctx: CanvasRenderingContext2D
                ctx.save()
                ctx.globalCompositeOperation = blendMode
                // Execute the original drawing function within the modified context
                val result: A = originalDrawing(ctx)
                ctx.restore()
                result
              }
            }
          }
        (bb, newRenderable)
      }
    }
  }

  // Define each blending operation using the helper
  def screen[A](image: Drawing[A]): Drawing[A] =
    blend(image, "screen")

  def burn[A](image: Drawing[A]): Drawing[A] =
    blend(image, "color-burn")

  def dodge[A](image: Drawing[A]): Drawing[A] =
    blend(image, "color-dodge")

  def lighten[A](image: Drawing[A]): Drawing[A] =
    blend(image, "lighten")

  def sourceOver[A](image: Drawing[A]): Drawing[A] =
    blend(image, "source-over")
}