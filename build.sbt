import sbt.Tests.Setup

name := "main"

version := "1.0"

organization := "org.heuriqo"

scalaVersion := "2.10.3"

resolvers += "Tim Tennant's repo" at "http://dl.bintray.com/timt/repo/"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "io.shaka" %% "naive-http-server" % "26"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.1" % "test"

libraryDependencies += "org.scalamock" % "scalamock-scalatest-support_2.10" % "3.1.4"

libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test"

testOptions += Setup( cl =>
   cl.loadClass("org.slf4j.LoggerFactory").
     getMethod("getLogger",cl.loadClass("java.lang.String")).
     invoke(null,"ROOT")
)