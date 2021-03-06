

package no.mesan.geek {
package snippet {

import scala.xml.{NodeSeq, Text}
import net.liftweb._
import util._
import common._
import java.util.Date
import no.mesan.geek.lib._
import Helpers._

class HelloWorld {
  lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date

  def howdy(in: NodeSeq): NodeSeq =
    (".time" #> date.map(d => d.toString)) (in)
}

}
}
