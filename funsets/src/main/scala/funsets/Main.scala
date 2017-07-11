package funsets

object Main extends App {
  import FunSets._
  val bigSet1 = union(singletonSet(1),singletonSet(2))
  val bigSet2 = union(singletonSet(2),singletonSet(3))
  val bigSet3 = union(bigSet1,bigSet2)
  val bigSet4 = union(singletonSet(1),union(singletonSet(2),singletonSet(3)))
  val outlier = singletonSet(900)
  val goodPoints = union(singletonSet(1),singletonSet(3))
  val badPoints = union(singletonSet(1),singletonSet(4))
  //println(forall(bigSet3,bigSet4))
  //println(forall(bigSet3,goodPoints))
  //println(forall(bigSet3,badPoints))
  //println(exists(bigSet3,outlier))
  var inc = (x:Int) => x+1
  printSet(map(bigSet4,inc))
}