/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2020 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{Game, Group, Sprite}
import scala.math.min

case class SpawnParams(game: Game, rule: Rule, group: Group, enemyMissiles: Group, player: Player)

case class Rule
(
  spawn: SpawnParams => Enemy, // spawn function
  shape: String, // sprite shape name prefix
  frames: Int,   // sprite animation frame count
  fps: Int,      // animation fpx
  score: Int,    // score increment when killed
  minL: Int = 1, // first level
  maxL: Int = 1, // last level
  modL: Int = 1, // (level-minL) % mod must be 0
  minC: Int = 1, // min count
  maxC: Int = 1, // max count
  div: Int = 1,  // count is (level-minL)/div + minC
  spd: Int = 50, // speed limit, used for different purposes in different enemies
  args: Seq[String] = Seq() // Additional arguments
)

class EnemyManager(game: Game, randomSafePosition: Sprite => Unit) {
  val MAX = 100000
  val rules = Seq(
    Rule(Enemy.spawn, "mine",  18, 9,  125, minL=1, maxL=MAX, modL=1, minC=4, maxC=10, div=1, spd=40),
    Rule(PathMine.spawn, "torusa", 32, 18, 150, minL=2, maxL=9, modL=1, minC=2, maxC=6, div=1, spd=80, Seq("3ALz9i9i")),
    Rule(PathMine.spawn, "torusb", 32, 18, 150, minL=10, maxL=19, modL=1, minC=2, maxC=4, div=1, spd=80, Seq("5ALx9ix9iax9ix9id")),
    Rule(PathMine.spawn, "torusc", 32, 18, 150, minL=20, maxL=29, modL=1, minC=2, maxC=4, div=1, spd=80, Seq("8ALz9iz9iz9iz9iz9iz9iaz9iz9iz9iz9iz9iz9id")),

    Rule(PathWorm.spawn, "balla", 32, 12, 250, minL=15, maxL=19, modL=1, minC=1, maxC=4, div=1, spd=50, Seq("6ALx9ix9iz9i", "2")),
    Rule(PathWorm.spawn, "ballb", 32, 12, 250, minL=20, maxL=24, modL=1, minC=2, maxC=4, div=1, spd=60, Seq("6ALz9i2z9i9ix", "2")),
    Rule(PathWorm.spawn, "ballc", 32, 12, 250, minL=25, maxL=29, modL=1, minC=3, maxC=4, div=1, spd=70, Seq("6AL9ix9ix9iz", "3")),

    Rule(PathWorm.spawn, "balla", 32, 12, 250, minL=30, maxL=39, modL=1, minC=3, maxC=4, div=1, spd=60, Seq("6ALx9ix9iz9i", "4")),
    Rule(PathWorm.spawn, "ballb", 32, 12, 250, minL=40, maxL=49, modL=1, minC=3, maxC=4, div=1, spd=70, Seq("6ALz9i2z9i9ix", "4")),
    Rule(PathWorm.spawn, "ballc", 32, 12, 250, minL=50, maxL=59, modL=1, minC=3, maxC=4, div=1, spd=80, Seq("6AL9ix9ix9iz", "4")),

    Rule(PathWorm.spawn, "balla", 32, 12, 250, minL=60, maxL=80, modL=1, minC=2, maxC=4, div=5, spd=60, Seq("6ALx9ix9iz9i", "5")),
    Rule(PathWorm.spawn, "ballb", 32, 12, 250, minL=75, maxL=100, modL=1, minC=2, maxC=4, div=5, spd=80, Seq("6ALz9i2z9i9ix", "5")),
    Rule(PathWorm.spawn, "ballc", 32, 12, 250, minL=95, maxL=MAX, modL=1, minC=2, maxC=4, div=5, spd=90, Seq("6AL9ix9ix9iz", "6")),

    Rule(XYShooter.spawn, "xshoot",  32, 8, 500, minL=8, maxL=19, modL=2, minC=1, maxC=5, div=4, spd=80, Seq("1","0", "700")),
    Rule(XYShooter.spawn, "yshoot",  32, 8, 500, minL=9, maxL=20, modL=2, minC=1, maxC=5, div=4, spd=80, Seq("0","1", "700")),

    Rule(XYShooter.spawn, "xshoot",  32, 8, 500, minL=21, maxL=MAX, modL=1, minC=1, maxC=4, div=4, spd=100, Seq("1","0", "800")),
    Rule(XYShooter.spawn, "yshoot",  32, 8, 500, minL=21, maxL=MAX, modL=1, minC=1, maxC=4, div=4, spd=100, Seq("0","1", "800")),

    Rule(Splitter.spawn, "2atom", 16, 12, 400, minL=10, maxL=40, modL=3, minC=2, maxC=5, div=1, spd=60, Seq("1atom","16","12","2")),
    Rule(Splitter.spawn, "2atomb", 16, 12, 400, minL=11, maxL=41, modL=3, minC=2, maxC=5, div=1, spd=60, Seq("1atom","16","12","2")),
    Rule(Splitter.spawn, "2atomc", 24, 12, 400, minL=12, maxL=42, modL=3, minC=2, maxC=5, div=1, spd=60, Seq("1atom","16","12","2")),

    Rule(Splitter.spawn, "4atom", 16, 12, 400, minL=30, maxL=MAX, modL=3, minC=1, maxC=5, div=3, spd=60, Seq("1atom","16","12","4")),
    Rule(Splitter.spawn, "4atomb", 16, 12, 400, minL=31, maxL=MAX, modL=3, minC=1, maxC=5, div=3, spd=60, Seq("1atom","16","12","4")),
    Rule(Splitter.spawn, "4atomc", 16, 12, 400, minL=32, maxL=MAX, modL=3, minC=1, maxC=5, div=3, spd=60, Seq("1atom","16","12","4")),

    Rule(Producer.spawn, "4boxa", 16, 16, 1000, minL=20, maxL=70, modL=1, minC=1, maxC=5, div=10, spd=10, Seq("1boxa","16","12","4","20000")),
    Rule(Producer.spawn, "4boxb", 16, 24, 1000, minL=71, maxL=MAX, modL=1, minC=2, maxC=5, div=10, spd=20, Seq("1boxb","16","12","4", "10000")),
    Rule(null,"",0,0,0,0,0,0,0,0,0,0)
  )

  def spawnEnemies(player: Player, level: Int, count: Int): (Group, Group) = {
    val enemies = game.add.group(name = "enemies")
    val enemyMissiles = game.add.group(name = "enemyMissiles") // these will be collision checked against other enemies

    if (count>0) {
      // Test with first rule only, and fixed count
      val rule = rules.head
      spawnEnemies(player, enemies, enemyMissiles, count, rule)
    }
    else {
      rules.foreach(rule => {
        if (rule.spawn!=null
          && level >= rule.minL
          && level <= rule.maxL
          && ((level-rule.minL) % rule.modL)==0) {
          // Rule matched this level, spawn enemies
          val enemyCount = min((level-rule.minL)/rule.div + rule.minC, rule.maxC)
          Logger.info(s"${rule.shape} $enemyCount")
          spawnEnemies(player, enemies, enemyMissiles, enemyCount, rule)
        }
      })
    }
    Logger.info(s"Live enemies: ${enemies.countLiving()}")
    (enemies, enemyMissiles)
  }

  def spawnEnemies(player: Player, group: Group, enemyMissiles: Group, count: Int, rule: Rule): Unit = {
    (1 to count).foreach(_ => {
      val enemy = rule.spawn(SpawnParams(game, rule, group, enemyMissiles, player))
      randomSafePosition(enemy)
    })
  }
}
