package com.jpkware.smng

import com.definitelyscala.phaser._
import org.scalajs.dom.raw.Element

import scala.scalajs.js

class StatePlay(game: Game, options: Map[String,String], status: Element) extends State {
  private var player: Player = _
  private var enemies: Group = _
  private var enemyMissiles: Group = _
  private var bonusManager: BonusManager = _
  private var enemyManager: EnemyManager = _
  private var cursors: CursorKeys = _
  private var fpsText: BitmapText = _
  private var touch: TouchControls = _
  private var gameOver = false
  private var sfxLevelEnd: Sound = _
  private var sfxLevelClr: Sound = _
  private var sfxCollect: Sound = _
  private var messages: Messages = _

  private val debug: Boolean = options.contains("debug")
  private def optionsCount: Int = if (options.contains("mines")) options("mines").toInt else -1

  override def init(args: js.Any*): Unit = {
    args.headOption match {
      case str: Some[js.Any] =>
        Logger.info(s"Play init ${str.get}")
        val cmd = str.get.asInstanceOf[String]
        if (cmd=="nextlevel") {
          StatePlay.scorebox.addToLevel(1)
          StatePlay.scores.bonusoidsCollected = 0
          StatePlay.scores.stars = 0
        }
        else {
          StatePlay.scores = Scorebox.InitialScore.copy()
          if (cmd(0).isDigit) StatePlay.scores.level = cmd.toInt
        }
      case _ =>
    }
    StarMinesNG.rnd.setSeed(42+StatePlay.scores.level)
    Progress.updateAndSave(StatePlay.scores, debug)
    gameOver = false
  }

  override def create(): Unit = {

    val space = StarMinesNG.addBackground(game, StatePlay.scores.level)
    space.scale.set(2,2)

    val gr = game.add.graphics(0,0)
    gr.lineStyle(2, 0xFFFFFF, 1)
    gr.drawRect(0,0,game.width,game.height)

    if (debug) {
      val button = PhaserButton.add(game, game.width/2, game.height-128, "skip", scale = 1.0)
      button.events.onInputUp.add(() => { StatePlay.scores.timeBonus = 0; nextLevel() }, null, 1)
    }

    PhaserButton.addMinMax(game)

    val button = PhaserButton.add(game, game.width - 40, 40, "", textFrame = PhaserButton.FrameExit, scale = 0.5)
    button.events.onInputUp.add(() => gotoMenu(), null, 1)

    game.physics.startSystem(PhysicsObj.ARCADE)

    val totalBonusoids = math.max(StatePlay.scores.totalBonusoids, Progress.state.maxBonusoids)
    player = new Player(game, 100,100, totalBonusoids)
    game.add.existing(player)

    touch = new TouchControls(game, options.contains("stick"))
    if (options.contains("touch") || options.contains("stick") || !game.device.desktop) touch.enable() else touch.addMouseControls(space, player)

    cursors = game.input.keyboard.createCursorKeys()

    Explosion.initGroups(game, Seq(Explosion.LargeExploCount, Explosion.SmallExploCount, Explosion.TinyExploCount))

    StatePlay.scores.shipLevel = player.shipLevel+1

    StatePlay.scorebox = new Scorebox(game, StatePlay.scores)

    fpsText = game.add.bitmapText(5,5, GlobalRes.FontId, "", 18)

    sfxLevelEnd = game.add.audio(StatePlay.SfxLevelEndId)
    sfxLevelClr = game.add.audio(StatePlay.SfxLevelClrId)
    sfxCollect = game.add.audio(StatePlay.SfxSwip)

    bonusManager = new BonusManager(game, 1 + scala.math.min((StatePlay.scores.level-1)/2, 9), setStartPosition)
    messages = new Messages(game)

    messages.show(s"Ship level ${player.shipLevel+1}")

    enemyManager = new EnemyManager(game, setStartPosition)
    val (e, m) = enemyManager.spawnEnemies(player, StatePlay.scores.level, optionsCount)
    enemies = e
    enemyMissiles = m

    StatePlay.scores.timeBonus = bonusManager.bonusoidCount * 5000
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
    game.physics.arcade.overlap(player, enemyMissiles, playerVsEnemy _, null, null)
    game.physics.arcade.overlap(player, bonusManager.bonusoids, playerVsBonusoid _, null, null)

    game.physics.arcade.collide(player.weapon1.bullets, StatePlay.scorebox)
    game.physics.arcade.overlap(player.weapon1.bullets, enemies, bulletVsEnemy _, null, null)
    game.physics.arcade.overlap(player.weapon1.bullets, bonusManager.containers, bulletVsBonusContainer _, null, null)
    game.physics.arcade.overlap(player.weapon1.bullets, bonusManager.bonusoids, bulletVsBonusoid _, null, null)

    game.physics.arcade.collide(player.weapon2.bullets, StatePlay.scorebox)
    game.physics.arcade.overlap(player.weapon2.bullets, enemies, bulletVsEnemy _, null, null)
    game.physics.arcade.overlap(player.weapon2.bullets, bonusManager.containers, bulletVsBonusContainer _, null, null)
    game.physics.arcade.overlap(player.weapon2.bullets, bonusManager.bonusoids, bulletVsBonusoid _, null, null)

    game.physics.arcade.collide(enemyMissiles, StatePlay.scorebox)
    game.physics.arcade.overlap(enemyMissiles, enemies, enemyMissileVsEnemy _, null, null)

    game.physics.arcade.collide(enemies, StatePlay.scorebox)

    game.physics.arcade.collide(bonusManager.bonusoids, StatePlay.scorebox)

    if (player.visible && StatePlay.scores.lives>0 && (bonusManager.allDead || enemies.countLiving()==0)) nextLevel()
  }

