name := "sbt-nomic"
organization := "com.zlad"
description := "Sbt plugin for nomic - applications deployer for hadoop ecosystem"
licenses += "Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
homepage := Some(url("https://github.com/zladovan/sbt-nomic"))
scmInfo := Some(ScmInfo(url("https://github.com/zladovan/sbt-nomic"), "git@github.com:zladovan/sbt-nomic.git"))

sbtPlugin := true

scalaVersion := appConfiguration.value.provider.scalaProvider.version
crossSbtVersions := Seq("0.13.16", "1.0.4")

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.1" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
