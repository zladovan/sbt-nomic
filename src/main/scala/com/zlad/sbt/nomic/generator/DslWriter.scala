package com.zlad.sbt.nomic.generator

sealed abstract class DslWriter {
  def content: String
  def map(f: String => DslWriter): DslWriter = f(content)
}

case object EmptyLineWriter extends DslWriter {
  override def content: String = "\n"
}

case class SimpleWriter(content: String) extends DslWriter

case class QuotedWriter(value: DslWriter, quote: String) extends DslWriter {
  override def content: String = s"$quote${value.content}$quote"
}

case class SequenceWriter(values: Seq[DslWriter], delimiter: String) extends DslWriter {
  override def content: String = values.map(_.content).mkString(delimiter)
}

case class GroupWriter(name: DslWriter, writers: Seq[DslWriter]) extends DslWriter {
  override def content: String =
    s"""|
        |${name.content} {
        |  ${writers.map(_.content).mkString("\n  ")}
        |}""".stripMargin.replaceAll("(?m)^\\s+$", "")
}

case class CallWriter(name: DslWriter, params: Seq[DslWriter]) extends DslWriter {
  override def content: String =
    s"""${name.content} (${params.map(_.content).mkString(", ")})"""
}

object DslWriter {

  object implicits {
    implicit def stringToSimpleWriter[A](value: A)(implicit f: A => String): SimpleWriter =
      SimpleWriter(value)
  }

  def spaceDelimited(field: DslWriter, value: DslWriter) =
    SequenceWriter(Seq(field, value), " ")

  def assignment(field: DslWriter, value: DslWriter) =
    SequenceWriter(Seq(field, value), " = ")

  def property(field: DslWriter, value: DslWriter) =
    SequenceWriter(Seq(field, value), ": ")

  def quoted(value: DslWriter) =
    QuotedWriter(value, "'")

  def doubleQuoted(value: DslWriter) =
    QuotedWriter(value, '"'.toString)

  def lines(values: Seq[DslWriter]) =
    SequenceWriter(values, "\n")
}
