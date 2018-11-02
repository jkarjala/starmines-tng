package com.jpkware.smng

import com.definitelyscala.phaser.{Game, Group, Sprite}

class EnemyManager(game: Game, randomSafePosition: (Sprite) => Unit) {

  def spawnEnemies(level: Int, count: Int): Group = {
    def mineCount: Int = if (count>0) count else 9 + level
    val enemies = game.add.group()
    spawnMines(enemies, mineCount)
    enemies
  }

  def spawnMines(group: Group, mineCount: Int): Unit = {
    (1 to mineCount).foreach(i => {
      val mine = new Mine(game, 0,0)
      randomSafePosition(mine)
      group.add(mine)
    })
  }
}
