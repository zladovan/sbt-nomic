package com.zlad.sbt.nomic.generator

import com.zlad.sbt.nomic.model.NomicFact
import sbt.{File, TaskKey, taskKey}

trait NomicGeneratorKeys {
  val nomic: TaskKey[File] = taskKey[File]("Collect all files in nomic target")
  val nomicFacts: TaskKey[Seq[NomicFact]] = taskKey[Seq[NomicFact]]("Resources in nomic module")
  val nomicDescribe: TaskKey[String] = taskKey[String]("Nomic box DSL content")
}
