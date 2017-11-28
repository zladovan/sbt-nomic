package com.zlad.sbt.nomic.generator

import java.io.File

import com.zlad.sbt.nomic.model.NomicFact
import sbt._

object NomicGenerator {

  def describe(facts: Seq[NomicFact]): String =
    NomicFactWriter(facts).content

  // todo add caching
  def stage(
    target: File,
    mappings: Seq[(File, String)],
    description: String
  ): File = {
    val root = target
    if (root.exists()) IO.delete(root)

    IO.createDirectories(for {
      (file, dest) <- mappings
      if file.isDirectory
    } yield root / dest)

    val descriptor = root / "nomic.box"
    IO.write(descriptor, description)

    for {
      (source, dest) <- mappings
      if source.exists() && !source.isDirectory
    } IO.copyFile(source, root / dest, preserveLastModified = true)

    root
  }

}
