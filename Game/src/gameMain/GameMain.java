package gameMain;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import game2D.*;

@SuppressWarnings("serial")
public class GameMain extends GameCore {

	static int screenWidth = 1616;
	static int screenHeight = 896;

	int xo, yo;

	int level = 3;

	TileMap tMap = new TileMap();

	Sprite player = null;
	
	Sprite boss = null;

	float gravity = 0.0003f;

	long shootTime = 0;
	long shootDelay = 900;
	long enemyShootDelay = 1800;

	// Player Animations
	Animation idle;
	Animation walk;
	Animation attack;
	Animation jump;

	Animation bullet;
	
	Animation bossSprite;

	Image background;

	float playerX;
	float playerY;

	boolean jumping = false;
	boolean grounded = true;

	static GameMain gct;

	ArrayList<Sprite> enemyList = new ArrayList<Sprite>();
	ArrayList<Sprite> bulletList = new ArrayList<Sprite>();
	ArrayList<Sprite> enemyBulletList = new ArrayList<Sprite>();
	ArrayList<Sprite> bossList = new ArrayList<Sprite>();

	ArrayList<Sprite> clouds = new ArrayList<Sprite>();

	Color bg = new Color(106, 199, 199);

	public static void main(String[] args) {

		gct = new GameMain();
		gct.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gct.init();
		// Start in windowed mode with the given screen height and width]
		// gct.startScreen();

		// gct.setVisible(true);
		// gct.setSize(screenWidth, screenHeight);

		gct.run(false, screenWidth, screenHeight);

	}

	// start screen fix later
	public void startScreen() {

		JPanel pnlStartScreen = new JPanel();
		pnlStartScreen.setSize(screenWidth, screenHeight);
		pnlStartScreen.setVisible(true);

		JButton btnStart = new JButton();
		btnStart.setLocation(screenWidth, screenHeight);
		btnStart.setSize(200, 40);
		btnStart.setVisible(true);
		btnStart.addActionListener(new ActionListener() {
			;
			@Override
			public void actionPerformed(ActionEvent e) {
				pnlStartScreen.setVisible(false);
				gct.remove(pnlStartScreen);
				gct.run(false, screenWidth, screenHeight);

			}
		});

		gct.add(pnlStartScreen);
		pnlStartScreen.add(btnStart);
	}

	public void init() {

		Sprite s;

		background = new ImageIcon("images/Background.png").getImage();

		createAnimations();

		player = new Sprite(idle);

		player.setDirection('r');

		Animation ca = new Animation();
		ca.addFrame(loadImage("images/cloud.png"), 1000);

		for (int c = 0; c < 3; c++) {
			s = new Sprite(ca);
			s.setX(screenWidth + (int) (Math.random() * 200.0f));
			s.setY(30 + (int) (Math.random() * 150.0f));
			s.setVelocityX(-0.2f);
			s.show();
			clouds.add(s);
		}

		initialiseGame();

	}

	public void draw(Graphics2D g) {

		xo = Math.round(player.getX() - (screenWidth / 2));
		yo = Math.round(player.getY() - (screenHeight / 2));

		// g.drawImage(background, 0, 0, null);

		for (Sprite s : clouds) {
			s.setOffsets(-xo, -yo);
			s.draw(g);
		}

		g.setColor(bg);
		g.fillRect(0, 0, getWidth(), getHeight());

		switch (player.getDirection()) {

		case 'l':
			player.setOffsets(-xo, -yo);
			break;

		case 'r':
			player.setOffsets(-xo + player.getWidth(), -yo);
			break;
		}
		// player.draw(g);
		player.drawTransformed(g);

		if (!enemyList.isEmpty()) {
			for (int i = 0; i < enemyList.size(); i++) {

				switch (enemyList.get(i).getDirection()) {

				case 'l':
					enemyList.get(i).setOffsets(-xo, -yo);
					break;
				case 'r':
					enemyList.get(i).setOffsets(-xo + enemyList.get(i).getWidth(), -yo);
					break;
				}

				enemyList.get(i).drawTransformed(g);
			}
		}
		
		if (!(boss == null)) {
			

				switch (boss.getDirection()) {

				case 'l':
					boss.setOffsets(-xo, -yo);
					break;
				case 'r':
					boss.setOffsets(-xo + boss.getWidth(), -yo);
					break;
				}

				boss.drawTransformed(g);
			
		}

		if (!bulletList.isEmpty()) {
			for (int i = 0; i < bulletList.size(); i++) {
				bulletList.get(i).setOffsets(-xo, -yo);
				bulletList.get(i).draw(g);
			}
		}

		if (!enemyBulletList.isEmpty()) {
			for (int i = 0; i < enemyBulletList.size(); i++) {
				enemyBulletList.get(i).setOffsets(-xo, -yo);
				enemyBulletList.get(i).draw(g);
			}
		}

		tMap.draw(g, -xo, -yo);

	}

