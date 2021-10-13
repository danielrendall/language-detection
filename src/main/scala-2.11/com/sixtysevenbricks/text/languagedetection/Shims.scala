package com.sixtysevenbricks.text.languagedetection

import scala.collection.JavaConverters.asScalaBufferConverter

/**
 * Allows conversion to be shimmed
 */
object Shims {

  implicit class ListOps[T](aList: java.util.List[T]) {
    def asScalaList: List[T] = aList.asScala.toList
  }

  implicit class MapOps[T, U](aMap: Map[T, U]) {
    def myFilterKeys(p: T => Boolean): Map[T, U] = aMap.filterKeys(p)
    def myMapValues[W](p: U => W): Map[T, W] = aMap.mapValues(p)
  }

}
