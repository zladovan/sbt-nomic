package com.zlad.sbt.nomic.controller

import com.zlad.sbt.nomic.model.{Group, Name, NomicFact}

import scala.sys.process._

object NomicController {
  def install(path: String): Unit =
    s"nomic install $path".!

  def remove(facts: Seq[NomicFact], log: sbt.Logger): Unit = {
    type BoxId = (Option[String], Option[String], Option[String])
    val boxId = facts.foldLeft[BoxId]((None, None, None)) { (id, fact) =>
      fact match {
        case Group(group) => id.copy(_1 = Some(group.toString))
        case Name(name)   => id.copy(_2 = Some(name.toString))
        // todo add version with removing exact version ?
        // case Version(version) => id.copy(_3 = Some(version))
        case _ => id
      }
    }
    remove(boxId._1.getOrElse(""), boxId._2.getOrElse(""), boxId._3.getOrElse("*"), log)
  }

  def remove(group: String, name: String, version: String, log: sbt.Logger): Unit = {
    val id = s"$group:$name:$version"
    log.info(s"Removing $id")
    s"nomic remove $id".!
  }

  def list(log: sbt.Logger): Seq[String] =
    callAndLog("nomic list", log)

  def config(log: sbt.Logger): Map[String, String] =
    callAndLog("nomic config", log).collect { case configLine(k, v) => k -> v }.toMap

  private def callAndLog(command: String, log: sbt.Logger) = {
    val lines = updateForOs(command).lines_!
    lines.foreach(log.info(_))
    lines
  }

  private def updateForOs(command: String): String = {
    if (sys.props.get("os.name").exists(_.startsWith("Windows")))
      "cmd /c \"" + command + "\""
    else
      command
  }

  private lazy val configLine = "(.+?):\\s*(.*)".r

}