	private void initialiseGame() {



		initialiseLevel();
	}

	public void initialiseLevel() {

		switch (level) {
		case 1:
			loadLevelOne();
			break;
		case 2:
			loadLevelTwo();
			break;
		case 3:
			loadLevelThree();
			// endGame();
			stop();
			break;
		}
	}

	private void loadLevelOne() {

		tMap.loadMap("map", "level1.txt");

		Sprite enemyOne = new Sprite(walk);
		Sprite enemyTwo = new Sprite(walk);
		
		player.setX(704);
		player.setY(800);
		player.show();

		enemyOne.setX(1344);
		enemyOne.setY(673);
		enemyOne.show();
		enemyList.add(enemyOne);

		enemyTwo.setX(2400);
		enemyTwo.setY(800);
		enemyTwo.show();
		enemyList.add(enemyTwo);

		enemyOne.setDirection('l');
		enemyTwo.setDirection('l');

	}

	private void loadLevelTwo() {

		tMap.loadMap("map", "level2.txt");

		Sprite enemyOne = new Sprite(walk);
		Sprite enemyTwo = new Sprite(walk);
		Sprite enemyThree = new Sprite(walk);
		Sprite enemyFour = new Sprite(walk);
		Sprite enemyFive = new Sprite(walk);

		player.setX(704);
		player.setY(800);
		player.setVelocityX(0);
		player.setVelocityY(0);
		player.show();
		player.setDirection('r');

		enemyOne.setX(1344);
		enemyOne.setY(1184);
		enemyOne.show();
		enemyList.add(enemyOne);

		enemyTwo.setX(1800);
		enemyTwo.setY(1184);
		enemyTwo.show();
		enemyList.add(enemyTwo);

		enemyThree.setX(4160);
		enemyThree.setY(928);
		enemyThree.show();
		enemyList.add(enemyThree);

		enemyFour.setX(2500);
		enemyFour.setY(800);
		enemyFour.show();
		enemyList.add(enemyFour);

		enemyFive.setX(3000);
		enemyFive.setY(288);
		enemyFive.show();
		enemyList.add(enemyFive);

		enemyOne.setDirection('r');
		enemyTwo.setDirection('l');
		enemyThree.setDirection('r');
		enemyFour.setDirection('r');
		enemyFive.setDirection('l');

	}
	
	private void loadLevelThree(){
		
		tMap.loadMap("map", "level3.txt");
		
		boss = new BossSprite(bossSprite);
		
		player.setX(170);
		player.setY(475);
		player.setVelocityX(0);
		player.setVelocityY(0);
		player.show();
		
		
		boss.setX(1488);
		boss.setY(512);
		boss.show();
		bossList.add(boss);
		boss.setDirection('l');
	}

	private void endGame() {

		player.shiftY(-player.getHeight());

		int dialogButton = JOptionPane.YES_NO_OPTION;
		JOptionPane.showConfirmDialog(null, "Game complete, Well done! Restart?", "Nice!", dialogButton);

		if (dialogButton == JOptionPane.YES_OPTION) {
			level = 1;
			initialiseLevel();
		}
		if (dialogButton == JOptionPane.NO_OPTION) {
			stop();
		}

	}

