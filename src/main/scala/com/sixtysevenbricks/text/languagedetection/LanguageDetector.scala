package com.sixtysevenbricks.text.languagedetection

import com.sixtysevenbricks.text.languagedetection.Implicits.StringOps
import com.sixtysevenbricks.text.languagedetection.Shims.{ListOps, MapOps}

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

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
class LanguageDetector(fingerprintDir: File, languagesToCheck: List[String]) {

  private val fingerprints: Map[String, Seq[String]] = readFingerprints(fingerprintDir)

  /** Identify a text as being in a specific language, returning the name of that language. */
  def identifyLanguage(text: String): String = {
    val maxChars = 4

    val profile = NGramProfiler.createNgramProfile(text, maxChars).take(300)
    val languageScores: Seq[(String, Int)] = for (language <- languagesToCheck;
                                                  languagePrints = fingerprints(language).filter(_.length <= maxChars))
    yield (language, computeDistance(profile, languagePrints))
    languageScores.max._1
  }

  /** Read language fingerprints from .lm files containing one line per ngram sorted in order of occurrence */
  private def readFingerprints(dir: File): Map[String, Seq[String]] = {
    dir.listFiles.filter(_.getName.endsWith(".fp")).map { file =>
      val languageName = file.getName.substring(0, file.getName.lastIndexOf(".fp"))
      val lines = Files.readAllLines(file.toPath, StandardCharsets.UTF_8).asScalaList
      languageName -> lines
    }.toMap
  }


  /** Work out the distance between two n-gram profiles. */
  private def computeDistance(ngrams: Profile, otherProfile: Profile): Int = {
    val max = 30
    val termDistances = for (key <- ngrams;
                             rank = ngrams.indexOf(key);
                             otherRank = if (otherProfile.contains(key)) otherProfile.indexOf(key) else 999;
                             diff = Math.abs(rank - otherRank))
    yield if (diff > max) max else diff
    termDistances.sum
  }

}
