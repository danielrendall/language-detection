package com.sixtysevenbricks.text.languagedetection

import org.specs2.mutable.Specification

import java.io.File

class LanguageDetectorSpec extends Specification {
  var detector = new LanguageDetector(getFile("/profile"), List("fr-FR", "de-DE", "en-US"))

  def getFile(path: String) = new File(this.getClass.getResource(path).getFile.replace("%20", " "))

  "Language detector" should {


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
