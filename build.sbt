lazy val commonSettings = Seq(
  organization := "sonnen",
  version := "1.0.0-SNAPSHOT",
  scalaVersion := "2.12.4"
)


lazy val server = project.settings(commonSettings)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .aggregate(server)