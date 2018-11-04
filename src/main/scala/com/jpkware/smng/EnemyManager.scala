package com.jpkware.smng

import com.definitelyscala.phaser.{Game, Group, Sprite}
import scala.math.min

case class Rule
(
  spawn: (Game, Rule, Group) => Enemy, // spawn function
  shape: String, // sprite shape name prefix
  frames: Int,   // sprite animation frame count
  fps: Int,      // animation fpx
  score: Int,    // score increment when killed
  minL: Int, // first level
  maxL: Int, // last level
  modL: Int, // (level-minL) % mod must be 0
  minC: Int, // min count
  maxC: Int, // max count
  div: Int,  // count is (level-minL)/div + minC
  spd: Int,  // speedlimit
  args: Seq[String] = Seq() // Additional arguments
)

class EnemyManager(game: Game, randomSafePosition: (Sprite) => Unit) {
  val MAX = 100000
  val rules = Seq(
    Rule(Enemy.spawn, "mine",  18, 9,  125, minL=1, maxL=MAX, modL=1, minC=4, maxC=10, div=1, spd=40),
    Rule(Enemy.spawn, "balla", 32, 12, 150, minL=3, maxL=10, modL=3, minC=4, maxC=10, div=2, spd=60),
    Rule(Enemy.spawn, "ballb", 32, 12, 150, minL=4, maxL=11, modL=3, minC=4, maxC=10, div=2, spd=60),
    Rule(Enemy.spawn, "ballc", 32, 12, 150, minL=5, maxL=12, modL=3, minC=4, maxC=10, div=2, spd=60),
    Rule(Enemy.spawn, "torusa", 32, 12, 250, minL=13, maxL=MAX, modL=3, minC=4, maxC=10, div=2, spd=80),
    Rule(Enemy.spawn, "torusb", 32, 12, 250, minL=14, maxL=MAX, modL=3, minC=4, maxC=10, div=2, spd=80),
    Rule(Enemy.spawn, "torusc", 32, 12, 250, minL=15, maxL=MAX, modL=3, minC=4, maxC=10, div=2, spd=80),
    Rule(null,"",0,0,0,0,0,0,0,0,0,0)
  )

  def spawnEnemies(level: Int, count: Int): Group = {
    val enemies = game.add.group()

    if (count>0) {
      // Test with first rule only, and fixed count
      val rule = rules(0)
      spawnEnemies(enemies, count, rule)
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
          spawnEnemies(enemies, enemyCount, rule)
        }
      })
    }
    enemies
  }

  def spawnEnemies(group: Group, count: Int, rule: Rule): Unit = {
    (1 to count).foreach(i => {
      val enemy = rule.spawn(game, rule, group)
      randomSafePosition(enemy)
    })
  }
}