	public void update(long elapsed) {

		player.setVelocityY(player.getVelocityY() + (gravity * elapsed));

		player.setAnimationSpeed(0.7f);

		for (Sprite s : clouds) {
			s.update(elapsed);
		}

		player.update(elapsed);

		handleTileMapCollisions(player, elapsed);

		if (!enemyList.isEmpty()) {
			for (int i = 0; i < enemyList.size(); i++) {
				enemyList.get(i).setVelocityY(enemyList.get(i).getVelocityY() + (gravity * elapsed));

				switch (enemyList.get(i).getDirection()) {

				case 'l':
					if (enemyList.get(i).getShooting() == true) {
						enemyList.get(i).setVelocityX(0);
						enemyList.get(i).setAnimation(idle);
					} else {
						enemyList.get(i).setVelocityX(-0.2f);
						enemyList.get(i).setAnimation(walk);
					}

					break;

				case 'r':
					if (enemyList.get(i).getShooting() == true) {
						enemyList.get(i).setVelocityX(0);
						enemyList.get(i).setAnimation(idle);
					} else {
						enemyList.get(i).setVelocityX(0.2f);
						enemyList.get(i).setAnimation(walk);
					}

					break;
				}
				enemyList.get(i).update(elapsed);
				enemyList.get(i).setAnimationSpeed(0.7f);
			}
			enemyCollsion(enemyList);
		}

		handleEnemyCollision(elapsed, enemyList);
		
		if (!(boss == null)) {
		
				boss.setVelocityY(boss.getVelocityY() + (gravity * elapsed));
				boss.update(elapsed);
				boss.setAnimationSpeed(0.7f);
			
			enemyCollsion(bossList);
			handleBossCollision(elapsed);
		}

	
		

		if (!bulletList.isEmpty()) {
			for (int i = 0; i < bulletList.size(); i++) {

				if (handleTileMapCollisions(bulletList.get(i), elapsed)) {
					deleteBullet(bulletList.get(i), bulletList);
				} else {
					bulletList.get(i).update(elapsed);
					bulletCollision(bulletList.get(i));
				}
			}
		}

		if (!enemyBulletList.isEmpty()) {
			for (int i = 0; i < enemyBulletList.size(); i++) {

				if (handleTileMapCollisions(enemyBulletList.get(i), elapsed)) {
					deleteBullet(enemyBulletList.get(i), enemyBulletList);
				} else {
					enemyBulletList.get(i).update(elapsed);
					enemyBulletCollision(enemyBulletList.get(i));
				}
			}
		}

		killBullets(bulletList);
		killBullets(enemyBulletList);
		checkLevelStatus();

	}

	public void mouseClicked(MouseEvent e) {

		if (gct.getCurrentTime() > shootTime) {
			shootTime = gct.getCurrentTime() + shootDelay;
			shootBullet(player);
		}

	}

	public void keyPressed(KeyEvent e) {

		int key = e.getKeyCode();

		if (key == KeyEvent.VK_ESCAPE) {
			stop();
		}

		if (key == KeyEvent.VK_UP) {

			if (jumping == false) {

				startJump();

			}
			jumping = true;

		}

		if (key == KeyEvent.VK_LEFT) {
			player.setVelocityX(-0.3f);
			player.setAnimation(walk);
			player.setScaleX(1);
			player.setDirection('l');

		}

		if (key == KeyEvent.VK_RIGHT) {
			player.setVelocityX(+0.3f);
			player.setAnimation(walk);
			player.setScaleX(-1);
			player.setDirection('r');

		}

		if (key == KeyEvent.VK_SPACE && gct.getCurrentTime() > shootTime) {
			shootTime = gct.getCurrentTime() + shootDelay;
			shootBullet(player);
		}

		e.consume();

	}

