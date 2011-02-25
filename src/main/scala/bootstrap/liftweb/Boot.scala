

package bootstrap.liftweb

import net.liftweb._
import common.{Empty, Full}
import http._
import sitemap.Loc._
import sitemap.{**, SiteMap, Menu, Loc}
import util.{ NamedPF }
import mapper.{Schemifier, DB, StandardDBVendor, DefaultConnectionIdentifier}
import util.{Props}
import no.mesan.geek.model._
import no.mesan.geek.snippet._

class Boot {
  def boot {
  
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
        			               Props.get("db.url") openOr 
        			               "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
        			               Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User)

  
    // where to search snippet
    LiftRules.addToPackages("no.mesan.geek")

    // build sitemap
    def sitemap() = SiteMap (
      Menu("Home") / "index" submenus (
        Menu("test") / "lala"
      ),
      Menu("Chat") / "chat",
      Menu("Recurse") / "recurse" / "one" submenus (
        Menu.param[Which]("Recurse", "Recurse",
                          {case "one" => Full(First())
                           case "two" => Full(Second())
                           case "both" => Full(Both())
                           case _ => Empty},
                          w => w.toString) / "recurse"
      ),
      Menu("Static") / "static" / ** >> User.AddUserMenusAfter
    )

    LiftRules.uriNotFound.prepend(NamedPF("404handler") {
      case (req, failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })

    LiftRules.setSiteMapFunc(() => User.sitemapMutator(sitemap()))

    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    LiftRules.ajaxEnd   = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Use HTML5
    LiftRules.htmlProperties.default.set((r: Req) =>  new Html5Properties(r.userAgent))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
  }
}