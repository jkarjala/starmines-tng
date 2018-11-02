package com.jpkware.smng

import com.definitelyscala.phaser.{Game, Group, Sprite}

case class Rule
(
  spawn: (Game, Rule) => Sprite,
  minLevel: Int,
  maxLevel: Int,
  minCount: Int,
  maxCount: Int
)

class EnemyManager(game: Game, randomSafePosition: (Sprite) => Unit) {
  val MAX = 100000
  val rules = Seq(
    Rule(Mine.spawn, 1, MAX, 9, 20)
  )

  def spawnEnemies(level: Int, count: Int): Group = {

    def enemyCount: Int = if (count>0) count else 9 + level
    val enemies = game.add.group()
    spawnEnemies(enemies, enemyCount, rules(0))
    enemies
  }

  def spawnEnemies(group: Group, count: Int, rule: Rule): Unit = {
    (1 to count).foreach(i => {
      val mine = rule.spawn(game, rule)
      randomSafePosition(mine)
      group.add(mine)
    })
  }
}
