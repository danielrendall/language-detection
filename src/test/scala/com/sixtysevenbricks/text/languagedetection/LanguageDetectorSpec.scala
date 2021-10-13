package com.sixtysevenbricks.text.languagedetection

import org.specs2.mutable.Specification

class LanguageDetectorSpec extends Specification {
  val detector: LanguageDetector = LanguageDetector.default

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
