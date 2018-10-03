import com.typesafe.sbt.SbtGit.GitKeys
import GitKeys._

enablePlugins(GitVersioning)

publishMavenStyle := false

bintrayPackageLabels := Seq("sbt", "plugin")
bintrayPackage := name.value
bintrayReleaseOnPublish := true
bintrayOrganization := Some("zlad")
bintrayRepository := "sbt-plugins"
bintrayVcsUrl := Some("https://github.com/zladovan/sbt-nomic.git")

git.useGitDescribe := true
git.uncommittedSignifier := None
isSnapshot := false

// version based on git describe
version := version.value
  .replaceFirst("-", ".") // with number of commits as minor version
  .replaceAll("-.*", "") // without commit hash
  .replaceAll("^(\\d+\\.\\d+)$", "$1.0") // with 0 as minor right after tag