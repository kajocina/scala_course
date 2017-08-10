import forcomp.Anagrams._


def wordOccurrences(w: Word): Occurrences = {
  // word == string -> count each letter and make a Map-like List[(Char,Int)]
  w.toLowerCase.groupBy(x => x).mapValues(x => x.length()).toList.sortBy(_._1)
}

/** Converts a sentence into its character occurrence list.  **/
def sentenceOccurrences(s: Sentence): Occurrences = {
  s.flatMap(x => wordOccurrences(x)).groupBy(_._1).mapValues(x => x.map(y => y._2)).mapValues(x => x.sum).toList.sortBy(_._1)
}

def combinations(occurrences: Occurrences):  List[List[(Char, Int)]] = {
  if (occurrences == List()) {List(List())}
  else {
    def iterateCombos(occurrences: Occurrences): List[Iterator[List[(Char, Int)]]] = {
      for {i <- occurrences.indices.toList
           j <- (0 until occurrences(i)._2).toList
      } yield occurrences.updated(i, (occurrences(i)._1, occurrences(i)._2 - j)).toSet.subsets().map(_.toList)
    }
    iterateCombos(occurrences).flatMap(_.toList).distinct.map(x => iterateCombos(x)).flatMap(_.toList).flatten.distinct.filter(_ != occurrences)
  }
}

val foo = List("Yes","man")
def sentenceAnagrams(sentence: Sentence): List[List[Word]] = {

}

sentenceAnagrams(List("Yes", "man"))
