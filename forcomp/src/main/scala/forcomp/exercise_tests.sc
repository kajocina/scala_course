import forcomp.Anagrams._


def wordOccurrences(w: Word): Occurrences = {
  // word == string -> count each letter and make a Map-like List[(Char,Int)]
  w.toLowerCase.groupBy(x => x).mapValues(x => x.length()).toList.sortBy(_._1)
}

/** Converts a sentence into its character occurrence list.  **/
def sentenceOccurrences(s: Sentence): Occurrences = {
  s.flatMap(x => wordOccurrences(x)).groupBy(_._1).mapValues(x => x.map(y => y._2)).mapValues(x => x.sum).toList.sortBy(_._1)
}


val foo: Occurrences = sentenceOccurrences(List("cccbbb"))
val abbacomb = List(
  List(),
  List(('a', 1)),
  List(('a', 2)),
  List(('b', 1)),
  List(('a', 1), ('b', 1)),
  List(('a', 2), ('b', 1)),
  List(('b', 2)),
  List(('a', 1), ('b', 2)),
  List(('a', 2)))

def combinations(occurrences: Occurrences):  List[List[(Char, Int)]] = {
  if (occurrences == List()) {return List(List())}
  def iterateCombos(occurrences: Occurrences): List[Iterator[List[(Char, Int)]]] = {
    for {i <- occurrences.indices.toList
         j <- (1 until occurrences(i)._2).toList
    } yield occurrences.updated(i, (occurrences(i)._1, occurrences(i)._2 - j)).toSet.subsets().map(_.toList)
  }
  iterateCombos(occurrences).flatMap(_.toList).distinct.map(_.sorted)
}
combinations(foo).toSet
abbacomb.toSet