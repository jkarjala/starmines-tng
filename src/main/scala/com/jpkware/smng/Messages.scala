package com.jpkware.smng

import com.definitelyscala.phaser.{BitmapText, Game, Timer}

import scala.collection.mutable

class Messages(game: Game) {
  val Limit: Int = 5
  private val texts = mutable.ArrayBuffer[BitmapText]()
  private val timer: Timer = game.time.create(false)

  timer.loop(100, tick _, null)
  timer.start()

  def show(msg: String): Unit = {
    if (texts.length==Limit) {
      texts(0).destroy()
      texts.remove(0)
    }
    texts.foreach(_.position.y -= 50)
    val text: BitmapText = game.add.bitmapText(game.width / 2, 300, "font", msg, 48)
    text.anchor.set(0.5, 0.5)
    texts += text
  }

  def tick(): Unit = {
    val toDelete: mutable.Seq[BitmapText] = texts.flatMap {text =>
        text.alpha -= 0.025
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
