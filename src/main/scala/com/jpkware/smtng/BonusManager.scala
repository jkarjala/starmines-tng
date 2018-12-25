/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser._

class BonusoidContainer(manager: BonusManager, game: Game, x: Double, y: Double)
  extends Sprite(game, x,y, "sprites", "bonusoid01") {
  game.physics.arcade.enable(this)
  anchor.set(0.5,0.5)
  animations.add("bonusoidrotate", Animation.generateFrameNames("bonusoid", 1, 36, "", 2))
  animations.play("bonusoidrotate", 9, true)

  override def kill(): Sprite = {
    val v = 150
    val directions: Array[Point] = Array(new Point(v,v),new Point(-v,v),new Point(v,-v),new Point(-v,-v))
    (0 to 3).foreach(i => {
      val b = manager.bonusoids.getFirstDead()
      b match {
        case b: Bonusoid =>
          b.reset(position.x, position.y, 1)
          b.body match {
            case body: Body => body.velocity = directions(i)
          }
        case _ => Logger.warn("No dead bonusItem??")
      }
    })
    super.kill()
  }
}

class Bonusoid(game: Game, x: Double, y: Double)
  extends Sprite(game, x,y, "sprites", "bonus01") {

  game.physics.arcade.enable(this)
  body match {
    case body: Body =>
      body.collideWorldBounds = true
      body.bounce.set(1,1)
  }

  animations.add("bonusrotate", Animation.generateFrameNames("bonus", 1, 36, "", 2))
  animations.play("bonusrotate", 9, true)
}

class BonusManager(game: Game, containerCount: Int, randomSafePosition: (Sprite) => Unit) {
  val containers: Group = game.add.group()
  val bonusoids: Group = game.add.group()
  val bonusoidCount = containerCount*4

  (1 to containerCount).foreach(i => {
    val b = new BonusoidContainer(this, game, 0, 0)
    randomSafePosition(b)
    containers.add(b)
  })

  (1 to bonusoidCount).foreach(i => {
    val b = new Bonusoid(game, 0, 0)
    b.kill()
    bonusoids.add(b)
  })

  def allDead: Boolean = containers.countLiving()==0 && bonusoids.countLiving()==0
}