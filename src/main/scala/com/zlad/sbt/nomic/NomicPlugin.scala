package com.zlad.sbt.nomic

import com.zlad.sbt.nomic.model.{Name, NomicPath, Version}
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

object NomicPlugin extends AutoPlugin {
  override def requires: Plugins = JvmPlugin
  override def trigger: PluginTrigger = allRequirements

  object autoImport extends NomicKeys

  import NomicPath.implicits._
  import autoImport._
  import controller.NomicController._
  import generator.NomicGenerator._

  override def projectSettings: Seq[Def.Setting[_]] = nomicSettings

  lazy val nomicSettings: Seq[Def.Setting[_]] =
    nomicGeneralSettings ++ nomicGeneratorSettings ++ nomicControllerSettings

  lazy val nomicGeneralSettings: Seq[Def.Setting[_]] = Seq(
    name in nomic := name.value,
    version in nomic := version.value,
    nomicFacts := Seq(
      Name((name in nomic).value),
      Version((version in nomic).value)
    )
  )

  lazy val nomicGeneratorSettings: Seq[Def.Setting[_]] = Seq(
    mappings in nomic := Seq.empty,
    target in nomic := target.value / "nomic",
    nomicDescribe := describe(nomicFacts.value, nomicFactionDependencies.value),
    nomic := stage((target in nomic).value, (mappings in nomic).value, nomicDescribe.value),
    nomicFactionDependencies := Map.empty
  )

  lazy val nomicControllerSettings: Seq[Def.Setting[_]] = Seq(
    nomicInstall := install(nomic.value.getAbsolutePath),
    nomicRemove := remove(nomicFacts.value, streams.value.log),
    nomicList := list(streams.value.log),
    nomicConfig := config(streams.value.log),
    nomicUpgrade := {
      nomicRemove.value
      nomicInstall.value
    }
  )
}
