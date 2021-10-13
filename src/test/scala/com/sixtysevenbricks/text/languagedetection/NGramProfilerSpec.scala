package com.sixtysevenbricks.text.languagedetection

import org.specs2.mutable.Specification

class NGramProfilerSpec extends Specification {

  "NGramProfiler" should {
    "extractNgrams1" in {
      NGramProfiler.extractNgrams("Fish", 1).toList must_===
        List("_", "f", "i", "s", "h", "_")
    }

    "extractNgrams2" in {
      NGramProfiler.extractNgrams("Fish", 2).toList must_===
        List("_", "_f", "f", "fi", "i", "is", "s", "sh", "h", "h_", "_")
    }

    "extractNgrams3" in {
      NGramProfiler.extractNgrams("Fish", 3).toList must_===
        List("_", "_f", "_fi", "f", "fi", "fis", "i", "is", "ish", "s", "sh", "sh_", "h", "h_", "_")
    }

    "countValue" in {
      NGramProfiler.countValues(List("red", "fish", "blue", "fish")).toMap must_===
        Map("fish" -> 2, "red" -> 1, "blue" -> 1)
    }

    "createNgramProfile" in {
      NGramProfiler.createNgramProfile("Fish", 2).toSet must_===
        Set("fi", "is", "_f", "sh", "h_")
    }

    "createNgramProfileCount" in {
      // TODO - this test may be slightly flaky because the ordering is not defined when two ngrams have the same
      // count; I've switched the "_f" and "fa" compared to the original because evidently the sort algorithm has
      // changed slightly
      NGramProfiler.createNgramProfile("Fa Ba Ba Ba", 2) must_===
        List("a_", "_b", "ba", "_f", "fa")
    }

  }

}
