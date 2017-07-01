package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
    * Exercise 1
    */
  def pascal(c: Int, r: Int): Int = {
    if (c == 0 || r == 0 || r == c) {
      1
    }
    else {
      pascal(c - 1, r - 1) + pascal(c, r - 1)
    }
  }

  /**
    * Exercise 2
    */
  def balance(chars: List[Char]): Boolean = {

    def search(open: Int, rest: List[Char]): Int = {

      if (rest.isEmpty) {
        return open
      }
      if (rest.head == '(') {
        return search(open+1,rest.tail)
      }
      if (rest.head == ')' && open == 0) {
        return -1 // ugly hack
      }
      if (rest.head == ')') {
        return search(open-1,rest.tail)
      }
      if (rest.head != ')' && rest.head != '('){
        return search(open,rest.tail)
      }

      return open
    }

    if (search(0, chars) == 0) {true}
    else {false}
  }

  /**
    * Exercise 3
    */

  def countChange(money: Int, coins: List[Int]): Int = {
    if (coins.isEmpty || money < 0) {return 0}
    if (money == 0) {return 1}
    return countChange(money-coins.head,coins) + countChange(money,coins.tail)
  }

}
