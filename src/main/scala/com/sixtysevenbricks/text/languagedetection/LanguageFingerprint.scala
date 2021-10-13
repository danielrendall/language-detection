package com.sixtysevenbricks.text.languagedetection

import java.io.InputStream
import scala.io.{Codec, Source}


/**
 * Holds the fingerprint of a language in terms of an ordered list of common ngrams
 */
case class LanguageFingerprint(languageName: String, fingerprint: Profile) {

  /**
   * Compute some measure of distance between this language fingerprint and the given profile
   * @param ngrams
   * @return
   */
  def computeDistance(ngrams: Profile, maxChars: Int): Int = {
    val max = 30
    val filtered = fingerprint.filter(_.length <= maxChars)
    val termDistances = for (key <- ngrams;
                             rank = ngrams.indexOf(key);
                             otherRank = if (filtered.contains(key)) filtered.indexOf(key) else 999;
                             diff = Math.abs(rank - otherRank))
    yield if (diff > max) max else diff
    termDistances.sum
  }

}

object LanguageFingerprint {

  private implicit val codec: Codec = Codec.UTF8

  def load(languageName: String, inputStream: InputStream): LanguageFingerprint =
    LanguageFingerprint(languageName, Source.fromInputStream(inputStream).getLines().toList)

}
