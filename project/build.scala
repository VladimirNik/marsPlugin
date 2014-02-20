import sbt._
import Keys._
import Configurations.CompilerPlugin

object build extends Build {
  val marsPluginName = "marsplugin"

  val buildSettings = Defaults.defaultSettings ++
    Seq(
      organization := "org.scala-lang.plugins",
      version := "0.1.0",
      scalaVersion := "2.11.0-M8"
    )

    val marsPlugin = Project(marsPluginName, file("."),
      settings = buildSettings ++ 
        Seq(
	  name := marsPluginName,
          libraryDependencies <++= scalaVersion apply dependencies
        )
    ) 

    def dependencies(sv: String) = Seq(
      "org.scala-lang" % "scala-compiler" % sv
    )
}
