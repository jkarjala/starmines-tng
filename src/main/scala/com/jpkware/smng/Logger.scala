package com.jpkware.smng

import scala.scalajs.js
import js.Dynamic.{ global => g }

object Logger {
  def info(s: String): Unit = g.console.info(s)
  def warn(s: String): Unit = g.console.warn(s)
}