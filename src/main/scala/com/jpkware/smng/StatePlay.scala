package com.jpkware.smng

import com.definitelyscala.phaser._
import org.scalajs.dom.raw.Element

import scala.scalajs.js


class StatePlay(game: Game, options: Map[String,String], status: Element) extends State {
  var player: Player = _
  var enemies: Group = _
  var bonusManager: BonusManager = _
  var enemyManager: EnemyManager = _
  var cursors: CursorKeys = _
  var fpsText: BitmapText = _
  var touch: TouchControls = _
  var gameOver = false
  var sfxLevelEnd: Sound = _
  var sfxLevelClr: Sound = _
  var sfxCollect: Sound = _
  var messages: Messages = _

  def optionsCount: Int = if (options.contains("mines")) options("mines").toInt else -1

  override def init(args: js.Any*): Unit = {
    args.headOption match {
      case str: Some[js.Any] =>
        Logger.info(s"Play init ${str.get}")
        val cmd = str.get.asInstanceOf[String]
        if (cmd=="nextlevel") {
          StatePlay.scorebox.addToLevel(1)
          StatePlay.scores.bonusesCollected = 0
        }
        else {
          StatePlay.scores = Scorebox.InitialScore
          if (cmd(0).isDigit) StatePlay.scores.level = cmd.toInt
          else if (options.contains("level"))
            StatePlay.scores.level = options("level").toInt
        }
      case _ =>
    }
    StarMinesNG.rnd.setSeed(42+StatePlay.scores.level)
    if (StatePlay.scores.level > StarMinesNG.progress.maxLevel) {
      StarMinesNG.progress.maxLevel = StatePlay.scores.level
      Progress.save(StarMinesNG.progress)
    }
    gameOver = false
  }

  override def create(): Unit = {

    val space = StarMinesNG.addBackground(game, StatePlay.scores.level)
    space.scale.set(2,2)

    val gr = game.add.graphics(0,0)
    gr.lineStyle(2, 0xFFFFFF, 1)
    gr.drawRect(0,0,game.width,game.height)

    if (options.contains("debug")) {
      val button = PhaserButton.add(game, game.width/2, game.height-128, "skip", scale = 1.0)
      button.events.onInputUp.add(nextLevel _, null, 1)
    }

    PhaserButton.addMinMax(game)

    val button = PhaserButton.add(game, game.width - 40, 40, "", textFrame = PhaserButton.FrameExit, scale = 0.5)
    button.events.onInputUp.add(() => gotoMenu(), null, 1)

    game.physics.startSystem(PhysicsObj.ARCADE)

    player = new Player(game, 100,100)
    game.add.existing(player)

    touch = new TouchControls(game, options.contains("stick"))
    if (options.contains("touch") || options.contains("stick") || !game.device.desktop) touch.enable() else touch.addMouseControls(space, player)

    cursors = game.input.keyboard.createCursorKeys()

    Explosion.initGroups(game, Seq(Explosion.LargeExploCount, Explosion.SmallExploCount, Explosion.TinyExploCount))

    StatePlay.scorebox = new Scorebox(game, StatePlay.scores)

    fpsText = game.add.bitmapText(5,5, GlobalRes.FontId, "", 18)

    sfxLevelEnd = game.add.audio(StatePlay.SfxLevelEndId)
    sfxLevelClr = game.add.audio(StatePlay.SfxLevelClrId)
    sfxCollect = game.add.audio(StatePlay.SfxSwip)

    bonusManager = new BonusManager(game, 1 + scala.math.min(((StatePlay.scores.level-1)/2).toInt, 8), setStartPosition)
    messages = new Messages(game)

    enemyManager = new EnemyManager(game, setStartPosition)
    enemies = enemyManager.spawnEnemies(player, StatePlay.scores.level, optionsCount)

    StatePlay.scores.timeBonus = bonusManager.bonusCount * 5000
  }

  override def update(): Unit = {
    if (gameOver) return
    handleCollisions()
    handleInput()
    if (options.contains("fps")) fpsText.setText(s"${game.time.fps.toString} ${enemies.countLiving()}")
  }

  override def render(): Unit = {
    // game.debug.bodyInfo(player, 32, 32)
    // game.debug.body(player)
    // game.debug.pointer(game.input.mousePointer)
  }

  private def setStartPosition(sprite: Sprite): Unit = {
    val minX = StatePlay.scorebox.position.x - StatePlay.scorebox.width/2
    val maxX = StatePlay.scorebox.position.x + StatePlay.scorebox.width/2
    val minY = StatePlay.scorebox.position.y - StatePlay.scorebox.height/2
    val maxY = StatePlay.scorebox.position.y + StatePlay.scorebox.height/2

    val areas = Seq(
      new Rectangle(200, 0, game.width-200-minX, minY), // from player to right
      new Rectangle(0, 200, minX, game.height-200), // from player to down end of screen
      new Rectangle(minX, maxY, game.width-minX, game.height-maxY), // below scorebox right end of screen
      new Rectangle(maxX, 0, game.width-maxX, game.height-minY) // top-right of scorebox to down
    )
    val margin = 20
    val area = areas(StarMinesNG.rnd.nextInt(4))
    val x = area.x+margin + StarMinesNG.rnd.nextInt(area.width.toInt-2*margin)
    val y = area.y+margin + StarMinesNG.rnd.nextInt(area.height.toInt-2*margin)
    sprite.reset(x,y)
  }

