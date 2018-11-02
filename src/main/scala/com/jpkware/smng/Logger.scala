package com.jpkware.smng

import scala.scalajs.js
import js.Dynamic.{ global => g }

object Logger {
  var messages: Messages = null
  def info(s: String): Unit = {
    if (messages!=null) messages.show(s)
    g.console.info(s)
  }
  def warn(s: String): Unit = {
    if (messages!=null) messages.show(s)
    g.console.warn(s)
  }
}