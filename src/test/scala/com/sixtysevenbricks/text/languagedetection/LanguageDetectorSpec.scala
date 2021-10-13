package com.sixtysevenbricks.text.languagedetection

import org.specs2.mutable.Specification

class LanguageDetectorSpec extends Specification {

  "Language detector" should {

    "identifyEnglish" in {
      LanguageDetector.newDefault.identifyLanguage("Hello world this is English") must_=== "en-US"
    }

    "identifyFrench" in {
      LanguageDetector.newDefault.identifyLanguage("Calcul des structures pour leur ") must_=== "fr-FR"
    }

    "identifyGerman" in {
      LanguageDetector.newDefault.identifyLanguage("Bemessung und Konstruktion von Stahlbauten") must_=== "de-DE"
    }
  }

  "Adding and removing languages" should {

    "return blank if no language is configured" in {
      val detector = LanguageDetector.newEmpty
      detector.identifyLanguage("Hello world this is English") must_=== ""
    }

    "allow a language to be added" in {
      val detector = LanguageDetector.newEmpty
      detector.addOrUpdateFingerprint(LanguageFingerprint.load("fr-FR",
        LanguageDetector.getClass.getResourceAsStream("/profile/fr-FR.fp")))
      // If there's only one language, everything will look like that language
      detector.identifyLanguage("Hello world this is English") must_=== "fr-FR"
    }

    "allow a language to be removed" in {
      val detector = LanguageDetector.newDefault
      detector.identifyLanguage("Calcul des structures pour leur ") must_=== "fr-FR"
      detector.removeFingerprint("fr-FR")
      // If French is not an option, this apparently looks more like German than English
      detector.identifyLanguage("Calcul des structures pour leur ") must_=== "de-DE"
    }
  }
}
