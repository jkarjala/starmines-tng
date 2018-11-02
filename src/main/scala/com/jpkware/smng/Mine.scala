package com.jpkware.smng

import com.definitelyscala.phaser.{Game, Sprite}

class Mine(game: Game, x: Double, y: Double)
  extends PreRotatedSprite(game, x,y, "sprites", "mine", 18) {
  game.physics.arcade.enable(this)
  physBody.maxVelocity.set(500,500)
  physBody.collideWorldBounds = true
  physBody.bounce.set(1,1)

  override def reset(x: Double, y: Double, health: Double = 1): Sprite = {
    super.reset(x, y, health)
    physBody.velocity.set(20-StarMinesNG.rnd.nextDouble()*40, 10-StarMinesNG.rnd.nextDouble()*20)
    rotationSpeed = if (StarMinesNG.rnd.nextInt(2)==1) scala.math.Pi * 2 else -scala.math.Pi * 2
    selectFrame()
    this
  }
}

object Mine {
  def spawn(game: Game, level: Rule) = new Mine(game, 0,0)
}