package com.sixtysevenbricks.text.languagedetection

import scala.jdk.CollectionConverters.ListHasAsScala

/**
 * Allows conversion to be shimmed
 */
object Shims {

  implicit class ListOps[T](aList: java.util.List[T]) {
    def asScalaList: List[T] = aList.asScala.toList
  }

  implicit class MapOps[T, U](aMap: Map[T, U]) {
    def myFilterKeys(p: T => Boolean): Map[T, U] = aMap.view.filterKeys(p).toMap
    def myMapValues[W](p: U => W): Map[T, W] = aMap.view.mapValues(p).toMap
  }

}
