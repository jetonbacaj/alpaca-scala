name := "alpaca-scala"

version := "4.0.0"

// POM settings for Sonatype
import xerial.sbt.Sonatype._
organization := "com.cynance"
homepage := Some(url("https://github.com/cynance/alpaca-scala"))
sonatypeProjectHosting := Some(GitHubHosting("cynance", "alpaca-scala", "devs@cynance.com"))
scmInfo := Some(ScmInfo(url("https://github.com/cynance/alpaca-scala"),"git@github.com:cynance/alpaca-scala.git"))
developers := List(Developer("cynance",
  "cynance",
  "devs@cynance.com",
  url("https://github.com/cynance")))
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true

//enablePlugins(MicrositesPlugin)

// Add sonatype repository settings
publishTo := sonatypePublishTo.value

scalaVersion := "2.13.4"

val circeVersion = "0.13.0"
val hammockVersion = "0.11.0"

libraryDependencies ++= Seq(
//  "com.pepegar" % "hammock-core_2.12" % hammockVersion,
//  "com.pepegar" % "hammock-circe_2.12" % hammockVersion,

  "com.github.pureconfig" %% "pureconfig" % "0.14.0",

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  "org.scalactic" %% "scalactic" % "3.2.2",
  "org.scalatest" %% "scalatest" % "3.2.2" % "test",
  "org.scalamock" %% "scalamock" % "5.0.0" % Test,

  "com.typesafe.akka" %% "akka-http"   % "10.2.1",
  "com.typesafe.akka" %% "akka-stream" % "2.6.10", // or whatever the latest version is,

  "io.nats" % "jnats" % "2.8.0",

  "org.typelevel" %% "cats-core" % "2.2.0",

  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",

  "com.softwaremill.macwire" %% "macros" % "2.3.7" % "provided",
  "com.softwaremill.macwire" %% "macrosakka" % "2.3.7" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.3.7",
  "com.softwaremill.macwire" %% "proxy" % "2.3.7",

  "com.beachape" %% "enumeratum" % "1.6.1",
  "com.beachape" %% "enumeratum-circe" % "1.6.1",
  "org.mockito" %% "mockito-scala-scalatest" % "1.16.0",
  "com.softwaremill.sttp.client3" %% "akka-http-backend"      % "3.0.0-RC7",
  "com.softwaremill.sttp.client3" %% "circe" % "3.0.0-RC7"


)

sourceGenerators in Test += Def.task {
  val file = (sourceManaged in Test).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
  Seq(file)
}.taskValue

coverageExcludedPackages := ".*ConfigService.*;.*Config.*;alpaca\\.client\\..*"

//coverageEnabled := true


//Microsite details
//micrositeName := "Alpaca Scala"
//micrositeDescription := "A Scala library for alpaca.markets"
//micrositeAuthor := "Cynance"
//micrositeBaseUrl := "/alpaca-scala"
//micrositeDocumentationUrl := "/alpaca-scala/docs"
//
//micrositePalette := Map(
//  "brand-primary"     -> "#000",
//  "brand-secondary"   -> "#000",
//  "brand-tertiary"    -> "#fcd600",
//  "gray-dark"         -> "#453E46",
//  "gray"              -> "#837F84",
//  "gray-light"        -> "#E3E2E3",
//  "gray-lighter"      -> "#F4F3F4",
//  "white-color"       -> "#FFFFFF")
//
//micrositePushSiteWith := GitHub4s
//
//micrositeGithubToken := sys.env.get("GITHUB_TOKEN")
//
//micrositeGithubOwner := "cynance"
//micrositeGithubRepo := "alpaca-scala"