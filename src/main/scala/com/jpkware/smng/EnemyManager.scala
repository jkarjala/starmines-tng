package com.jpkware.smng

import com.definitelyscala.phaser.{Game, Group, Sprite}
import scala.math.min

case class SpawnParams(game: Game, rule: Rule, group: Group, player: Player)

case class Rule
(
  spawn: (SpawnParams) => Enemy, // spawn function
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

class EnemyManager(game: Game, randomSafePosition: (Sprite) => Unit) {
  val MAX = 100000
  val rules = Seq(
    Rule(Enemy.spawn, "mine",  18, 9,  125, minL=1, maxL=MAX, modL=1, minC=4, maxC=10, div=1, spd=40),
    Rule(PathMine.spawn, "torusa", 32, 18, 150, minL=2, maxL=20, modL=3, minC=2, maxC=10, div=2, spd=100, Seq("3ALz9i9i")),
    Rule(PathMine.spawn, "torusb", 32, 18, 150, minL=3, maxL=20, modL=3, minC=2, maxC=10, div=2, spd=40, Seq("8ALx9ix9iax9ix9id")),
    Rule(PathMine.spawn, "torusc", 32, 18, 150, minL=4, maxL=20, modL=3, minC=2, maxC=10, div=2, spd=40, Seq("8ALz9iz9iz9iz9iz9iz9iaz9iz9iz9iz9iz9iz9id")),

    Rule(PathWorm.spawn, "balla", 32, 12, 250, minL=5, maxL=30, modL=3, minC=1, maxC=5, div=2, spd=50, Seq("6ALx9ix9iz9i", "2")),
    Rule(PathWorm.spawn, "ballb", 32, 12, 250, minL=6, maxL=31, modL=3, minC=1, maxC=5, div=2, spd=50, Seq("6ALz9i2z9i9ix", "2")),
    Rule(PathWorm.spawn, "ballc", 32, 12, 250, minL=7, maxL=32, modL=3, minC=1, maxC=5, div=2, spd=50, Seq("6AL9ix9ix9iz", "2")),

    Rule(XYShooter.spawn, "xshoot",  32, 8, 300, minL=8, maxL=MAX, modL=2, minC=1, maxC=5, div=4, spd=80, Seq("1","0", "800")),
    Rule(XYShooter.spawn, "yshoot",  32, 8, 300, minL=9, maxL=MAX, modL=2, minC=1, maxC=5, div=4, spd=80, Seq("0","1", "800")),

    Rule(Splitter.spawn, "2atom", 16, 12, 400, minL=10, maxL=40, modL=3, minC=3, maxC=10, div=1, spd=60, Seq("1atom","16","12","2")),
    Rule(Splitter.spawn, "2atomb", 16, 12, 400, minL=11, maxL=41, modL=3, minC=3, maxC=10, div=1, spd=60, Seq("1atom","16","12","2")),
    Rule(Splitter.spawn, "2atomc", 24, 12, 400, minL=12, maxL=42, modL=3, minC=3, maxC=10, div=1, spd=60, Seq("1atom","16","12","2")),

    Rule(Splitter.spawn, "4atom", 16, 12, 400, minL=15, maxL=MAX, modL=3, minC=1, maxC=5, div=3, spd=90, Seq("1atom","16","12","4")),
    Rule(Splitter.spawn, "4atomb", 16, 12, 400, minL=16, maxL=MAX, modL=3, minC=1, maxC=5, div=3, spd=90, Seq("1atom","16","12","4")),
    Rule(Splitter.spawn, "4atomc", 16, 12, 400, minL=17, maxL=MAX, modL=3, minC=1, maxC=5, div=3, spd=90, Seq("1atom","16","12","4")),

    Rule(PathWorm.spawn, "balla", 32, 12, 250, minL=30, maxL=47, modL=3, minC=1, maxC=5, div=3, spd=60, Seq("6ALx9ix9iz9i", "4")),
    Rule(PathWorm.spawn, "ballb", 32, 12, 250, minL=31, maxL=48, modL=3, minC=1, maxC=5, div=3, spd=60, Seq("6ALz9i2z9i9ix", "4")),
    Rule(PathWorm.spawn, "ballc", 32, 12, 250, minL=32, maxL=49, modL=3, minC=1, maxC=5, div=3, spd=60, Seq("6AL9ix9ix9iz", "4")),

    Rule(PathWorm.spawn, "balla", 32, 12, 250, minL=50, maxL=MAX, modL=3, minC=1, maxC=5, div=5, spd=80, Seq("6ALx9ix9iz9i", "6")),
    Rule(PathWorm.spawn, "ballb", 32, 12, 250, minL=51, maxL=MAX, modL=3, minC=1, maxC=5, div=5, spd=80, Seq("6ALz9i2z9i9ix", "6")),
    Rule(PathWorm.spawn, "ballc", 32, 12, 250, minL=52, maxL=MAX, modL=3, minC=1, maxC=5, div=5, spd=80, Seq("6AL9ix9ix9iz", "6")),

    Rule(Producer.spawn, "4boxa", 16, 20, 1000, minL=20, maxL=MAX, modL=2, minC=1, maxC=5, div=10, spd=10, Seq("1boxa","16","12","4","5000")),
    Rule(Producer.spawn, "4boxb", 16, 20, 1000, minL=21, maxL=MAX, modL=2, minC=1, maxC=5, div=10, spd=10, Seq("1boxb","16","12","4", "5000")),
    Rule(null,"",0,0,0,0,0,0,0,0,0,0)
  )

  def spawnEnemies(player: Player, level: Int, count: Int): Group = {
    val enemies = game.add.group(name = "enemies")

    if (count>0) {
      // Test with first rule only, and fixed count
      val rule = rules.head
      spawnEnemies(player, enemies, count, rule)
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
          spawnEnemies(player, enemies, enemyCount, rule)
        }
      })
    }
    enemies.forEach((c: Sprite) => { Logger.info(s"${c.frameName} ${c.alive}  ${c.centerX} ${c.centerX}")}, null, false)
    enemies
  }

  def spawnEnemies(player: Player, group: Group, count: Int, rule: Rule): Unit = {
    (1 to count).foreach(i => {
      val enemy = rule.spawn(SpawnParams(game, rule, group, player))
      randomSafePosition(enemy)
    })
  }
}
