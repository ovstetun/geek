

package no.mesan.geek
package snippet

import net.liftweb.util._
import Helpers._

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import lib.DependencyFactory

class HelloWorldTest extends FunSuite with ShouldMatchers {
  val stableTime = now

  test("Should place time in the correct place") {
    DependencyFactory.time.doWith(stableTime) {
      val hello = new HelloWorld
      Thread.sleep(1000)

      val str = hello.howdy(<span>Hello at <span class="time"/></span>).toString
      printf(str)

      str.indexOf(stableTime.toString) should  be >= 0
      str.indexOf("Hello at") should  be >= 0
    }
  }
}
