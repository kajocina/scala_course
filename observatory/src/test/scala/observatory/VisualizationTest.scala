package observatory


import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

trait VisualizationTest extends FunSuite with Checkers {
  import Visualization._
  test("Color extrapolation"){
    val colors = Array((10.0,Color(50,50,50)),(30.0,Color(150,150,150)))
    assert(interpolateColor(colors,20) == Color(100,100,100))
  }
}
