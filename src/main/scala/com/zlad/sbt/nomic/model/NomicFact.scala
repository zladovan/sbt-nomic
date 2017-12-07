package com.zlad.sbt.nomic.model

case class NomicFactGroup(name: String, params: List[String] = List.empty)

sealed abstract class NomicFact(val group: Option[NomicFactGroup], val factionName: Option[String])

// General facts
case class Group(name: NomicPath) extends NomicFact(None, None)
case class Name(name: NomicPath) extends NomicFact(None, None)
case class Version(name: String) extends NomicFact(None, None)
case class Module(name: NomicPath) extends NomicFact(None, None)
case class Const(name: String, value: String) extends NomicFact(None, None)


// HDFS
sealed abstract class HdfsNomicFact(faction: Option[String])
  extends NomicFact(Some(NomicFactGroup("hdfs", List.empty)), faction)

case class HdfsResource(
  path: NomicPath,
  to: Option[NomicPath] = None,
  keepIt: Boolean = false,
  faction: Option[String] = None
) extends HdfsNomicFact(faction)

case class HdfsDir(
  path: NomicPath,
  keepIt: Boolean = false,
  faction: Option[String] = None
) extends HdfsNomicFact(faction)


// Hive
sealed abstract class HiveNomicFact(schemaName: Option[String], faction: Option[String])
  extends NomicFact(Some(NomicFactGroup("hive", schemaName.toList)), faction)

case class HiveSchema(name: String, keepIt: Boolean, faction: Option[String])
  extends HiveNomicFact(None, faction)

case class HiveFields(
  fields: Map[String, String],
  schema: Option[String] = None,
  faction: Option[String] = None
) extends HiveNomicFact(schema, None)

case class HiveScript(
  path: NomicPath,
  schema: Option[String] = None,
  faction: Option[String] = None
) extends HiveNomicFact(schema, None)

case class HiveTable(
  name: String,
  scriptPath: NomicPath,
  schema: Option[String] = None,
  keepIt: Boolean = false,
  faction: Option[String] = None
) extends HiveNomicFact(schema, faction)


// Oozie
sealed abstract class OozieNomicFact(faction: Option[String])
  extends NomicFact(Some(NomicFactGroup("oozie")), faction)

case class OozieCoordinator(
  path: NomicPath,
  parameters: Map[String, String] = Map.empty,
  keepIt: Boolean = false,
  faction: Option[String] = None
) extends OozieNomicFact(faction)