	public void keyReleased(KeyEvent e) {

		int key = e.getKeyCode();

		if (key == KeyEvent.VK_LEFT) {
			player.setVelocityX(0);
			player.setAnimation(idle);
		}

		if (key == KeyEvent.VK_RIGHT) {
			player.setVelocityX(0);
			player.setAnimation(idle);
		}

		e.consume();
	}

	private void createAnimations() {

		idle = new Animation();
		idle.loadAnimationFromSheet("playerSprite/Idle.png", 11, 1, 60);

		walk = new Animation();
		walk.loadAnimationFromSheet("playerSprite/Walk.png", 5, 2, 60);

		attack = new Animation();
		attack.loadAnimationFromSheet("playerSprite/Attack.png", 5, 2, 60);

		jump = new Animation();
		jump.loadAnimationFromSheet("playerSprite/Jump.png", 5, 2, 60);

		bullet = new Animation();
		bullet.loadAnimationFromSheet("images/Bullet.png", 1, 1, 60);
		
		bossSprite = new Animation();
		bossSprite.loadAnimationFromSheet("images/BossSprite.png", 1, 1, 60);

	}

	private void startJump() {

		grounded = false;
		player.setAnimation(jump);
		player.setVelocityY(-0.3f);

		Timer jumpTimer = new Timer();

		jumpTimer.schedule(new TimerTask() {
			@Override
			public void run() {

				player.setAnimation(idle);
			}
		}, 1000);

	}

	private void checkLevelStatus() {

		int x = Math.round(player.getX()) + 16;
		int y = Math.round(player.getY()) + 34;

		int tx = x / tMap.getTileWidth();
		int ty = y / tMap.getTileHeight();
		
		if (tMap.getTileChar(tx, ty) == 'n') {

			level++;
			randomWinSound();
			initialiseLevel();
		}


	}

	private void shootBullet(Sprite s) {

		Sprite bulletSprite = new Sprite(bullet);

		bulletSprite.setX(Math.round(s.getX() + 20));
		bulletSprite.setY(Math.round(s.getY() + 32));

		switch (s.getDirection()) {

		case 'l':
			bulletSprite.setVelocityX(-0.5f);
			break;

		case 'r':
			bulletSprite.setVelocityX(0.5f);
			break;
		}

		try {
			play("sounds/M4A1_Single.wav");
		} catch (Exception e) {
			System.out.println("errir");
			e.printStackTrace();
		}
		bulletSprite.show();
		bulletList.add(bulletSprite);
	}

	private void killBullets(ArrayList<Sprite> bullets) {

		for (int i = 0; i < bullets.size(); i++) {
			if (bullets.get(i).getX() > player.getX() + (screenWidth / 2) || bullets.get(i).getX() < player.getX() - (screenWidth/ 2)
					|| bullets.get(i).getY() < player.getY() - (screenHeight/ 2)) {
				bullets.get(i).hide();
				bullets.remove(i);
			}
		}
	}

	private void killPlayer() {

		enemyList.removeAll(enemyList);
		bulletList.removeAll(bulletList);
		enemyBulletList.removeAll(enemyBulletList);
		bossList.removeAll(bossList);
		if(!(boss == null)){
			boss.hide();
		}
		player.hide();
		// level = 1;
		randomFailSound();

		initialiseGame();
	}
	
	private void handleBossCollision(long elapsed){
		
		handleTileMapCollisions(boss, elapsed);
		
		if (inEnemyBox(boss, player)) {

			
			if(player.getX() < boss.getX()){
				boss.setDirection('l');
				boss.setScaleX(1);
			}
			if(player.getX() > boss.getX()){
				boss.setDirection('r');
				boss.setScaleX(-1);
			}
			//boss.setShooting(true);
			enemyShoot(boss);
		}
	}
	

