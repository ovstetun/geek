package no.mesan.geek
package comet

import net.liftweb._
import http._
import actor._
import util._
import util.Helpers._
import js.jquery.JqJsCmds._
import js._
import JsCmds._
import java.util.Date
import xml.{Text, NodeSeq}

final case class Message(msg:String, when:Date = now, guid:String = nextFuncName)
final case class Remove(guid:String)


object ChatServer extends LiftActor with ListenerManager {
  private var messages = Vector(Message("Welcome"))

  def createUpdate = messages

  override protected def lowPriority = {
    case s:String => {
      val m = Message(s)
      messages :+= m
      updateListeners(m -> messages)
    }
    case r @ Remove(guid) => {
      messages = messages.filterNot(_.guid == guid)
      updateListeners(r -> messages)
    }
  }
}

class Chat extends CometActor with CometListener {
  private var msgs: Vector[Message] = Vector()

  def registerWith = ChatServer

  override def lowPriority = {
    case (Remove(guid), v:Vector[Message]) => {
      msgs = v
      partialUpdate(
        FadeOut(guid, TimeSpan(0), TimeSpan(500)) &
        After(TimeSpan(500), Replace(guid, NodeSeq.Empty))
      )
    }
    case (m:Message, v:Vector[Message]) => {
      msgs = v
      partialUpdate(
        AppendHtml("ul_dude", doLine(m)(("li ^^" #> "^^")(defaultXml))) &
        Hide(m.guid) &
        FadeIn(m.guid, TimeSpan(0), TimeSpan(500))
      )
    }
    case v: Vector[Message] => msgs = v; reRender()
  }

  def render = {
    "ul [id]" #> "ul_dude" &
    "li" #> msgs.map(doLine) & ClearClearable
  }

  def doLine(m:Message)(html:NodeSeq) = {
    ("li [id]" #> m.guid &
    "li *" #> (Text(m.msg + " ") ++
               SHtml.ajaxButton("delete", () => {
                 ChatServer ! Remove(m.guid)
                 Noop
               }))) (html)
  }
}

