libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.1.3"
)

mainClass in (Compile, run) := Some("sonnen.clients.Main")
