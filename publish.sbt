import com.typesafe.sbt.SbtGit.GitKeys

publishMavenStyle := false

bintrayPackageLabels := Seq("sbt", "plugin")
bintrayVcsUrl := Some("git@github.com:zladovan/sbt-nomic.git")
bintrayPackage := name.value
bintrayReleaseOnPublish := true
bintrayOrganization := (if (isSnapshot.value) Some("zlad") else Some("sbt"))
bintrayRepository := (if (isSnapshot.value) "sbt-plugin-snapshots" else "sbt-plugin-releases")

// update version with build number (on master)
version := {
  (version.value, git.gitCurrentBranch.value, sys.props.get("buildNr")) match {
    case (v, "master", Some(n)) => v.replace("-SNAPSHOT", s".$n")
    case (v, _, _) => v.replace("-SNAPSHOT", s"-${System.currentTimeMillis}")
  }
}

// tag with current version (when publish from master)
publish := {
  publish.value
  (version.value, git.gitCurrentBranch.value, GitKeys.gitRunner.value, baseDirectory.value, streams.value.log) match {
    case (v, "master", runGit, cwd, log) =>
      runGit("tag", s"v${version.value}")(cwd, log)
      runGit("push", "origin", "--tags")(cwd, log)
    case _ => // nothing to do on publish from other branch than master
  }
}