  def nextLevel(): Unit = {
    StatePlay.scores.stars = 1

    val timeBonusMsg: String = if (StatePlay.scores.timeBonus>0) {
      StatePlay.scores.stars += 1
      "* Field completed!\n* Time bonus achieved!\n"
    } else {
      "* Field completed!\n  Time bonus missed...\n"
    }

    val result: String = if (bonusManager.bonusoidCount == StatePlay.scores.bonusoidsCollected) {
      sfxLevelEnd.play()
      StatePlay.scores.stars += 1
      "* All Bonusoids collected!\n"
    }
    else {
      sfxLevelClr.play()
      val ratio = s"${StatePlay.scores.bonusoidsCollected}/${bonusManager.bonusoidCount}"
      s"  Only $ratio Bonusoids..."
    }
    clearLevel()
    Progress.updateAndSave(StatePlay.scores, debug)
    game.state.start("nextlevel", args = timeBonusMsg + result, clearCache = false, clearWorld = false)
  }

  def clearLevel(): Unit = {
    touch.disable()
    enemies.destroy()
    messages.clear()
    player.hide()
    bonusManager.bonusoids.destroy()
  }

  def bulletVsEnemy(bullet: Bullet, enemy: Sprite): Unit = {
    enemy match {
      case e: Enemy => StatePlay.scorebox.addToScore(e.bulletHit(bullet))
      case _ => Logger.warn(s"Unknown enemy $enemy")
    }
  }

  def bulletVsBonusContainer(bullet: Bullet, bonusoid: Sprite): Unit = {
    messages.show("Bonusoids released, go catch them!")
    Explosion(game, Explosion.SmallExploCount, bonusoid)
    bonusoid.kill()
    bullet.kill()
    StatePlay.scorebox.addToScore(1000)
  }

  def bulletVsBonusoid(bullet: Bullet, bonus: Sprite): Unit = {
    messages.show("Bonusoid destroyed!")
    Explosion(game, Explosion.TinyExploCount, bonus)
    bonus.kill()
    bullet.kill()
    StatePlay.scorebox.addToScore(100)
  }

  def playerVsBonusoid(player: Player, bonusoid: Sprite): Unit = {
    sfxCollect.play()
    bonusoid.kill()
    StatePlay.scorebox.addToBonusoidsCollected(1)
    StatePlay.scorebox.addToScore(2000)
    messages.show(s"Bonusoid collected!")
    player.maybeUpgradeShip(StatePlay.scores.totalBonusoids) match {
      case Some(level) =>
        messages.show(s"SHIP SYSTEMS UPGRADED TO LEVEL ${level+1}!")
        StatePlay.scores.shipLevel = level+1
      case None => // Nothing to do
    }
  }

  def playerVsEnemy(player: Player, enemy: Sprite): Unit = {
    if (player.immortal) {
      Explosion(game, Explosion.SmallExploCount, enemy)
      enemy.kill()
    }
    else {
      messages.show("SHIP LOST!")
      Explosion(game, Explosion.LargeExploCount, player)
      enemy.kill()
      player.kill()
      val timer = game.time.create(true)
      timer.add(3000, () => {
        StatePlay.scorebox.addToLives(-1)
        if (StatePlay.scores.lives<=0) handleGameOver() else player.revive()
      }, null)
      timer.start(0)
    }
  }

  def enemyMissileVsEnemy(enemy1: Enemy, enemy2: Enemy): Unit = {
    if (!enemy1.enemyHit(enemy2)) enemy2.enemyHit(enemy1)
  }

  def handleGameOver(): Unit = {
    gameOver = true
    clearLevel()
    Progress.updateAndSave(StatePlay.scores, debug)
    game.state.start("gameover", args = "gameover", clearCache = false, clearWorld = false)
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
    if (debug && k.isDown('U')) debugUpgrade()
  }

  def gotoMenu(): Unit = {
    Progress.updateAndSave(StatePlay.scores, debug)
    game.state.start("menu", args = "quit", clearCache = false, clearWorld = true)
  }

  def debugUpgrade(): Unit = {
    StatePlay.scorebox.addToBonusoidsCollected(1)
    player.maybeUpgradeShip(StatePlay.scores.totalBonusoids) match {
      case Some(level) => messages.show(s"Ship systems upgraded to level $level!")
      case None => // Nothing to do
    }
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