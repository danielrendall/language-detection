package com.sixtysevenbricks.text.languagedetection

import com.sixtysevenbricks.text.languagedetection.ListConverters.ListOps

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

  // We want higher counts to come first, not last; negating them is a cheap trick which works...
  private implicit val tupleOrdering: Ordering[(String, Int)] = Ordering.by(x => (-x._2, x._1))

  private val fingerprints: Map[String, Seq[String]] = readFingerprints(fingerprintDir)

  type Profile = Seq[String]

  /** Identify a text as being in a specific language, returning the name of that language. */
  def identifyLanguage(text: String): String = {
    val maxChars = 4

    val profile = createNgramProfile(text, maxChars).take(300)
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

  /** Remove non-alphabetic characters except whitespace */
  def removePunctuation(text: String): String = text.replaceAll("[^\\p{L}\\s]", "")

  /** Convert tabs and runs of space to single spaces, and trim leading and trailing space. */
  def normalizeSpace(text: String): String = text.trim().replaceAll("[\\s\\t]+", " ")

  /** Provide a total occurrence for each of the distinct terms in a sequence. i.e. (red,fish,blue,fish) will return: red 1, fish 2, blue 1. */
  def countValues[T](terms: Seq[T]): Map[T, Int] = {
    terms.groupBy(identity).view.mapValues(_.length).toMap
  }

  /** Extract the n-grams (bigrams, trigrams, etc.) from a piece of text into a sequence. Spaces are represented as _. */
  def extractNgrams(text: String, maxChars: Int): Seq[String] = {
    val normalizedText: String = "_" + removePunctuation(normalizeSpace(text.toLowerCase)).replace(" ", "_") + "_"
    for {start <- (0 to normalizedText.length);
         length <- (1 to maxChars);
         if start + length <= normalizedText.length} yield normalizedText.substring(start, start + length)
  }

  /** Provide a sorted summary count of the n-grams in a piece of text. */
  def createNgramProfile(text: String, maxChars: Int): Profile =
    sortNgramValues(countValues(extractNgrams(text, maxChars)))

  /** Remove the unigrams, then sort the ngrams by their occurrence into a flat profile. */
  private def sortNgramValues(countedValues: Map[String, Int]): Profile = {
    countedValues.view.filterKeys(_.length > 1).toList.sorted.map(_._1)
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
