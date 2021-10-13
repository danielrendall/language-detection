package com.sixtysevenbricks.text.languagedetection

object Implicits {

  implicit class StringOps(aString: String) {

    /** Remove non-alphabetic characters except whitespace */
    def withoutPunctuation: String = aString.replaceAll("[^\\p{L}\\s]", "")

    /** Convert tabs and runs of space to single spaces, and trim leading and trailing space. */
    def withNormalisedSpace: String = aString.trim().replaceAll("[\\s\\t]+", " ")

  }


}
