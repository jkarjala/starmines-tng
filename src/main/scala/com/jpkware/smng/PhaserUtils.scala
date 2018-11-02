package com.jpkware.smng

import com.definitelyscala.phaser._

object PhaserKeys {
  def isFireDown(game: Game): Boolean = {
    val k = game.input.keyboard
    k.isDown(0x0D) || k.isDown(' ') || k.isDown('M')
  }
}

object PhaserButton {
  def add(game: Game, x: Double, y: Double, text: String, alpha: Double = 0.75, group: Group = null, scale: Double = 2): Button = {
    val button = game.add.button(x,y, GlobalRes.ButtonId, null, null, 0, 1, 0, 1)
    button.scale.set(scale,scale)
    button.anchor.set(0.5,0.5)

    val t: BitmapText = game.add.bitmapText(x, y, GlobalRes.FontId, text, 24 * scale)
    t.align = "center"
    t.anchor.set(0.5, 0.5)
    t.alpha = alpha
    if (group!=null) {
      group.add(button)
      group.add(t)
    }
    button
  }
}