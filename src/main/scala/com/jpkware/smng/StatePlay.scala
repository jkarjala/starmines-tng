package com.jpkware.smng

import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser.{Game, _}
import org.scalajs.dom.raw.Element

import scala.annotation.tailrec
import scala.scalajs.js


class StatePlay(game: Game, options: Map[String,String], status: Element) {
  var player: Player = _
  var scorebox: Sprite = _
  var mines: Group = _
  var touchButtons: Group = _
  var sfxZap: Sound = _
  var sfxExplo: Sound = _
  var sfxTinyexp: Sound = _
  var cursors: CursorKeys = _
  var scoreText: BitmapText = _
  var score: Int = _
  var livesText: BitmapText = _
  var lives: Int = _
  var fpsText: BitmapText = _
  var shield: Boolean = _

  var rotateRight = false
  var rotateLeft = false
  var rotateStop = false
  var thrust = false
  var fire = false
  var gameOver = false

  val state: State = PhaserState(init _, preload, create, update, render)

  private val mineCount = options.getOrElse("mines", "10").toInt

  def init(args: js.Array[String]): Unit = {
    Logger.info(s"init: $args")
    score = 0
    lives = 5
    shield = false
    rotateRight = false
    rotateLeft = false
    rotateStop = false
    thrust = false
    fire = false
    gameOver = false
  }

  def preload(): Unit = {
  }

