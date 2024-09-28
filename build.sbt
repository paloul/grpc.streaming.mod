ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

val akkaVersion = "2.9.5"
lazy val akkaGrpcVersion = sys.props.getOrElse("akka-grpc.version", "2.4.3")

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

enablePlugins(AkkaGrpcPlugin)
enablePlugins(JavaAppPackaging)

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

lazy val root = (project in file("."))
  .settings(
    name := "groundstation-commandserver",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
      "com.typesafe.akka" %% "akka-pki" % akkaVersion,

      "ch.qos.logback" % "logback-classic" % "1.5.6"
    )
  )
