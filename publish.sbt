import com.typesafe.sbt.SbtGit.GitKeys
import GitKeys._

lazy val isRelease = Def.setting(git.gitCurrentBranch.value == "master")

publishMavenStyle := false

bintrayPackageLabels := Seq("sbt", "plugin")
bintrayVcsUrl := Some("git@github.com:zladovan/sbt-nomic.git")
bintrayPackage := name.value
bintrayReleaseOnPublish := true
bintrayOrganization := Some("zlad")
bintrayRepository := (if (isRelease.value) "sbt-plugins" else "sbt-plugin-snapshots")

// use build number for release, git commit hash for snapshot version
version := {
  (version.value, isRelease.value, sys.props.get("buildNr"), gitHeadCommit.value) match {
    case (v, true, Some(n), _) => v.replace("-SNAPSHOT", s".$n")
    case (v, _, _, Some(hash)) => v.replace("-SNAPSHOT", s"-${hash.substring(0, 7)}")
    case (v, _, _, _)          => v
  }
}

// tag release version after publish
publish := {
  publish.value
  (version.value, isRelease.value, gitRunner.value, baseDirectory.value, streams.value.log) match {
    case (ver, true, runGit, cwd, log) =>
      runGit("tag", s"v$ver")(cwd, log)
      runGit("push", "origin", "--tags")(cwd, log)
    case _ => // nothing to do on publish from other branch than master
  }
}
