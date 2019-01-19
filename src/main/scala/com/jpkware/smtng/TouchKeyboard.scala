/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser._
import org.scalajs.dom

import scala.collection.mutable
import scala.scalajs.js

class TouchKeyboard(game: Game, x:Double, y:Double, limit: Int, default: String, entered: (String) => Unit) {
  val keyboardButtons: Group = game.add.group()
  val lowerButtons: Group = game.add.group()
  val upperButtons: Group = game.add.group()

  val lowerKeys = Seq("1234567890-", "qwertyuiop()", "asdfghjkl;'", "zxcvbnm,.! ")
  val upperKeys = Seq("1234567890+", "QWERTYUIOP*/", "ASDFGHJKL:@", "ZXCVBNM<>?_")
  val step = 100
  val text: mutable.Buffer[Char] = mutable.Buffer()
  var shifted = true
  var charAdded = false

  val sfxTick = game.add.audio(StateNextLevel.SfxTick)

  lowerButtons.visible = false
  addKeys(x,y+step,lowerKeys(0), lowerButtons)
  addKeys(x+20,y+step*2,lowerKeys(1), lowerButtons)
  addKeys(x+40,y+step*3,lowerKeys(2), lowerButtons)
  addKeys(x+60,y+step*4,lowerKeys(3), lowerButtons)

  addKeys(x,y+step,upperKeys(0), upperButtons)
  addKeys(x+20,y+step*2,upperKeys(1), upperButtons)
  addKeys(x+40,y+step*3,upperKeys(2), upperButtons)
  addKeys(x+60,y+step*4,upperKeys(3), upperButtons)

  private val backspace = PhaserButton.add(game, x+lowerKeys(0).length*step,y+step, "Del", group = keyboardButtons, scale = 0.75, alpha = 1.0)
  backspace.events.onInputUp.add(() => delChar(), null, 1)

  private val shift = PhaserButton.add(game, x-step+60,y+step*4, "Shift", group = keyboardButtons, scale = 0.75, alpha = 1.0)
  shift.events.onInputUp.add(() => toggleShift(), null, 1)

  private val okButton = PhaserButton.add(game, x+120+step*lowerKeys(2).length, y+step*3.5, "OK", group = keyboardButtons, scale = 1.5, alpha = 1.0)
  okButton.events.onInputUp.add(() => enter(), null, 1)
  if (default.isEmpty) okButton.visible = false else text.appendAll(default)

  val output: BitmapText = game.add.bitmapText(x,y,GlobalRes.FontId, text.mkString(""), 48, keyboardButtons)
  output.anchor.set(0.0,0.5)

  def addKeys(x: Double, y: Double, keys:String, group: Group): Unit = {
    keys.zipWithIndex.foreach { case (ch, i) => {
      val button = PhaserButton.add(game, x+i*step,y, ch.toString, group = group, scale = 0.75)
      button.events.onInputUp.add(() => addChar(ch), null, 1)
    }}
  }

  def clearDefault: Boolean = !charAdded && text.mkString("")==default

  def addChar(ch: Char): Unit = {
    if (text.length<limit) {
      if (clearDefault) text.clear()
      text.append(ch)
      charAdded = true
      setShift(ch == '.')
      output.setText(text.mkString(""))
    }
    sfxTick.play()
    okButton.visible = text.nonEmpty
  }

  def delChar(): Unit = {
    if (text.nonEmpty) {
      if (clearDefault) text.clear() else {
        text.remove(text.length - 1)
        if (text.length == 0 || text.last=='.') setShift(true) else setShift(false)
      }
    }
    sfxTick.play()
    okButton.visible = text.nonEmpty
    output.setText(text.mkString(""))
  }

  def setShift(v: Boolean): Unit = {
    shifted = v
    lowerButtons.visible = !shifted
    upperButtons.visible = shifted
  }
  def toggleShift(): Unit = {
    setShift(!shifted)
  }

  def enter(): Unit = {
    lowerButtons.destroy()
    upperButtons.destroy()
    keyboardButtons.destroy()
    entered(text.mkString(""))
  }
}
