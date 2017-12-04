lazy val commonSettings = Seq(
  organization := "sonnen",
  version := "1.0.0-SNAPSHOT",
  scalaVersion := "2.12.4"
)

lazy val common = project.settings(commonSettings)

lazy val server = project.dependsOn(common)
  .settings(commonSettings)
  .enablePlugins(PlayScala)


lazy val clients = project.settings(commonSettings)
  .dependsOn(common)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .aggregate(server, clients, common)