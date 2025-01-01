name := "contest-alarm"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
	"com.softwaremill.sttp.client3"	%% "core" 			% "3.8.6",
	"com.softwaremill.sttp.client3" %% "circe"			% "3.8.6",
	"io.circe"						%% "circe-core"		% "0.14.6",
	"io.circe"						%% "circe-generic"	% "0.14.6",
	"io.circe"						%% "circe-parser"	% "0.14.6",
	"org.scalatest"					%% "scalatest"		% "3.2.16"  % Test,
	"javax.mail"					% "mail"			% "1.4.5",
)

scalacOptions ++= Seq(
	"-deprecation",
	"-feature",
	"-unchecked",
	"-Xlint",
	"-Ywarn-dead-code",
)

testFrameworks := Seq(new TestFramework("org.scalatest.tools.Framework"))

