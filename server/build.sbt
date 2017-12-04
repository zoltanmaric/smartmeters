resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq(
//  jdbc,
  ehcache,
  ws,
  specs2 % Test,
  guice,
  "com.typesafe.play" %% "play-slick" % "3.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.1",
  "org.postgresql" % "postgresql" % "42.0.0"
)

unmanagedResourceDirectories in Test +=  baseDirectory ( _ /"target/web/public/test" ).value

      