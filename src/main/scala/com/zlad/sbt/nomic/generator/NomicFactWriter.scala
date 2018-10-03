package com.zlad.sbt.nomic.generator

import com.zlad.sbt.nomic.model._

object NomicFactWriter {

  import DslWriter._
  import implicits._

  def apply(facts: Seq[NomicFact], dependencies: Map[String, String] = Map.empty): DslWriter = {
    lines(
      facts
        .groupBy(_.factionName)
        .map {
          case (k, v) =>
            (k.map(f => Faction(f, dependencies.get(f))), v.groupBy(_.group).toSeq.sortBy(noneFirst))
        }
        .toSeq
        .sortBy(noneFirst)
        .map(factionWriter)
    )
  }

  def factionWriter: ((Option[Faction], Seq[(Option[NomicFactGroup], Seq[NomicFact])])) => DslWriter = {
    case (None, facts) => lines(facts.map(groupWriter))
    case (Some(faction), facts) =>
      GroupWriter(
        CallWriter(
          "faction",
          Seq(doubleQuoted(faction.name)) ++ faction.dependency
            .map(a => assignment("dependsOn", doubleQuoted(a)))
            .toSeq
        ),
        facts.map(groupWriter(_).map(ident))
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
    case Group(group)                   => assignment("group", quoted(group.toString))
    case Name(name)                     => assignment("name", quoted(name.toString))
    case Version(version)               => assignment("version", quoted(version))
    case Module(module)                 => spaceDelimited("module", quoted(module.toString))
    case Const(name, value)             => assignment(name, doubleQuoted(value))
    case Require(group, name, version)  => require(group, name, version)

    case HdfsResource(path, None, keep, _) => keepIt(keep, hdfsResource(path))
    case HdfsResource(path, Some(to), keep, _) =>
      keepIt(
        keep,
        spaceDelimited(
          hdfsResource(path),
          spaceDelimited("to", doubleQuoted(to.toString))
        )
      )
    case HdfsDir(path, keep, _) =>
      keepIt(keep, spaceDelimited("dir", doubleQuoted(path.toString)))

    case HiveFields(fields, _, _)    => spaceDelimited("fields", properties(fields))
    case HiveSchema(schema, keep, _) => keepIt(keep, spaceDelimited("schema", doubleQuoted(schema)))
    case HiveScript(script, _, _)    => spaceDelimited("script", doubleQuoted(script.toString))
    case HiveTable(name, scriptPath, _, keep, _) =>
      keepIt(
        keep,
        spaceDelimited(
          spaceDelimited("table", doubleQuoted(name)),
          spaceDelimited("from", doubleQuoted(scriptPath.toString))
        )
      )

    case OozieCoordinator(path, params, keep, _) if params.nonEmpty =>
      keepIt(
        keep,
        spaceDelimited(
          oozieCoordinator(path),
          spaceDelimited("parameters", properties(params))
        )
      )
    case OozieCoordinator(path, _, keep, _) => keepIt(keep, oozieCoordinator(path))
  }

  def require(group: String, name: String, version: String): DslWriter =
    spaceDelimited("require", properties(Map("group" -> group, "name" -> name, "version" -> version)))

  def hdfsResource(path: NomicPath): DslWriter =
    spaceDelimited("resource", quoted(path.toString))

  def oozieCoordinator(path: NomicPath): DslWriter =
    spaceDelimited("coordinator", doubleQuoted(path.toString))

  def properties(fields: Map[String, String]) =
    SequenceWriter(
      fields.map { case (key, value) => property(key, doubleQuoted(value)) }.toSeq,
      ", "
    )

  def keepIt(keep: Boolean, writer: DslWriter): DslWriter =
    if (keep)
      spaceDelimited(writer, spaceDelimited("keepIt", "true"))
    else
      writer

  def ident: String => DslWriter =
    _.split("\n").map(l => if (l.nonEmpty) "  " + l else l).mkString("\n")

  def noneFirst[A, B]: ((Option[A], B)) => Int = {
    case (None, _) => 0
    case _ => 1
  }

  case class Faction(name: String, dependency: Option[String])

}
