/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2020 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{BitmapText, Game, Timer}

import scala.collection.mutable

class Messages(game: Game) {
  var limit: Int = 5
  var fontSize = 40
  private val texts = mutable.ArrayBuffer[BitmapText]()
  private val timer: Timer = game.time.create(false)

  timer.loop(100, tick _, null)
  timer.start()

  def show(msg: String): Unit = {
    if (texts.length==limit) {
      texts(0).destroy()
      texts.remove(0)
    }
    texts.foreach(_.position.y -= fontSize+2)
    val text: BitmapText = game.add.bitmapText(game.width / 2, 300, GlobalRes.FontId, msg, fontSize)
    text.anchor.set(0.5, 0.5)
    texts += text
  }

  def tick(): Unit = {
    val toDelete: mutable.Seq[BitmapText] = texts.flatMap {text =>
        text.alpha -= 0.02
        if (text.alpha<0) {
          text.destroy()
          Some(text)
        }
        None
    }
    toDelete.foreach(text => texts -= text)
  }

  def clear(): Unit = {
    texts.foreach(_.destroy())
    texts.clear()
  }
}
