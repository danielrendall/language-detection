package com.sixtysevenbricks.text.languagedetection

import org.specs2.mutable.Specification

import java.io.File

class LanguageDetectorSpec extends Specification {
  var detector = new LanguageDetector(getFile("/profile"), List("fr-FR", "de-DE", "en-US"))

  def getFile(path: String) = new File(this.getClass.getResource(path).getFile.replace("%20", " "))

  "Language detector" should {

    "extractNgrams1" in {
      detector.extractNgrams("Fish", 1).toList must_=== List("_", "f", "i", "s", "h", "_")
    }

    "extractNgrams2" in {
      detector.extractNgrams("Fish", 2).toList must_=== List("_", "_f", "f", "fi", "i", "is", "s", "sh", "h", "h_", "_")
    }

    "extractNgrams3" in {
      detector.extractNgrams("Fish", 3).toList must_=== List("_", "_f", "_fi", "f", "fi", "fis", "i", "is", "ish", "s", "sh", "sh_", "h", "h_", "_")
    }

    "countValue" in {
      detector.countValues(List("red", "fish", "blue", "fish")).toMap must_=== Map("fish" -> 2, "red" -> 1, "blue" -> 1)
    }

    "createNgramProfile" in {
      detector.createNgramProfile("Fish", 2).toSet must_=== Set("fi", "is", "_f", "sh", "h_")
    }

    "createNgramProfileCount" in {
      // TODO - this test may be slightly flaky because the ordering is not defined when two ngrams have the same
      // count; I've switched the "_f" and "fa" compared to the original because evidently the sort algorithm has
      // changed slightly
      detector.createNgramProfile("Fa Ba Ba Ba", 2) must_=== List("a_", "_b", "ba", "_f", "fa")
    }

    "identifyEnglish" in {
      detector.identifyLanguage("Hello world this is English") must_=== "en-US"
    }

    "identifyFrench" in {
      detector.identifyLanguage("Calcul des structures pour leur ") must_=== "fr-FR"
    }

    "identifyGerman" in {
      detector.identifyLanguage("Bemessung und Konstruktion von Stahlbauten") must_=== "de-DE"
    }
  }
}
