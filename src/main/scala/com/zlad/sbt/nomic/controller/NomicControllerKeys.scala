package com.zlad.sbt.nomic.controller

import sbt.{TaskKey, taskKey}

class NomicControllerKeys {
  val nomicInstall: TaskKey[Unit] = taskKey[Unit]("Install nomic box")
  val nomicRemove: TaskKey[Unit] = taskKey[Unit]("Remove nomic box")
  val nomicUpgrade: TaskKey[Unit] = taskKey[Unit]("Upgrade nomic box")
  val nomicList: TaskKey[Seq[String]] = taskKey[Seq[String]]("List all nomic boxes")
  val nomicConfig: TaskKey[Map[String, String]] = taskKey[Map[String, String]]("Check nomic config")
}