	private void handleEnemyCollision(long elapsed, ArrayList<Sprite> l) {

		for (int i = 0; i < l.size(); i++) {

			handleTileMapCollisions(l.get(i), elapsed);

			int x = Math.round(l.get(i).getX()) + 16;
			int y = Math.round(l.get(i).getY()) + 32;

			int tx = x / tMap.getTileWidth();
			int ty = y / tMap.getTileHeight();

			if (inEnemyBox(l.get(i), player)) {

				
				if(player.getX() < l.get(i).getX()){
					l.get(i).setDirection('l');
					l.get(i).setScaleX(1);
				}
				if(player.getX() > l.get(i).getX()){
					l.get(i).setDirection('r');
					l.get(i).setScaleX(-1);
				}
				l.get(i).setShooting(true);
				enemyShoot(l.get(i));

			} else {
				l.get(i).setShooting(false);
				// change from right to left
				if (tMap.getTileChar(tx + 2, ty) != '.') {
					l.get(i).setDirection('l');
					l.get(i).setScaleX(1);

				}
				if (tMap.getTileChar(tx, ty + 2) == 'd') {
					l.get(i).setDirection('l');
					l.get(i).setScaleX(1);
				}

				// change from left to right
				if (tMap.getTileChar(tx - 1, ty) != '.') {
					l.get(i).setDirection('r');
					l.get(i).setScaleX(-1);

				}

				if (tMap.getTileChar(tx, ty + 2) == 'b') {
					l.get(i).setDirection('r');
					l.get(i).setScaleX(-1);

				}

			}
		}
	}

	private boolean inEnemyBox(Sprite s, Sprite c) {
		
		int edge = 300;
		
		if(s.equals(boss)){
			edge = 600;
		}

		if (((s.getX() + s.getImage().getWidth(null) + edge) >= c.getX())
				&& (s.getX() < (c.getX() + c.getImage().getWidth(null)) + edge)
				&& ((s.getY() + s.getImage().getHeight(null) + edge) >= c.getY())
				&& (s.getY() < (c.getY() + c.getImage().getHeight(null)) + edge)) {
			
			return true;
		}	
		else{
			return false;
		}
	}

	private void enemyShoot(Sprite s) {

		if (gct.getCurrentTime() > s.getShootTime()) {
			s.setShootTime(gct.getCurrentTime() + enemyShootDelay);
			enemyShootBullet(s);
		}
	}

	private void enemyShootBullet(Sprite s) {

		Sprite bulletSprite = new Sprite(bullet);

		bulletSprite.setX(Math.round(s.getX() + 20));
		bulletSprite.setY(Math.round(s.getY() + 32));
		
		double bulletVelocity = 0.5;
		
		
		//fires bullet towards players current position
	    double angle = Math.atan2(player.getX() - s.getX(), player.getY() - s.getY());
	    float xVelocity = (float) (bulletVelocity * Math.sin(angle));
	    float yVelocity = (float) (bulletVelocity * Math.cos(angle));
		bulletSprite.setVelocityX(xVelocity);
		bulletSprite.setVelocityY(yVelocity);

//		switch (s.getDirection()) {
//
//		case 'l':
//			bulletSprite.setVelocityX(-0.5f);
//			break;
//
//		case 'r':
//			bulletSprite.setVelocityX(0.5f);
//			break;
//		}

		try {
			play("sounds/M4A1_Single.wav");
		} catch (Exception e) {
			System.out.println("errir");
			e.printStackTrace();
		}
		bulletSprite.show();
		enemyBulletList.add(bulletSprite);
	}

