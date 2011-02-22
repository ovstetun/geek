package no.mesan.geek
package snippet

import net.liftweb.http.SHtml
import comet.ChatServer
import net.liftweb.http.js.JsCmds.SetValById

object ChatIn {
  def render = SHtml.onSubmit(s => {
    ChatServer ! s
    SetValById("chat_in", "")
  })
}