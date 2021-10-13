package com.sixtysevenbricks.text.languagedetection

import java.util.concurrent.atomic.AtomicReference

/**
 * Work out the language of a piece of text by comparing its n-gram fingerprint
 * with a set of pre-prepared fingerprints for various languages.
 * <p>
 * This is inspired by the paper "Evaluation of Language Identification Methods"
 * by Simon Kranig (http://www.sfs.uni-tuebingen.de/iscl/Theses/kranig.pdf),
 * linked to from http://tnlessone.wordpress.com/2007/05/13/how-to-detect-which-language-a-text-is-written-in-or-when-science-meets-human/,
 * and the code and explanation at http://boxoffice.ch/pseudo/code_functions.php.
 *
 * @author Inigo Surguy
 * @created 6th June 2009
 */
class LanguageDetector(initialFingerprints: Iterable[LanguageFingerprint]) {

  private final val fingerprints =
    new AtomicReference[Map[String, LanguageFingerprint]](initialFingerprints.map(s => s.languageName -> s).toMap)

  /** Identify a text as being in a specific language, returning the name of that language. */
  def identifyLanguage(text: String): String = {
    val maxChars = 4

    val profile = NGramProfiler.createNgramProfile(text, maxChars).take(300)

    fingerprints.get().values.map(lf => (lf.languageName, lf.computeDistance(profile, maxChars))).max._1
  }
}

object LanguageDetector {

  /**
   * The languages for which we have built-in support
   */
  val availableLanguages: Seq[String] = Seq("de-DE", "en-US", "fr-FR")

  /**
   * Build a language detector initialised with fingerprints taken from the resources directory; these are currently
   * de-DE, en-US and fr-FR
   *
   * @return A default LanguageDetector
   */
  def default: LanguageDetector = {
    val fingerprints: Seq[LanguageFingerprint] = availableLanguages.map { l =>
      LanguageFingerprint.load(l, LanguageDetector.getClass.getResourceAsStream(s"/profile/$l.fp"))
    }
    new LanguageDetector(fingerprints)
  }

}
