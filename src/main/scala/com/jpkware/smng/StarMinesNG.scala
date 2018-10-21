package com.jpkware.smng

import com.definitelyscala.phaser.Game
import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser._
import org.scalajs.dom
import org.scalajs.dom.raw.Element

import scala.annotation.tailrec
import scala.util.Random


object StarMinesNG {
  val rnd = new Random(42)

  def main(args: Array[String]): Unit = {

    val parent: Element = dom.document.getElementById("game")
    val hash = dom.document.location.hash
    val options: Map[String, String] = if (hash==null || hash.isEmpty) Map() else {
      val s = hash.tail.split(',')
      s.map(o => {
        val os = o.split('=')
        if (os.length>1) os(0) -> os(1) else os(0) -> "true"
      }).toMap
    }
    new StarMinesNG(parent, options)
  }
}

class StarMinesNG(parent: Element, options: Map[String,String]) {
  var player: Player = _
  var scorebox: Sprite = _
  var mines: Group = _
  var sfxZap: Sound = _
  var sfxExplo: Sound = _
  var sfxTinyexp: Sound = _
  var cursors: CursorKeys = _
  var fireButton: Key = _
  var gamepad: Any = _
  var scoreText: BitmapText = _
  var score: Int = _
  var fpsText: BitmapText = _

  var rotateRight = false
  var rotateLeft = false
  var rotateStop = false
  var thrust = false
  var fire = false

  private val mode = if (options.contains("webgl")) Phaser.WEBGL else Phaser.CANVAS
  val game = new Game(960, 540, mode, parent)
  game.state.add("play", State(preload, create, update, render))
  game.state.start("play", clearWorld = true, clearCache = false)

  private val mineCount = options.getOrElse("mines", "10").toInt

  def preload(): Unit = {
    game.time.advancedTiming = true
    game.load.atlasJSONHash("sprites", "res/mainv1.png", "res/mainv1.json")
    game.load.image("space", "res/space1.jpg")
    game.load.image("missile", "res/missile.png")
    game.load.image("flame", "res/flame.png")
    game.load.image("scorebox", "res/scorebox.png")
    game.load.image("explo", "res/explo.png")
    game.load.bitmapFont("font", "res/font.png", "res/font.fnt")
    game.load.spritesheet("button", "res/button.png",128, 128)
    game.load.audio("sfx:zap", "res/zap.wav")
    game.load.audio("sfx:explo", "res/explo.wav")
    game.load.audio("sfx:tinyexp", "res/tinyexp.wav")
  }

  def create(): Unit = {
    game.scale.fullScreenScaleMode = ScaleManager.SHOW_ALL

    game.add.sprite(0,0,"space")

    if (options.contains("touch") || !game.device.desktop) addTouchButtons()

    game.physics.startSystem(PhysicsObj.ARCADE)

    player = new Player(game, 100,100)
    game.add.existing(player)
    cursors = game.input.keyboard.createCursorKeys()
    fireButton = game.input.keyboard.addKey(Keyboard.SPACEBAR)

    scorebox = game.add.sprite(game.width/2,game.height/2, "scorebox")
    scorebox.anchor.set(0.5,0.5)
    game.physics.enable(scorebox)
    scorebox.body match { case b: Body => b.immovable = true}
    scorebox.inputEnabled = true
    scorebox.events.onInputUp.add(() => {
      if (game.scale.isFullScreen) game.scale.stopFullScreen() else game.scale.startFullScreen()
    }, null, 1)

    game.add.bitmapText(game.width/2,game.height/2-40, "font", "StarMines", 48).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2,game.height/2-20, "font", "The Next Generation", 18).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2-140,game.height/2+10, "font", "Score:", 24)
    scoreText = game.add.bitmapText(game.width/2-48,game.height/2+10, "font", "", 24)
    score = 0
    updateScore(0)

    Explosion.initGroups(game, Seq(Explosion.LargeExploCount, Explosion.SmallExploCount))

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

    val buttonY = game.height - 128
    addTouchButton(10, buttonY, () => {
      rotateLeft = true
    }, () => {
      rotateStop = true
    })
    addTouchButton(150, buttonY, () => {
      rotateRight = true
    }, () => {
      rotateStop = true
    })
    addTouchButton(game.width - 138, buttonY, () => {
      fire = true
    }, () => {
      fire = false
    })
    addTouchButton(game.width - 278, buttonY, () => {
      thrust = true
    }, () => {
      thrust = false
    })
  }

  def addTouchButton(x: Double, y: Double, down: () => Unit, up: () => Unit): Button = {
    val button = game.add.button(x, y, "button", null, null, 0, 1, 0, 1)
    button.fixedToCamera = true
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
    updateScore(123)
    sfxTinyexp.play()
    reviveMine()
  }
  def playerVsEnemy(player: Player, enemy: Sprite): Unit = {
    Explosion(game, Explosion.LargeExploCount).explode(player, 2000)
    sfxExplo.play()
    updateScore(-1000)
    enemy.kill()
    reviveMine()
    reviveMine()
    player.stop()
  }

  def updateScore(delta: Int): Unit = {
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

    if (fireButton.isDown || k.isDown('M') || fire) {
      if (player.fire()!=null) sfxZap.play()
    }
  }
}