	private boolean handleTileMapCollisions(Sprite s, long elapsed) {

		int x = Math.round(s.getX()) + 16;
		int	y = Math.round(s.getY()) + 32;
		
		if(s.equals(boss)){
			x = Math.round(s.getX()) + 236;
			y = Math.round(s.getY()) + 140;
		}

		int tx = x / tMap.getTileWidth();
		int ty = y / tMap.getTileHeight();

		Sprite collisionSprite = null;

		if (!bulletList.contains(s) && !enemyBulletList.contains(s)) {

			// right
			if ((tMap.getTileChar(tx + 1, ty) != '.') && (tMap.getTileChar(tx + 1, ty) != 'n') && (tMap.getTileChar(tx + 1, ty) != 'p')) {
				s.setVelocityX(-s.getVelocityX() * (0.01f * elapsed));
				s.shiftX(-2);
			}

			// left
			if ((tMap.getTileChar(tx, ty) != '.') && (tMap.getTileChar(tx, ty) != 'n') && (tMap.getTileChar(tx, ty) != 'p')) {
				s.setVelocityX(-s.getVelocityX() * (0.06f * elapsed));
				s.shiftX(+2);
			}

			// down
			if ((tMap.getTileChar(tx, ty + 1) != '.') && (tMap.getTileChar(tx, ty + 1) != 'n') && (tMap.getTileChar(tx, ty + 1) != 'p')) {
				s.setVelocityY(-s.getVelocityY() * (0.01f * elapsed));
				s.shiftY(-1);
				if (s.equals(player)) {
					grounded = true;
					jumping = false;
				}
			}
			// up
			if ((tMap.getTileChar(tx, ty) != '.')) {
				s.setVelocityY(-s.getVelocityY() * (0.01f * elapsed));
				s.shiftY(+1);
			}

			// right
			if (s.getVelocityX() > 0.0) {
				tx = tx + 1;
			}
			// down
			if (s.getVelocityY() > 0.0) {
				ty = ty + 1;
			}
			// up
			if (s.getVelocityY() < 0.0) {
				ty = ty - 1;
			}
			// left
			if (s.getVelocityX() < 0.0 && !(s.getVelocityY() < 0.0)) {
				tx = tx - 1;
			}
		}

		if ((tMap.getTileChar(tx, ty) != '.') && (tMap.getTileChar(tx, ty) != 'n') && (tMap.getTileChar(tx, ty) != 'p')) {

			Animation anim = new Animation();
			anim.addFrame(tMap.getTileImage(tx, ty), 60);
			collisionSprite = new Sprite(anim);
			collisionSprite.setX(tMap.getTileXC(tx, ty));
			collisionSprite.setY(tMap.getTileYC(tx, ty));

			if (boundingBoxes(s, collisionSprite)) {

				if (handleCollisions(s, collisionSprite)) {

					System.out.println("hit");

					// right
					if (s.getVelocityX() > 0.0) {

						s.setVelocityX(-s.getVelocityX() * (0.01f * elapsed));
						s.shiftX(-5);

					}
					// left
					if (s.getVelocityX() < 0.0) {

						s.setVelocityX(-s.getVelocityX() * (0.01f * elapsed));
						s.shiftX(5);

					}
					// down
					if (s.getVelocityY() > 0.0 && (tMap.getTileChar(tx, ty) == 'u' || tMap.getTileChar(tx, ty) == 'd'
							|| tMap.getTileChar(tx, ty) == 'b')) {

						s.setVelocityY(-s.getVelocityY() * (0.01f * elapsed));
						s.shiftY(-1);
						if (s.equals(player)) {
							grounded = true;
							jumping = false;
						}
					}

					// up
					if (s.getVelocityY() < 0.0) {
						s.setVelocityY(-s.getVelocityY() * (0.01f * elapsed));
						s.shiftY(-1);

					}

					// down, left
					if (s.getVelocityX() < 0.0 && s.getVelocityY() > 0.0) {
						s.setVelocityX(0);
					}

					// up, left
					if (s.getVelocityX() < 0.0 && s.getVelocityY() < 0.0) {
						s.setVelocityX(0);
					}

					return true;

				}
			}
		}



		return false;
	}

	private void bulletCollision(Sprite bullet) {

		for (int i = 0; i < enemyList.size(); i++) {

			if (((bullet.getX() + bullet.getImage().getWidth(null) - 30) >= enemyList.get(i).getX())
					&& (bullet.getX() < (enemyList.get(i).getX() + enemyList.get(i).getImage().getWidth(null)) - 30)
					&& ((bullet.getY() + bullet.getImage().getHeight(null) - 30) >= enemyList.get(i).getY())
					&& (bullet.getY() < (enemyList.get(i).getY() + enemyList.get(i).getImage().getHeight(null)) - 30)) {

				deleteBullet(bullet, bulletList);
				enemyList.get(i).hide();
				enemyList.remove(i);
				try {
					play("sounds/Male Grunt.wav");
				} catch (Exception e) {
					System.out.println("errir");
					e.printStackTrace();
				}

			}
		}
	}

