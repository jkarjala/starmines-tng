/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

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