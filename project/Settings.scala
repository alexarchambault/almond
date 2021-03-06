
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files

import sbt._
import sbt.Keys._

object Settings {

  def scala211 = "2.11.12"
  def scala212 = "2.12.8"

  lazy val isAtLeast212 = Def.setting {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n >= 12 => true
      case _ => false
    }
  }

  lazy val shared = Seq(
    scalaVersion := scala212,
    crossScalaVersions := Seq(scala212, "2.12.7", "2.12.6", scala211),
    scalacOptions ++= Seq(
      // see http://tpolecat.github.io/2017/04/25/scalac-flags.html
      "-deprecation",
      "-feature",
      "-explaintypes",
      "-encoding", "utf-8",
      "-language:higherKinds",
      "-unchecked"
    ),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      "jitpack" at "https://jitpack.io"
    ),
    // Seems required when cross-publishing for several scala versions
    // with same major and minor numbers (e.g. 2.12.6 and 2.12.7)
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    exportVersionsSetting
  )

  lazy val dontPublish = Seq(
    publish := {},
    publishLocal := {},
    publishArtifact := false
  )

  def generatePropertyFile(path: String) =
    resourceGenerators.in(Compile) += Def.task {
      import sys.process._

      val dir = classDirectory.in(Compile).value
      val ver = version.value

      val f = path.split('/').foldLeft(dir)(_ / _)
      f.getParentFile.mkdirs()

      val p = new java.util.Properties

      p.setProperty("version", ver)
      p.setProperty("commit-hash", Seq("git", "rev-parse", "HEAD").!!.trim)

      val w = new java.io.FileOutputStream(f)
      p.store(w, "Almond properties")
      w.close()

      state.value.log.info(s"Wrote $f")

      Seq(f)
    }

  lazy val generateDependenciesFile =
    resourceGenerators.in(Compile) += Def.task {

      val dir = classDirectory.in(Compile).value / "almond"
      val res = coursier.CoursierPlugin.autoImport.coursierResolutions
        .value
        .collectFirst {
          case (scopes, r) if scopes(coursier.core.Configuration.compile) =>
            r
        }
        .getOrElse(
          sys.error("compile coursier resolution not found")
        )

      val content = res
        .minDependencies
        .toVector
        .map { d =>
          (d.module.organization, d.module.name, d.version)
        }
        .sorted
        .map {
          case (org, name, ver) =>
          s"$org:$name:$ver"
        }
        .mkString("\n")

      val f = dir / "almond-user-dependencies.txt"
      dir.mkdirs()

      Files.write(f.toPath, content.getBytes(UTF_8))

      state.value.log.info(s"Wrote $f")

      Seq(f)
    }

  lazy val testSettings = Seq(
    fork.in(Test) := true, // Java serialization goes awry without that
    testFrameworks += new TestFramework("utest.runner.Framework"),
    javaOptions.in(Test) ++= Seq("-Xmx3g", "-Dfoo=bzz"),
    libraryDependencies += Deps.utest % "test"
  )

  implicit class ProjectOps(val project: Project) extends AnyVal {
    def underModules: Project = {
      val base = project.base.getParentFile / "modules" / project.base.getName
      project.in(base)
    }
    def underScala: Project = {
      val base = project.base.getParentFile / "modules" / "scala" / project.base.getName
      project.in(base)
    }
    def underShared: Project = {
      val base = project.base.getParentFile / "modules" / "shared" / project.base.getName
      project.in(base)
    }
  }

  lazy val exportVersions = taskKey[String]("Prints the current version to a dedicated file under target/")

  lazy val exportVersionsSetting: Setting[_] = {
    exportVersions := {
      val ver = version.value
      val ammoniteVer = Deps.Versions.ammonite
      val scalaVer = scalaVersion.value
      val outputDir = target.value

      outputDir.mkdirs()

      val output = outputDir / "version"
      Files.write(output.toPath, ver.getBytes(UTF_8))
      state.value.log.info(s"Wrote $output")

      val ammoniteOutput = outputDir / "ammonite-version"
      Files.write(ammoniteOutput.toPath, ammoniteVer.getBytes(UTF_8))
      state.value.log.info(s"Wrote $ammoniteOutput")

      val scalaOutput = outputDir / "scala-version"
      Files.write(scalaOutput.toPath, scalaVer.getBytes(UTF_8))
      state.value.log.info(s"Wrote $scalaOutput")

      ver
    }
  }

}
