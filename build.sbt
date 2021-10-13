import Dependencies._

lazy val scala211 = "2.11.12"
lazy val scala212 = "2.12.15"
lazy val scala213 = "2.13.6"
lazy val supportedScalaVersions = List(scala213, scala212, scala211)


ThisBuild / scalaVersion     := scala213
ThisBuild / organization     := "com.sixtysevenbricks.text.languagedetection"
ThisBuild / organizationName := "languagedetection"

githubOwner := "danielrendall"
githubRepository := "language-detection"
githubTokenSource := TokenSource.Environment("GITHUB_TOKEN")
releaseCrossBuild := true

lazy val root = (project in file("."))
.settings(
    name := "LanguageDetection",
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      specs2 % Test
    )
)
