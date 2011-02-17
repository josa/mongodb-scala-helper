import sbt._

class MongodbScalaHelper(info: ProjectInfo) extends DefaultProject(info) {

  val scalatoolsSnapshot = ScalaToolsSnapshots

  override def libraryDependencies = Set(
    "junit" % "junit" % "4.5" % "test->default"
    ) ++ super.libraryDependencies

  val scalatest = "org.scalatest" % "scalatest" %
          "1.2.1-SNAPSHOT"

  val bumRels = "bum-releases" at "http://repo.bumnetworks.com/releases"
  val bumSnaps = "bum-snapshots" at "http://repo.bumnetworks.com/snapshots"

  val casbah = "com.mongodb.casbah" % "casbah_2.8.0" % "2.0.1"

}