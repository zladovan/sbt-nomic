package com.zlad.sbt.nomic.model

case class NomicFactGroup(name: String, params: List[String] = List.empty)

sealed abstract class NomicFact(val group: Option[NomicFactGroup])
case class Group(name: NomicPath) extends NomicFact(None)
case class Name(name: NomicPath) extends NomicFact(None)
case class Version(name: String) extends NomicFact(None)
case class Module(name: NomicPath) extends NomicFact(None)
case class Const(name: String, value: String) extends NomicFact(None)

sealed abstract class HdfsNomicFact extends NomicFact(Some(NomicFactGroup("hdfs", List.empty)))
case class HdfsResource(path: NomicPath, to: Option[NomicPath] = None) extends HdfsNomicFact
case class HdfsDir(path: NomicPath) extends HdfsNomicFact
case class HdfsTemplate(path: NomicPath, fields: Map[String, String]) extends HdfsNomicFact

sealed abstract class HiveNomicFact(schemaName: Option[String])
    extends NomicFact(Some(NomicFactGroup("hive", schemaName.toList)))
case class HiveSchema(name: String) extends HiveNomicFact(None)
case class HiveFields(fields: Map[String, String], schema: Option[String] = None)
    extends HiveNomicFact(schema)
case class HiveScript(path: NomicPath, schema: Option[String] = None) extends HiveNomicFact(schema)
case class HiveTable(name: String, scriptPath: NomicPath, schema: Option[String] = None)
    extends HiveNomicFact(schema)

sealed abstract class OozieNomicFact extends NomicFact(Some(NomicFactGroup("oozie")))
case class OozieCoordinator(path: NomicPath, parameters: Map[String, String] = Map.empty)
    extends OozieNomicFact
