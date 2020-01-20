/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2020 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser._

class PathWorm(game: Game, rule: Rule, group: Group) extends PathMine(game, rule, group) {
  val count: Int = rule.args(1).toInt

  override def reset(x: Double, y: Double, health: Double = 1): Sprite = {
    this.x = x
    this.y = y
    animation.setFrame(rule.shape + "01")
    (1 to count).foreach(i => {
      val tailRule = rule.copy(args = Seq(s"i"*(i*5) + rule.args.head))
      val b = new PathMine(game, tailRule, group)
      b.x = x
      b.y = y
      b.animation.setFrame(rule.shape + "01")
      b.physBody.velocity.set(physBody.velocity.x, physBody.velocity.y)
    })
    this
  }
}

object PathWorm {
  def spawn(p: SpawnParams) : Enemy = new PathWorm(p.game, p.rule, p.group)
}