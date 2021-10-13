package com.sixtysevenbricks.text

package object languagedetection {

  type Profile = Seq[String]

  /**
   * We represent an NGram and its count as a (String, Int). When we order them, we want highest count first.
   * Negating the count is a cheap trick with works...
   */
  implicit val tupleOrdering: Ordering[(String, Int)] = Ordering.by(x => (-x._2, x._1))


}
