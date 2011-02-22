
import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {
  val scalatoolsRelease = "Scala Tools Snapshot" at "http://scala-tools.org/repo-releases/"

  val liftVersion = "2.3-M1"
  
  def lift(name:String) = "net.liftweb" %% ("lift-" + name) % liftVersion withSources


  override def scanDirectories = Nil
  override def jettyWebappPath = webappPath

  override def libraryDependencies = Set(
    lift("util"),
    lift("webkit"),
    lift("testkit"),
    lift("wizard"),
    lift("mapper"),
    lift("actor"),

    "com.h2database" % "h2" % "1.2.138",

    "ch.qos.logback" % "logback-classic" % "0.9.26",

    "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default",
    "junit" % "junit" % "4.5" % "test->default",
    "org.scalatest" % "scalatest" % "1.2" % "test->default" withSources,
    "org.scala-tools.testing" %% "specs" % "1.6.6" % "test->default" withSources
  ) ++ super.libraryDependencies
}
