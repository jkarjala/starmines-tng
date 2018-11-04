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
  minL: Int = 1, // first level
  maxL: Int = 1, // last level
  modL: Int = 1, // (level-minL) % mod must be 0
  minC: Int = 1, // min count
  maxC: Int = 1, // max count
  div: Int = 1,  // count is (level-minL)/div + minC
  spd: Int = 50, // speedlimit
  args: Seq[String] = Seq() // Additional arguments
)

class EnemyManager(game: Game, randomSafePosition: (Sprite) => Unit) {
  val MAX = 100000
  val rules = Seq(
    Rule(Enemy.spawn, "mine",  18, 9,  125, minL=1, maxL=MAX, modL=1, minC=4, maxC=10, div=1, spd=40),
    Rule(Enemy.spawn, "balla", 32, 12, 150, minL=2, maxL=20, modL=3, minC=2, maxC=10, div=2, spd=60),
    Rule(Enemy.spawn, "ballb", 32, 12, 150, minL=3, maxL=21, modL=3, minC=2, maxC=10, div=2, spd=60),
    Rule(Enemy.spawn, "ballc", 32, 12, 150, minL=4, maxL=22, modL=3, minC=2, maxC=10, div=2, spd=60),
    Rule(Enemy.spawn, "torusa", 32, 12, 250, minL=10, maxL=40, modL=3, minC=2, maxC=10, div=2, spd=80),
    Rule(Enemy.spawn, "torusb", 32, 12, 250, minL=11, maxL=41, modL=3, minC=2, maxC=10, div=2, spd=80),
    Rule(Enemy.spawn, "torusc", 32, 12, 250, minL=12, maxL=42, modL=3, minC=2, maxC=10, div=2, spd=80),
    Rule(Splitter.spawn, "2atom", 16, 12, 400, minL=20, maxL=50, modL=3, minC=1, maxC=10, div=2, spd=60, Seq("1atom","16","12","2")),
    Rule(Splitter.spawn, "2atomb", 16, 12, 400, minL=21, maxL=51, modL=3, minC=1, maxC=10, div=2, spd=60, Seq("1atom","16","12","2")),
    Rule(Splitter.spawn, "2atomc", 24, 12, 400, minL=22, maxL=52, modL=3, minC=1, maxC=10, div=2, spd=60, Seq("1atom","16","12","2")),
    Rule(Splitter.spawn, "4atom", 16, 12, 400, minL=30, maxL=60, modL=3, minC=1, maxC=10, div=2, spd=90, Seq("1atom","16","12","4")),
    Rule(Splitter.spawn, "4atomb", 16, 12, 400, minL=31, maxL=61, modL=3, minC=1, maxC=10, div=2, spd=90, Seq("1atom","16","12","4")),
    Rule(Splitter.spawn, "4atomc", 16, 12, 400, minL=32, maxL=62, modL=3, minC=1, maxC=10, div=2, spd=90, Seq("1atom","16","12","4")),
    Rule(Splitter.spawn, "4boxa", 16, 12, 400, minL=50, maxL=MAX, modL=2, minC=1, maxC=10, div=2, spd=120, Seq("1atom","16","12","4")),
    Rule(Splitter.spawn, "4boxb", 16, 12, 400, minL=51, maxL=MAX, modL=2, minC=1, maxC=10, div=2, spd=120, Seq("1atom","16","12","4")),
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
