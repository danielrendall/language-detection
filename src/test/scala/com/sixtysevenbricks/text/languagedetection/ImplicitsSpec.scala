package com.sixtysevenbricks.text.languagedetection

import com.sixtysevenbricks.text.languagedetection.Implicits.StringOps
import org.specs2.mutable.Specification

class ImplicitsSpec extends Specification {

  "StringOps" should {

    "removePunctuation" in {
      "Fi!sh cakes.?123".withoutPunctuation must_=== "Fish cakes"
    }

    "removePunctuationEmptyString" in {
      "".withoutPunctuation must_=== ""
    }

    "normalizeSpace" in {
      "  Fish\u0009 \u000A cakes \u000A  ".withNormalisedSpace must_=== "Fish cakes"
    }
  }
}
