package com.jpkware.smng

import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser._

class PathMine(game: Game, rule: Rule, group: Group) extends Enemy(game, rule, group) {

  // Path syntax: z=ccw, x=cw, a=accel, d=decel, i=idle, L=loop-start, [1-9]c=repeat c per tick, [1-9]C repeat c now
  val path = preparePath(rule.args(0))
  var pathIndex = 0
  var loopIndex = 0
  var speed = 0.0
  var tick = 0

  def preparePath(path: String): String = {
    val res = new StringBuffer(path.length)
    var i = 0
    while (i<path.length) {
      path(i) match {
        case d if d.isDigit =>
          val count = d.toString.toInt
          i += 1
          val c = path(i)
          if (c.isLower) {
            // repeat the command to make it happen at every tick
            (1 to count).foreach(_ => res.append(c))
          }
          else  {
            res.append(d)
            res.append(c.toLower)
          }
        case c => res.append(c)
      }
      i += 1
    }
    Logger.info(s"prepared path: $res $loopIndex")
    res.toString
  }

  def followPath(tickCount: Int): Unit = {
    var repeat = 1
    tick += 1

    (1 to tickCount).foreach(i => {
      while (repeat>0) {
        repeat -= 1
        val (deltaHeading: Double, deltaSpeed: Double) = path(pathIndex) match {
          case 'z' =>
            (-10 * (2*scala.math.Pi/360), 0.0)
          case 'x' =>
            (10 * (2*scala.math.Pi/360), 0.0)
          case 'a' =>
            (0.0, rule.spd)
          case 'd' =>
            (0.0, -rule.spd)
          case 'i' =>
            // coasting on idle
            (0.0, 0.0)
          case 'L' =>
            loopIndex = pathIndex + 1
            (0.0, 0.0)
          case c if c.isDigit =>
            repeat += c.toString.toInt
            pathIndex += 1
            (0.0, 0.0)
          case xx =>
            Logger.warn(s"Unknown command $xx in path $path")
            (0.0, 0.0)
        }
        if (deltaHeading != 0.0 || deltaSpeed != 0.0) {
          // Must get heading from the body or collision bounces get messed up
          var heading = scala.math.atan2(physBody.velocity.y, physBody.velocity.x) + deltaHeading
          if (heading < -scala.math.Pi) heading += 2*scala.math.Pi
          if (heading > scala.math.Pi) heading -= 2*scala.math.Pi
          speed += deltaSpeed

          // Logger.info(s"$tick pth: $path cmd: ${path(pathIndex)} speed:$speed, head:$heading, $x, $y")
          game.physics.arcade.velocityFromRotation(heading, speed, physBody.velocity)
        }
        if (repeat==0) {
          if (pathIndex < path.length - 1) pathIndex += 1 else {
            pathIndex = loopIndex
          }
        }
      }
    })
  }

  override def reset(x: Double, y: Double, health: Double = 1): Sprite = {
    pathIndex = 0
    speed = 0
    super.reset(x, y, health)
    this
  }

  override def update(): Unit = {
    if (!alive) return

    val ticks = game.time.physicsElapsedMS / 16
    followPath(ticks.toInt)

    super.update()
  }
}

object PathMine {
  def spawn(p: SpawnParams) : Enemy = new PathMine(p.game, p.rule, p.group)
}