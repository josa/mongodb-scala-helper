import sbt._

import reaktor.scct.ScctProject

class MongodbScalaHelper(info: ProjectInfo) extends DefaultProject(info) with ScctProject {

  val scalatoolsSnapshot = ScalaToolsSnapshots

  override def libraryDependencies = Set(
    "junit" % "junit" % "4.5" % "test->default"
  ) ++ super.libraryDependencies

  val scalatest = "org.scalatest" % "scalatest" %
    "1.2.1-SNAPSHOT"

  val slf4j = "org.slf4j" % "slf4j-api" % "1.6.1"
  val l4jbind = "org.slf4j" % "slf4j-log4j12" % "1.6.1" % "runtime"

  val casbah = "com.mongodb.casbah" % "casbah_2.8.0" % "2.0.1"

}