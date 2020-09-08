name := "LFS_code_test"

version := "0.1"

scalaVersion := "2.13.3"

mainClass in (Compile, run) := Some("lfs.fixedwidth2delimited.FixedWidth2Delimited")

libraryDependencies ++= Seq(
  "com.typesafe.play"          %% "play-json"     % "2.9.0",
  "org.rogach"                 %% "scallop"       % "3.5.1",
  "org.slf4j"                  %  "slf4j-simple"  % "1.7.5",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.scalatest"              %% "scalatest"     % "3.1.1" % Test,
)