  def handleCollisions(): Unit = {
    game.physics.arcade.collide(player, StatePlay.scorebox)
    game.physics.arcade.overlap(player, enemies, playerVsEnemy _, null, null)
    game.physics.arcade.overlap(player, bonusManager.bonuses, playerVsBonus _, null, null)
    game.physics.arcade.collide(player.weapon.bullets, StatePlay.scorebox)
    game.physics.arcade.overlap(player.weapon.bullets, enemies, bulletVsEnemy _, null, null)
    game.physics.arcade.overlap(player.weapon.bullets, bonusManager.containers, bulletVsBonusoid _, null, null)
    game.physics.arcade.overlap(player.weapon.bullets, bonusManager.bonuses, bulletVsBonus _, null, null)
    game.physics.arcade.overlap(enemies, enemies, enemyVsEnemy _, null, null)
    game.physics.arcade.collide(enemies, StatePlay.scorebox)
    game.physics.arcade.collide(bonusManager.bonuses, StatePlay.scorebox)
    if (bonusManager.allDead || enemies.countLiving()==0) nextLevel()
  }

  def nextLevel(): Unit = {
    val result = if (bonusManager.bonusCount == StatePlay.scores.bonusesCollected) {
      sfxLevelEnd.play()
      "Field completed perfectly!\nAll Bonusoids collected!"
    }
    else {
      sfxLevelClr.play()
      if (enemies.countLiving()==0)
        "Mines destroyed, field completed!\nSome Bonusoids not collected..."
      else
        "Field completed, but\nlost some Bonusoids..."
    }
    touch.disable()
    enemies.destroy()
    messages.clear()
    player.hide()
    bonusManager.bonuses.destroy()
    game.state.start("nextlevel", args = result, clearCache = false, clearWorld = false)
  }

  def bulletVsEnemy(bullet: Bullet, enemy: Sprite): Unit = {
    enemy match {
      case e: Enemy => StatePlay.scorebox.addToScore(e.bulletHit(bullet))
      case _ => Logger.warn(s"Unknown enemy $enemy")
    }
  }

  def bulletVsBonusoid(bullet: Bullet, bonusoid: Sprite): Unit = {
    messages.show("Bonusoids released, go catch them!")
    Explosion(game, Explosion.SmallExploCount).explode(bonusoid)
    bonusoid.kill()
    bullet.kill()
    StatePlay.scorebox.addToScore(1000)
  }

  def bulletVsBonus(bullet: Bullet, bonus: Sprite): Unit = {
    messages.show("Bonusoid lost!")
    Explosion(game, Explosion.SmallExploCount).explode(bonus)
    bonus.kill()
    bullet.kill()
    StatePlay.scorebox.addToScore(100)
  }

  def playerVsBonus(player: Player, bonus: Sprite): Unit = {
    messages.show("Bonusoid collected!")
    sfxCollect.play()
    bonus.kill()
    StatePlay.scorebox.addToBonusesCollected(1)
    StatePlay.scorebox.addToScore(2000)
    StatePlay.scorebox.addToTimeBonus(2000)
  }

  def playerVsEnemy(player: Player, enemy: Sprite): Unit = {
    if (player.immortal) {
      Explosion(game, Explosion.SmallExploCount).explode(enemy)
      enemy.kill()
    }
    else {
      messages.show("SHIP LOST!")
      Explosion(game, Explosion.LargeExploCount).explode(player)
      enemy.kill()
      player.kill()
      val timer = game.time.create(true)
      timer.add(3000, () => {
        StatePlay.scorebox.addToLives(-1)
        if (StatePlay.scores.lives==0) handleGameOver() else player.revive()
      }, null)
      timer.start(0)
    }
  }

  def enemyVsEnemy(enemy1: Enemy, enemy2: Enemy): Unit = {
    if (!enemy1.enemyHit(enemy2)) enemy2.enemyHit(enemy1)
  }

  def handleGameOver(): Unit = {
    gameOver = true
    touch.disable()
    enemies.destroy()
    player.hide()
    messages.clear()
    saveProgress()
    game.state.start("gameover", args = "gameover", clearCache = false, clearWorld = false)
  }

  def saveProgress(): Unit = {
    if (StatePlay.scores.score > StarMinesNG.progress.highScore) {
      StarMinesNG.progress.highScore = StatePlay.scores.score
    }
    if (StatePlay.scores.totalBonuses > StarMinesNG.progress.maxBonusesCollected) {
      StarMinesNG.progress.maxBonusesCollected = StatePlay.scores.totalBonuses
    }
    Progress.save(StarMinesNG.progress)
  }

  def handleInput(): Unit = {
    val k = game.input.keyboard

    if (cursors.left.isDown || k.isDown('Z') || touch.rotateLeft) player.rotateLeft()
    else if (cursors.right.isDown || k.isDown('X') || touch.rotateRight) player.rotateRight()
    else player.rotateStop()

    if (cursors.up.isDown || k.isDown('N') || touch.thrust) player.thrust()
    else if (touch.thrustRotation!=touch.NoRotation) player.rotateOrThrust(touch.thrustRotation)
    else player.thrustStop()

    if (touch.fireRotation!=touch.NoRotation) player.rotateToFire(touch.fireRotation)

    if (PhaserKeys.isFireDown(game) || touch.fire) player.fire()

    if (k.isDown(27)) gotoMenu()
  }

  def gotoMenu(): Unit = {
    saveProgress()
    game.state.start("menu", args = "quit", clearCache = false, clearWorld = true)
  }
}

object StatePlay {
  def SfxLevelClrId = "sfx:levelclr"
  def SfxLevelEndId = "sfx:levelend"
  def SfxSwip = "sfx:swip"

  var scores: ScoreState = _
  var scorebox: Scorebox = _

  def preloadResources(game: Game): Unit = {
    game.load.audio(SfxLevelClrId, "res/levelclr.wav")
    game.load.audio(SfxLevelEndId, "res/levelend.wav")
    game.load.audio(SfxSwip, "res/swip.wav")
  }
}