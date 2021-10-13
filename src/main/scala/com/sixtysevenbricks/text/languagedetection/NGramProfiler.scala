package com.sixtysevenbricks.text.languagedetection

import com.sixtysevenbricks.text.languagedetection.Implicits.StringOps
import com.sixtysevenbricks.text.languagedetection.Shims.MapOps

/**
 * Code to generate an NGram profile from a piece of text; basically an ordered list of the NGrams (up to some maximum
 * length) found in the text
 */
object NGramProfiler {

  /** Provide a sorted summary count of the n-grams in a piece of text. */
  def createNgramProfile(text: String, maxChars: Int): Profile =
    sortNgramValues(countValues(extractNgrams(text, maxChars)))

  /** Provide a total occurrence for each of the distinct terms in a sequence. i.e. (red,fish,blue,fish) will return: red 1, fish 2, blue 1. */
  def countValues[T](terms: Seq[T]): Map[T, Int] = {
    terms.groupBy(identity).myMapValues(_.length)
  }

  /** Extract the n-grams (bigrams, trigrams, etc.) from a piece of text into a sequence. Spaces are represented as _. */
  def extractNgrams(text: String, maxChars: Int): Seq[String] = {
    val normalizedText: String = "_" + text.toLowerCase.withNormalisedSpace.withoutPunctuation.replace(" ", "_") + "_"
    for {start <- (0 to normalizedText.length);
         length <- (1 to maxChars);
         if start + length <= normalizedText.length} yield normalizedText.substring(start, start + length)
  }

  /** Remove the unigrams, then sort the ngrams by their occurrence into a flat profile. */
  private def sortNgramValues(countedValues: Map[String, Int]): Profile = {
    countedValues.myFilterKeys(_.length > 1).toList.sorted.map(_._1)
  }


}
