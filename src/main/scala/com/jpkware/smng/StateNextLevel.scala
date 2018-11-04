package com.jpkware.smng

import com.definitelyscala.phaser.{BitmapText, Game, Sound, State}
import org.scalajs.dom.raw.Element

import scala.scalajs.js

class StateNextLevel(game: Game, options: Map[String,String], status: Element) extends State {

  var keyDown: Boolean = _
  var result: String = _
  var text: BitmapText = _
  var sfxTick: Sound = _

  override def init(args: js.Any*): Unit = {
    args.headOption match {
      case str: Some[js.Any] =>
        Logger.info(s"init ${str.get}")
        result = str.get.asInstanceOf[String]
      case _ => Logger.info(s"Unknown args: $args")
    }
  }

  override def create(): Unit = {

    val button = PhaserButton.add(game, game.width/2,game.height-200, "Next")
    button.events.onInputUp.add(gotoPlay _, null, 1)
    keyDown = PhaserKeys.isFireDown(game)

    text = game.add.bitmapText(game.width/2,300, GlobalRes.FontId, "Collecting time bonus...", 64)
    text.anchor.set(0.5,0.5)

    sfxTick = game.add.audio(StateNextLevel.SfxTick)

    if (StatePlay.scores.timeBonus>0) {
      val timer = game.time.create(true)
      timer.loop(150, () => {
        if (StatePlay.scores.timeBonus>2000)
          StatePlay.scorebox.addToScore((2000).toInt)
        else
          StatePlay.scorebox.addToScore((StatePlay.scores.timeBonus).toInt)
        StatePlay.scorebox.addToTimeBonus((-2000).toInt)
        sfxTick.play()
        if (StatePlay.scores.timeBonus==0) timer.stop(true)
      }, null)
      timer.start(0)
    }

  }

  override def update(): Unit = {
    if (StatePlay.scores.timeBonus>0) return
    else text.text = result

    if (keyDown) keyDown = !PhaserKeys.isFireDown(game)
    if (!keyDown && PhaserKeys.isFireDown(game)) gotoPlay()
  }

  def gotoPlay(): Unit = {
    game.state.start("play", args = "nextlevel", clearCache = false, clearWorld = true)
  }

}

object StateNextLevel {
  def SfxTick = "sfx:tick"

  def preloadResources(game: Game): Unit = {
    game.load.audio(SfxTick, "res/tick.wav")
  }
}