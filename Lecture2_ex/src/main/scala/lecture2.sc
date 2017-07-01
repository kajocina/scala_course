/**
  * Created by kaj on 7/1/17.
  */
import math.abs

object main {
  def reduce(f: Int => Int, combine: (Int, Int) => Int, zero: Int)(a: Int, b: Int): Int =
    if (a > b) zero
    else combine(f(a), reduce(f,combine,zero)(a+1,b))

  def prod(f: Int => Int)(a: Int, b: Int): Int = reduce(f,(x,y) => x*y,1)(1,4)
  prod(x => x*x)(3,4)



  val tolerance = 0.0001
  def isCloseEnough(x: Double, y: Double) =
    abs( (x-y) /x) / x < tolerance
  def fixedPoint(f: Double => Double)(firstGuess: Double) = {
    def iterate(guess: Double): Double = {
      val next = f(guess)
      if (isCloseEnough(guess,next)) next
      else iterate(next)
    }
    iterate(firstGuess)
  }
  fixedPoint(x => 1 + x/2)(1)

  def averageDamp(f: Double => Double)(x: Double) = (x + f(x))/2

  def mySqrt(x: Double) =
    fixedPoint(averageDamp(y => x/y))(1)

  mySqrt(16.0)

}

