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

  private val Zero: (String, Int) = ("", Int.MinValue)

  /** Identify a text as being in a specific language, returning the name of that language. */
  def identifyLanguage(text: String): String = {
    val maxChars = 4

    val profile = NGramProfiler.createNgramProfile(text, maxChars).take(300)

    // We prepend zero to ensure this returns an empty string if there are no configured languages
    (Zero :: fingerprints.get().values.map(lf => (lf.languageName, lf.computeDistance(profile, maxChars))).toList).max._1
  }

  /**
   * Add or replace an existing fingerprint (this happens atomically; anything currently being processed will still use
   * the old collection of fingerprints). Note that this mutates the current language detector!
   * @param newFingerprint The new language fingerprint which will replace an existing one if the language matches
   * @return True if we replaced an existing fingerprint, otherwise false
   */
  def addOrUpdateFingerprint(newFingerprint: LanguageFingerprint): Boolean = {
    fingerprints.synchronized {
      val currentMap = fingerprints.get()
      val ret = currentMap.contains(newFingerprint.languageName)
      fingerprints.set(currentMap.updated(newFingerprint.languageName, newFingerprint))
      ret
    }
  }

  /**
   * Remove a fingerprint for the given language. Note that this mutates the current language detector!
   * @param language Name of the language to remove
   * @return True if we removed the fingerprint, otherwise false
   */
  def removeFingerprint(language: String): Boolean = {
    fingerprints.synchronized {
      val currentMap = fingerprints.get()
      val ret = currentMap.contains(language)
      if (ret) {
        fingerprints.set(currentMap.-(language))
      }
      ret
    }
  }

  /**
   * Return the languages that we currently know about.
   * @return
   */
  def getAvailableLanguages: Seq[String] = fingerprints.get().keys.toList.sorted
}

object LanguageDetector {

  /**
   * The languages for which we have built-in support
   */
  val availableLanguages: Seq[String] = Seq("de-DE", "en-US", "fr-FR")

  /**
   * Return a LanguageDetector with no initial languages (
   */
  def newEmpty: LanguageDetector = new LanguageDetector(Seq.empty)

  /**
   * Build a language detector initialised with fingerprints taken from the resources directory; these are currently
   * de-DE, en-US and fr-FR
   *
   * @return A default LanguageDetector
   */
  def newDefault: LanguageDetector = {
    val fingerprints: Seq[LanguageFingerprint] = availableLanguages.map { l =>
      LanguageFingerprint.load(l, LanguageDetector.getClass.getResourceAsStream(s"/profile/$l.fp"))
    }
    new LanguageDetector(fingerprints)
  }

}
