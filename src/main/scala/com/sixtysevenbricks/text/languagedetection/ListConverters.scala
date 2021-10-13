package com.sixtysevenbricks.text.languagedetection

import scala.jdk.CollectionConverters.ListHasAsScala

/**
 * Allows conversion to be shimmed
 */
object ListConverters {

  implicit class ListOps[T](aList: java.util.List[T]) {
    def asScalaList: List[T] = aList.asScala.toList
  }

}
