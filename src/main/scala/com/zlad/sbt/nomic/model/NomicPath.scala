package com.zlad.sbt.nomic.model

import scala.language.implicitConversions

class NomicPath(path: String) {
  override def toString: String = path
}
object NomicPath {
  def fromAnyPath(path: String): NomicPath = new NomicPath(path.replace("\\", "/"))

  object implicits {
    implicit def stringToNomicPath(path: String): NomicPath = NomicPath.fromAnyPath(path)
  }
}