	private void enemyBulletCollision(Sprite bullet) {

		if (((bullet.getX() + bullet.getImage().getWidth(null) - 15) >= player.getX())
				&& (bullet.getX() < (player.getX() + player.getImage().getWidth(null)) - 15)
				&& ((bullet.getY() + bullet.getImage().getHeight(null) - 15) >= player.getY())
				&& (bullet.getY() < (player.getY() + player.getImage().getHeight(null)) - 15)) {

			deleteBullet(bullet, enemyBulletList);
			//killPlayer();

			

		}

	}

	private boolean boundingBoxes(Sprite s1, Sprite s2) {
		if (((s1.getX() + s1.getImage().getWidth(null)) >= s2.getX())
				&& (s1.getX() < (s2.getX() + s2.getImage().getWidth(null)))
				&& ((s1.getY() + s1.getImage().getHeight(null)) >= s2.getY())
				&& (s1.getY() < (s2.getY() + s2.getImage().getHeight(null)))) {
			return true;
		}
		return false;
	}

	private void deleteBullet(Sprite bullet, ArrayList<Sprite> bullets) {

		bullet.hide();
		bullets.remove(bullet);
	}

	private void enemyCollsion(ArrayList<Sprite> l) {

		for (int i = 0; i < l.size(); i++) {

			if (((player.getX() + player.getImage().getWidth(null) - 30) >= l.get(i).getX())
					&& (player.getX() < (l.get(i).getX() + l.get(i).getImage().getWidth(null)) - 30)
					&& ((player.getY() + player.getImage().getHeight(null) - 30) >= l.get(i).getY())
					&& (player.getY() < (l.get(i).getY() + l.get(i).getImage().getHeight(null)) - 30)) {

				//killPlayer();

			}
		}
	}

	private boolean handleCollisions(Sprite s, Sprite c) {

		float dx, dy, minimum;

		dx = (s.getX() - c.getX()) / 64;

		dy = (s.getY() - c.getY()) / 64;

		minimum = s.getRadius() + c.getRadius();

		return (((dx * dx) + (dy * dy)) < (minimum * minimum));

	}

	private void randomFailSound() {
		String sound = "";

		Random r = new Random();
		int low = 1;
		int high = 5;
		int result = r.nextInt(high - low) + low;

		System.out.println(result);

		switch (result) {

		case 1:
			sound = ("sounds/demoman_kaboom.wav");
			break;

		case 2:
			sound = ("sounds/engineer_no01.wav");
			break;

		case 3:
			sound = ("sounds/sadtrombone.wav");
			break;

		case 4:
			sound = ("sounds/yanp.wav");
			break;
		}

		try {
			play(sound);
		} catch (Exception e) {
			System.out.println("errir");
			e.printStackTrace();
		}
	}

	private void randomWinSound() {

		String sound = "";

		Random r = new Random();
		int low = 1;
		int high = 5;
		int result = r.nextInt(high - low) + low;

		switch (result) {

		case 1:
			sound = ("sounds/lalalalala.wav");
			break;

		case 2:
			sound = ("sounds/victory_fanfare.wav");
			break;

		case 3:
			sound = ("sounds/that_was_easy.wav");
			break;

		case 4:
			sound = ("sounds/turn-down-for-whatlouder.wav");
			break;
		}

		try {
			play(sound);
		} catch (Exception e) {
			System.out.println("errir");
			e.printStackTrace();
		}
	}

	private void play(String filename) throws Exception {

		try {

			File file = new File(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			AudioFormat format = stream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();

			// clip.close();
		} catch (Exception e) {
			System.out.println("errir");
			e.printStackTrace();
		}

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

}
