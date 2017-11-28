name := "sbt-nomic"
organization := "com.zlad"
description := "Sbt plugin for nomic - applications deployer for hadoop ecosystem"

version := "0.1-SNAPSHOT"

sbtPlugin := true

scalaVersion := appConfiguration.value.provider.scalaProvider.version
crossSbtVersions := Seq("0.13.16", "1.0.4")

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.1" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

licenses += "Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
publishMavenStyle := false
bintrayPackageLabels := Seq("sbt", "plugin")
bintrayOrganization := Some("zlad")
bintrayVcsUrl := Some("""git@github.com:zladovan/sbt-nomic.git""")
bintrayRepository := "sbt-plugin-releases"
bintrayPackage := name.value
bintrayReleaseOnPublish := false

// set up 'scripted; sbt plugin for testing sbt plugins
scriptedLaunchOpts ++=
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
