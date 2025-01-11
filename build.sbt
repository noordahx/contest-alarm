name := "contest-alarm"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.12"

lazy val root = (project in file("."))
	.settings(
		libraryDependencies ++= Seq(
			// Web server
			"com.typesafe.akka" %% "akka-http" % "10.2.10",
			"com.typesafe.akka" %% "akka-stream" % "2.6.20",
			"com.typesafe.akka" %% "akka-http-spray-json" % "10.2.10",
			
			// Emails
			"com.github.daddykotex" %% "courier" % "3.2.0",
			"com.softwaremill.sttp.client3"	%% "core" % "3.8.6",
			"com.softwaremill.sttp.client3" %% "circe" % "3.8.6",
			"io.circe" %% "circe-core" % "0.14.6",
			"io.circe" %% "circe-generic" % "0.14.6",
			"io.circe" %% "circe-parser" % "0.14.6",
			"org.scalatest" %% "scalatest" % "3.2.16" % Test,
		)

	)

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Releases"  at "https://oss.sonatype.org/content/repositories/releases"
)
