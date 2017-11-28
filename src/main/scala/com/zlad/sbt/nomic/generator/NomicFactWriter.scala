package com.zlad.sbt.nomic.generator

import com.zlad.sbt.nomic.model._

object NomicFactWriter {

  import DslWriter._
  import implicits._

  def apply(facts: Seq[NomicFact]): DslWriter = {
    lines(
      facts
        .groupBy(_.group)
        .toSeq
        .sortBy {
          case (None, _)                            => 0
          case (Some(NomicFactGroup("hdfs", _)), _) => 1
          case _                                    => 2
        }
        .map(groupWriter)
    )
  }

  def groupWriter: ((Option[NomicFactGroup], Seq[NomicFact])) => DslWriter = {
    case (None, facts)        => lines(facts.map(writer))
    case (Some(group), facts) => GroupWriter(groupNameWriter(group), facts.map(writer))
  }

  def groupNameWriter: NomicFactGroup => DslWriter = {
    case NomicFactGroup(name, Nil)    => name
    case NomicFactGroup(name, params) => CallWriter(name, params.map(c => doubleQuoted(c)))
  }

  def writer: NomicFact => DslWriter = {
    case Group(group)       => assignment("group", quoted(group.toString))
    case Name(name)         => assignment("name", quoted(name.toString))
    case Version(version)   => assignment("version", quoted(version))
    case Module(module)     => spaceDelimited("module", quoted(module.toString))
    case Const(name, value) => assignment(name, doubleQuoted(value))

    case HdfsResource(path, None) => hdfsResource(path)
    case HdfsResource(path, Some(to)) =>
      spaceDelimited(
        hdfsResource(path),
        spaceDelimited("to", doubleQuoted(to.toString))
      )
    case HdfsDir(path) => spaceDelimited("dir", doubleQuoted(path.toString))
    case HdfsTemplate(path, fields) =>
      spaceDelimited(
        spaceDelimited("template", doubleQuoted(path.toString)),
        spaceDelimited("with", properties(fields))
      )

    case HiveFields(fields, _) => spaceDelimited("fields", properties(fields))
    case HiveSchema(schema)    => spaceDelimited("schema", doubleQuoted(schema))
    case HiveScript(script, _) => spaceDelimited("script", doubleQuoted(script.toString))
    case HiveTable(name, scriptPath, _) =>
      spaceDelimited(
        spaceDelimited("table", doubleQuoted(name)),
        spaceDelimited("from", doubleQuoted(scriptPath.toString))
      )

    case OozieCoordinator(path, params) if params.nonEmpty =>
      spaceDelimited(
        oozieCoordinator(path),
        spaceDelimited("parameters", properties(params))
      )
    case OozieCoordinator(path, _) => oozieCoordinator(path)
  }

  def hdfsResource(path: NomicPath): DslWriter =
    spaceDelimited("resource", quoted(path.toString))

  def oozieCoordinator(path: NomicPath): DslWriter =
    spaceDelimited("coordinator", quoted(path.toString))

  def properties(fields: Map[String, String]) =
    SequenceWriter(
      fields.map { case (key, value) => property(key, doubleQuoted(value)) }.toSeq,
      ", "
    )
}
