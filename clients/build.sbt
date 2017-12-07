libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.1.3",
  "com.typesafe" % "config" % "1.3.1"
)

mainClass in (Compile, run) := Some("sonnen.clients.Main")
