package com.zlad.sbt.nomic.generator

import org.scalatest.{FlatSpec, Matchers}
import com.zlad.sbt.nomic.model._
import NomicPath.implicits._

class NomicFactWriterSpec extends FlatSpec with Matchers {

  "Nomic dsl writer" should "write faction" in {
    val dsl = write(HdfsResource(path = "file.txt", faction = Some("test")))
    dsl shouldEqual """
      |faction ("test") {
      |
      |  hdfs {
      |    resource 'file.txt'
      |  }
      |}""".stripMargin
  }

  it should "write faction with dependency" in {
    val dsl = write(HdfsResource(path = "file.txt", faction = Some("test")), Map("test" -> "prod"))
    dsl shouldEqual """
      |faction ("test", dependsOn = "prod") {
      |
      |  hdfs {
      |    resource 'file.txt'
      |  }
      |}""".stripMargin
  }

  it should "write hdfs resource" in {
    val dsl = write(HdfsResource(path = "file.txt"))
    dsl shouldEqual """
      |hdfs {
      |  resource 'file.txt'
      |}""".stripMargin
  }

  it should "write hdfs resource with path change" in {
    val dsl = write(HdfsResource(path = "file.txt", to = Some("new_path/file.csv")))
    dsl shouldEqual """
      |hdfs {
      |  resource 'file.txt' to "new_path/file.csv"
      |}""".stripMargin
  }

  it should "write hdfs resource with keepIt" in {
    val dsl = write(HdfsResource(path = "file.txt", keepIt = true))
    dsl shouldEqual """
      |hdfs {
      |  resource 'file.txt' keepIt true
      |}""".stripMargin
  }

  it should "write hdfs dir" in {
    val dsl = write(HdfsDir(path = "data"))
    dsl shouldEqual """
      |hdfs {
      |  dir "data"
      |}""".stripMargin
  }

  it should "write hdfs dir with keepIt" in {
    val dsl = write(HdfsDir(path = "data", keepIt = true))
    dsl shouldEqual """
      |hdfs {
      |  dir "data" keepIt true
      |}""".stripMargin
  }

  it should "write hive table" in {
    val dsl = write(HiveTable(name = "transactions", scriptPath = "create-table.q"))
    dsl shouldEqual """
      |hive {
      |  table "transactions" from "create-table.q"
      |}""".stripMargin
  }

  it should "write hive table with keepIt" in {
    val dsl = write(HiveTable(name = "transactions", scriptPath = "create-table.q", keepIt = true))
    dsl shouldEqual """
      |hive {
      |  table "transactions" from "create-table.q" keepIt true
      |}""".stripMargin
  }

  it should "write hive script" in {
    val dsl = write(HiveScript(path = "prepare.q"))
    dsl shouldEqual """
      |hive {
      |  script "prepare.q"
      |}""".stripMargin
  }

  it should "write hive elements with different schemas" in {
    val dsl = writeMany(Seq(
      HiveTable(name = "transactions", scriptPath = "create-table.q", schema = Some("first")),
      HiveScript(path = "prepare.q", schema = Some("second"))
    ))
    dsl shouldEqual """
      |hive ("second") {
      |  script "prepare.q"
      |}
      |
      |hive ("first") {
      |  table "transactions" from "create-table.q"
      |}""".stripMargin
  }

  it should "write oozie coordinator" in {
    val dsl = write(OozieCoordinator(path = "main.xml"))
    dsl shouldEqual """
        |oozie {
        |  coordinator "main.xml"
        |}""".stripMargin
  }

  it should "write oozie coordinator with params" in {
    val dsl = write(OozieCoordinator(path = "main.xml", parameters = Map("a" -> "1", "b" -> "2")))
    dsl shouldEqual """
        |oozie {
        |  coordinator "main.xml" parameters a: "1", b: "2"
        |}""".stripMargin
  }

  it should "write oozie coordinator with keepIt" in {
    val dsl = write(OozieCoordinator(path = "main.xml", keepIt = true))
    dsl shouldEqual """
        |oozie {
        |  coordinator "main.xml" keepIt true
        |}""".stripMargin
  }

  def writeMany(facts: Seq[NomicFact], dependencies: Map[String, String] = Map.empty): String =
    NomicFactWriter(facts, dependencies).content

  def write(fact: NomicFact, dependencies: Map[String, String] = Map.empty): String =
    writeMany(Seq(fact), dependencies)

}
