name := "sbt-nomic"
organization := "com.zlad"
version := "0.1-SNAPSHOT"

sbtPlugin := true

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test

bintrayPackageLabels := Seq("sbt","plugin")
bintrayVcsUrl := Some("""git@github.com:com.zlad/sbt-nomic.git""")

initialCommands in console := """import nomic._"""

// set up 'scripted; sbt plugin for testing sbt plugins
scriptedLaunchOpts ++=
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
