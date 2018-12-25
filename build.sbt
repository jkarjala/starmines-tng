/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
name := "starmines-tng"

version := "0.1"

scalaVersion := "2.12.2"

resolvers += Resolver.jcenterRepo

enablePlugins(ScalaJSPlugin)
enablePlugins(WorkbenchPlugin)

workbenchDefaultRootObject := Some(("target/scala-2.12/classes/index-dev.html", "target/scala-2.12/"))

name := "StarMines the Next Generation"

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.6",
  "org.scala-js" %%% "scalajs-java-logging" % "0.1.5",
  "com.definitelyscala" %%% "scala-js-phaser" % "1.0.2",
  "com.definitelyscala" %%% "scala-js-phaserpixi" % "1.0.2",
  "com.definitelyscala" %%% "scala-js-phaserp2" % "1.0.2"
)