  def create(): Unit = {
    val space = game.add.sprite(0,0,"space")
    space.scale.set(2,2)

    touchButtons = game.add.group()
    if (options.contains("touch") || !game.device.desktop) addTouchButtons()

    game.physics.startSystem(PhysicsObj.ARCADE)

    player = new Player(game, 100,100)
    game.add.existing(player)
    cursors = game.input.keyboard.createCursorKeys()

    Explosion.initGroups(game, Seq(Explosion.LargeExploCount, Explosion.SmallExploCount))

    scorebox = game.add.sprite(game.width/2,game.height/2, "scorebox")
    scorebox.anchor.set(0.5,0.5)
    game.physics.enable(scorebox)
    scorebox.body match { case b: Body => b.immovable = true}
    scorebox.inputEnabled = true
    scorebox.events.onInputUp.add(() => {
      if (game.scale.isFullScreen) game.scale.stopFullScreen() else game.scale.startFullScreen()
    }, null, 1)

    game.add.bitmapText(game.width/2,game.height/2-80, "font", "StarMines", 96).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2,game.height/2-48, "font", "THE NEXT GENERATION", 32).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2-280,game.height/2+20, "font", "Score:", 48)
    scoreText = game.add.bitmapText(game.width/2-96,game.height/2+20, "font", "", 48)
    addToScore(0)

    game.add.bitmapText(game.width/2-280,game.height/2+60, "font", "Lives:", 48)
    livesText = game.add.bitmapText(game.width/2-96,game.height/2+60, "font", "", 48)
    updateLives(lives)

    fpsText = game.add.bitmapText(5,5, "font", "", 18)

    spawnMines()

    sfxZap = game.add.audio("sfx:zap")
    sfxZap.allowMultiple = true
    sfxExplo = game.add.audio("sfx:explo")
    sfxZap.allowMultiple = true
    sfxTinyexp = game.add.audio("sfx:tinyexp")
    sfxTinyexp.allowMultiple = true
  }

  def update(): Unit = {
    if (gameOver) return
    handleCollisions()
    handleInput()
    if (options.contains("fps")) fpsText.setText(game.time.fps.toString)
  }

  def render(): Unit = {
    // game.debug.bodyInfo(player, 32, 32)
    // game.debug.body(player)
    // game.debug.pointer(game.input.mousePointer)
  }

  def addTouchButtons(): Unit = {
    game.input.addPointer() // 3rd
    game.input.addPointer() // 4th
    val radius = 128
    val buttonY = game.height - radius
    addTouchButton(radius, buttonY, "CCW", () => {
      rotateLeft = true
    }, () => {
      rotateStop = true
    })
    addTouchButton(radius*3, buttonY, "CW", () => {
      rotateRight = true
    }, () => {
      rotateStop = true
    })
    addTouchButton(game.width - radius, buttonY, "Fire", () => {
      fire = true
    }, () => {
      fire = false
    })
    addTouchButton(game.width - radius*4 + radius, buttonY, "Thrust", () => {
      thrust = true
    }, () => {
      thrust = false
    })
  }

  def addTouchButton(x: Double, y: Double, text: String, down: () => Unit, up: () => Unit): Button = {
    val button = PhaserButton.add(game, x, y, text, 0.2, touchButtons)
    button.events.onInputOver.add(down, null, 1)
    button.events.onInputOut.add(up, null, 1)
    button.events.onInputDown.add(down, null, 1)
    button.events.onInputUp.add(up, null, 1)
    button
  }

  def spawnMines(): Unit = {
    mines = game.add.group()

    (1 to mineCount).foreach(i => {
      val mine = new Mine(game, 0,0)
      ensureSafePosition(mine)
      mines.add(mine)
    })
  }

  def reviveMine(): Mine = {
    mines.getFirstDead() match {
      case mine: Mine =>
        ensureSafePosition(mine)
        mine
      case _ => null
    }
  }

  @tailrec
  private def ensureSafePosition(sprite: Sprite): Unit = {
    val x = StarMinesNG.rnd.nextFloat()*game.world.width
    val y = StarMinesNG.rnd.nextFloat()*game.world.height
    val sbb: Rectangle = scorebox.getBounds().asInstanceOf[Rectangle]

    // XXX the bounds are in local space if the sprite is not yet in world, transform manually
    if (sbb.x<0) {
      sbb.x += scorebox.position.x
      sbb.y += scorebox.position.y
    }
    val mbb: Rectangle = sprite.getBounds().asInstanceOf[Rectangle]
    // The new position is not yet in effect; assume x,y is center
    mbb.x = x - mbb.width/2
    mbb.y = y - mbb.height/2
    if (!Rectangle.intersects(sbb,mbb)) {
      sprite.reset(x, y)
    }
    else {
      Logger.info(s"$x, $y was unsafe, retrying")
      ensureSafePosition(sprite)
    }
  }

  def handleCollisions(): Unit = {
    game.physics.arcade.collide(player, scorebox)
    game.physics.arcade.overlap(player, mines, playerVsEnemy _, null, null)
    game.physics.arcade.collide(player.weapon.bullets, scorebox)
    game.physics.arcade.overlap(player.weapon.bullets, mines, bulletVsEnemy _, null, null)
    game.physics.arcade.collide(mines, scorebox)
  }

  def bulletVsEnemy(bullet: Bullet, enemy: Sprite): Unit = {
    Explosion(game, Explosion.SmallExploCount).explode(enemy, 500)
    enemy.kill()
    bullet.kill()
    addToScore(123)
    sfxTinyexp.play()
    reviveMine()
  }
  def playerVsEnemy(player: Player, enemy: Sprite): Unit = {
    if (shield) {
      Explosion(game, Explosion.SmallExploCount).explode(enemy, 500)
      sfxTinyexp.play()
      enemy.kill()
      reviveMine()
    }
    else {
      Explosion(game, Explosion.LargeExploCount).explode(player, 2000)
      sfxExplo.play()
      enemy.kill()
      player.death()
      val timer = game.time.create(true)
      timer.add(2000, () => {
        shield = true
        updateLives(lives - 1)
        if (lives==0) handleGameOver()
        else {
          player.revive(1)
          player.alpha = 0.5
        }
      }, null)
      timer.add(3000, () => {
        shield = false
        player.alpha = 1.0
      }, null)
      timer.start(0)
    }
  }

  def updateLives(lives: Int): Unit = {
    this.lives = lives
    livesText.setText(f"$lives%d")
  }

  def handleGameOver(): Unit = {
    gameOver = true
    touchButtons.destroy()
    mines.destroy()
    game.state.start("gameover", args = js.Array[String]("gameover"), clearCache = false, clearWorld = false)
  }

  def addToScore(delta: Int): Unit = {
    score += delta
    if (score<0) score = 0
    scoreText.setText(f"$score%08d")
  }
  def handleInput(): Unit = {
    val k = game.input.keyboard
    if (game.device.desktop) {
      if (cursors.left.isDown || k.isDown('Z')) rotateLeft = true
      else if (cursors.right.isDown || k.isDown('X')) rotateRight = true
      else if (!options.contains("touch"))
        rotateStop = true
    }

    if (rotateRight) player.rotateRight()
    if (rotateLeft) player.rotateLeft()
    if (rotateStop) {
      rotateRight = false
      rotateLeft = false
      rotateStop = false
      player.rotateStop()
    }

    if (game.device.desktop) {
      if (cursors.up.isDown || k.isDown('N')) thrust = true else if (!options.contains("touch")) thrust = false
    }

    if (thrust) player.thrust() else player.brake()

    if (PhaserKeys.isFireDown(game) || fire) {
      if (player.fire()!=null) sfxZap.play()
    }

    if (k.isDown(27)) game.state.start("menu", args = js.Array[String]("quit"), clearCache = false, clearWorld = true)
  }
}