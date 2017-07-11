object rationals {
  val foo: Rational = new Rational(1, 2)
  foo.numer
  foo.denom

  val bar = new Rational(1, 3)
  foo + bar

  val sub = foo.subtr(new Rational(1,6))

  class Rational(x: Int, y: Int) {

    require(y != 0, "denominator must be non-zero")

    private def gcd(a: Int, b: Int): Int = if (b==0) a else gcd(b, a % b)
    private val g = gcd(x,y)

    val numer = x
    val denom = y

    def < (that: Rational) = numer*that.denom > that.numer*denom
    def max(that: Rational) = if (this < that) that else this

    def + (that: Rational) =
      new Rational(numer * that.denom + that.numer * denom, denom * that.denom)

    def unary_- : Rational = new Rational(-numer,denom)

    def subtr(that: Rational) = this + -that

    override def toString = numer/g + "/" + denom/g
  }
}
