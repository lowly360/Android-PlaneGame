package com.pg;


import java.util.Random;
import java.util.Vector;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

/**
 * 
 * @author Himi
 *
 */

public class MySurfaceView extends SurfaceView implements Callback, Runnable {
	private SurfaceHolder sfh;
	private Paint paint;
	private Paint paint2;
	private Thread th;
	private boolean flag;
	private Canvas canvas;
	public static int screenW, screenH;
	public static int Pause_flag = 0;
	
	//定义游戏状态常量
	public static final int GAME_MENU = 0;//游戏菜单
	public static final int GAMEING = 1;//游戏中
	public static final int GAME_WIN = 2;//游戏胜利
	public static final int GAME_LOST = 3;//游戏失败
	public static final int GAME_PAUSE = -1;//游戏菜单
	
	//当前游戏状态(默认初始在游戏菜单界面)
	public static int gameState = GAME_MENU;
	//声明一个Resources实例便于加载图片
	private Resources res = this.getResources();
	//声明游戏需要用到的图片资源(图片声明)
	private Bitmap bmpBackGround;//游戏背景
	private Bitmap bmpStart1;
	private Bitmap bmpStart2;
	private Bitmap bmpSound,bmpSound2;
	public static boolean soundFlag = true;
	
	private Bitmap bmpGamePause; //游戏暂停
	private Bitmap bmpPause_bg;
	private Bitmap bmpPause_back;
	private Bitmap bmpPause_continue;
	private Bitmap bmpPause_exit;
	
	public static Bitmap bmpPause_canvas; 
	public static Bitmap bmpPause_canvas2;
	
	private Bitmap bmpBoom;//爆炸效果
	private Bitmap bmpBoosBoom;//Boos爆炸效果
	private Bitmap bmpButton;//游戏开始按钮
	private Bitmap bmpButtonPress;//游戏开始按钮被点击
	private Bitmap bmpEnemyDuck;//怪物鸭子
	private Bitmap bmpEnemyFly;//怪物苍蝇
	private Bitmap bmpEnemyBoss;//怪物猪头Boss
	
	private Bitmap bmpEnemyNew;
	private Bitmap bmpEnemyWeapon;
	
	private Bitmap bmpGameWin;//游戏胜利背景
	private Bitmap bmpGameLost;//游戏失败背景
	
	private Bitmap bmpPlayer;//游戏主角飞机
	private Bitmap bmpPlayerHp;//主角飞机血量
	
	public static int playerWeaponLevel = 1; 
	
	private Bitmap bmpMenu;//菜单背景
	public static Bitmap bmpBullet,bmpBullet2;//子弹
	public static Bitmap bmpEnemyBullet;//敌机子弹
	public static Bitmap bmpBossBullet;//Boss子弹
	//声明一个菜单对象
	private GameMenu gameMenu;
	//声明一个滚动游戏背景对象
	private GameBg backGround;
	
	private GamePause gamePause;
	
	public static SoundIcon soundIcon;
	
	//声明主角对象
	private Player player;
	//声明一个敌机容器
	private Vector<Enemy> vcEnemy;
	//每次生成敌机的时间(毫秒)
	private int createEnemyTime = 50;
	private int count;//计数器
	
	
	//敌人数组：1和2表示敌机的种类，-1表示Boss
	//二维数组的每一维都是一组怪物
	
	private int enemyArray[][] = {{1,2,3,4},{2,1,4,2},{2,3,2,3},{1,1,1},{5,2} ,{ 2, 1 ,5}, { -1 } };
	//当前取出一维数组的下标
	public static int enemyArrayIndex;
	//是否出现Boss标识位
	private boolean isBoss;
	//随机库，为创建的敌机赋予随即坐标
	private Random random;
	//敌机子弹容器
	private Vector<Bullet> vcBullet;
	//添加子弹的计数器
	private int countEnemyBullet;
	//主角子弹容器
	private Vector<Bullet> vcBulletPlayer;
	//添加子弹的计数器
	private int countPlayerBullet;
	//爆炸效果容器
	private Vector<Boom> vcBoom;
	//声明Boss
	private Boss boss;
	//Boss的子弹容器
	public static Vector<Bullet> vcBulletBoss;
	
	//音效 测试
	public static SoundPool sp;
	//soundId_long为爆炸声音， shoot为主角开炮声， enemy_shoot为敌人开炮声
	public static int soundId_long,shoot,enemy_shoot;
	
	//背景音乐
	
	public static MediaPlayer mediaPlayer;
	public static MediaPlayer mediaPlayer2;
	private AudioManager am;
	
	
	private Bitmap bmpclip;
	private  Canvas canvasclip;

	/**
	 * SurfaceView初始化函数
	 */
	public MySurfaceView(Context context) {
		super(context);
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint2 = new Paint();
		paint2.setColor(Color.WHITE);
		paint2.setAntiAlias(true);
			
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		sp = new SoundPool(4, AudioManager.STREAM_MUSIC,100);
		soundId_long = sp.load(context,R.raw.boom,1);
		shoot = sp.load(context, R.raw.shoot,1);
		enemy_shoot = sp.load(context, R.raw.enemy_shoot,1);
			
		//设置背景常亮
		this.setKeepScreenOn(true);
	}

	/**
	 * SurfaceView视图创建，响应此函数
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		screenW = this.getWidth();
		screenH = this.getHeight();
		
		mediaPlayer = MediaPlayer.create(getContext(), R.raw.bg1);
		mediaPlayer2 = MediaPlayer.create(getContext(), R.raw.bg2);
		mediaPlayer.setLooping(true);
		mediaPlayer2.setLooping(true);
		
		bmpclip = Bitmap.createBitmap(this.getWidth(),this.getHeight(),Config.ARGB_8888);
		canvasclip = new Canvas(bmpclip);
		canvasclip.drawColor(Color.WHITE);
		
		am = (AudioManager)MainActivity.instance.getSystemService(Context.AUDIO_SERVICE);

		initGame();
		flag = true;
		//实例线程
		th = new Thread(this);
		//启动线程
		th.start();
	}

	/*
	 * 自定义的游戏初始化函数
	 */
void initGame() {
		//放置游戏切入后台重新进入游戏时，游戏被重置!
		//当游戏状态处于菜单时，才会重置游戏
		if (gameState == GAME_MENU) {
			//加载游戏资源
			bmpBackGround = BitmapFactory.decodeResource(res, R.drawable.bg3);
			bmpStart1 = BitmapFactory.decodeResource(res, R.drawable.start1);	
			bmpStart1 = BitmapFactory.decodeResource(res, R.drawable.start2);	
			
			bmpSound = BitmapFactory.decodeResource(res,R.drawable.sound);
			bmpSound2 = BitmapFactory.decodeResource(res,R.drawable.sound2);
						
			bmpGamePause = BitmapFactory.decodeResource(res, R.drawable.gamepause);
			bmpPause_bg = BitmapFactory.decodeResource(res, R.drawable.pause_bg);
			bmpPause_back = BitmapFactory.decodeResource(res, R.drawable.pause_back);
			bmpPause_continue = BitmapFactory.decodeResource(res, R.drawable.pause_continue);
			bmpPause_exit = BitmapFactory.decodeResource(res, R.drawable.pause_exit);
			
			bmpPause_canvas = Bitmap.createBitmap(screenW,screenH,Bitmap.Config.ARGB_8888);
			
			bmpBoom = BitmapFactory.decodeResource(res, R.drawable.boom);
			bmpBoosBoom = BitmapFactory.decodeResource(res, R.drawable.boos_boom);
			bmpButton = BitmapFactory.decodeResource(res, R.drawable.button);
			bmpButtonPress = BitmapFactory.decodeResource(res, R.drawable.button_press);
			bmpEnemyDuck = BitmapFactory.decodeResource(res, R.drawable.enemy_duck2);
			bmpEnemyFly = BitmapFactory.decodeResource(res, R.drawable.enemy_fly2);
			bmpEnemyBoss = BitmapFactory.decodeResource(res, R.drawable.enemy_boss);
			//新添加的敌人
			bmpEnemyNew = BitmapFactory.decodeResource(res, R.drawable.enemy_new);
			bmpEnemyWeapon = BitmapFactory.decodeResource(res, R.drawable.enemy_fly);
			
			
			bmpGameWin = BitmapFactory.decodeResource(res, R.drawable.gamewin);
			bmpGameLost = BitmapFactory.decodeResource(res, R.drawable.gamelost);
			bmpPlayer = BitmapFactory.decodeResource(res, R.drawable.player);
			bmpPlayerHp = BitmapFactory.decodeResource(res, R.drawable.hp);
			bmpMenu = BitmapFactory.decodeResource(res, R.drawable.menu);
			bmpBullet = BitmapFactory.decodeResource(res, R.drawable.bullet);
			bmpBullet2 = BitmapFactory.decodeResource(res, R.drawable.bullet2);
			
			bmpEnemyBullet = BitmapFactory.decodeResource(res, R.drawable.bullet_enemy);
			bmpBossBullet = BitmapFactory.decodeResource(res, R.drawable.boosbullet);
			//爆炸效果容器实例
			vcBoom = new Vector<Boom>();
			//敌机子弹容器实例
			vcBullet = new Vector<Bullet>();
			//主角子弹容器实例
			vcBulletPlayer = new Vector<Bullet>();
			
			//菜单类实例
			gameMenu = new GameMenu(bmpMenu, bmpButton, bmpButtonPress,bmpStart1,bmpStart2);
			//暂停菜单实例
			gamePause = new GamePause(bmpPause_bg,bmpPause_back,bmpPause_continue,bmpPause_exit);
			//静音键实例
			soundIcon = new SoundIcon(bmpSound, bmpSound2);
			
			//实例游戏背景
			backGround = new GameBg(bmpBackGround,bmpGamePause);
			//实例主角
			player = new Player(bmpPlayer, bmpPlayerHp);
			//实例敌机容器
			vcEnemy = new Vector<Enemy>();
			//实例随机库
			random = new Random();
			//---Boss相关
			//实例boss对象
			boss = new Boss(bmpEnemyBoss);
			//实例Boss子弹容器
			vcBulletBoss = new Vector<Bullet>();
		}
	}

	/**
	 * 游戏绘图
	 */
	public void myDraw() {
		try {
			canvas = sfh.lockCanvas();
			if (canvas != null ) {
				canvas.drawColor(Color.WHITE);
				
				//绘图函数根据游戏状态不同进行不同绘制
				switch (gameState) {
				case GAME_MENU:
					//菜单的绘图函数
					if(Pause_flag == 1){
						initGame();
						//重置怪物出场
						isBoss = false;
						playerWeaponLevel = 1;
						enemyArrayIndex = 0;
						Pause_flag = 0;
					}
					if(!soundFlag)
						mediaPlayer.start();
					
					gameMenu.draw(canvas, paint);					
					soundIcon.draw(canvas, paint);
					break;
				case GAMEING:
					//游戏背景，画布canvasclip是实时截图，为了暂停后，描绘后面的背景，见书本283
					
					if(!soundFlag)
						mediaPlayer2.start();
					
					backGround.draw(canvas, paint);			
					backGround.draw(canvasclip, paint);
					
					//主角绘图函数
			
					player.draw(canvas, paint);
					player.draw(canvasclip, paint);
					
					if (isBoss == false) {
						//敌机绘制
						for (int i = 0; i < vcEnemy.size(); i++) {
							vcEnemy.elementAt(i).draw(canvas, paint);
							vcEnemy.elementAt(i).draw(canvasclip, paint);
						}
						//敌机子弹绘制
						for (int i = 0; i < vcBullet.size(); i++) {
							vcBullet.elementAt(i).draw(canvas, paint);
							vcBullet.elementAt(i).draw(canvasclip, paint);
						}
					} else {
						//Boos的绘制
					
						boss.draw(canvas, paint);
						boss.draw(canvasclip, paint);
						
						//Boss子弹逻辑
						for (int i = 0; i < vcBulletBoss.size(); i++) {
							vcBulletBoss.elementAt(i).draw(canvas, paint);
							vcBulletBoss.elementAt(i).draw(canvasclip, paint);
						}
					}
					//处理主角子弹绘制
					for (int i = 0; i < vcBulletPlayer.size(); i++) {
					
						vcBulletPlayer.elementAt(i).draw(canvas, paint);
						vcBulletPlayer.elementAt(i).draw(canvasclip, paint);
						
					}
					//爆炸效果绘制
					for (int i = 0; i < vcBoom.size(); i++) {
					
						vcBoom.elementAt(i).draw(canvas, paint);						
						vcBoom.elementAt(i).draw(canvasclip, paint);
						
					}		            
					
					break;
				case GAME_PAUSE:
					
					paint2.setAlpha(245);
					bmpPause_canvas2 = fastblur(bmpclip, 4);
					canvas.drawBitmap(bmpPause_canvas2, 0, 0,paint);
					
					gamePause.draw(canvas, paint2);
					
					soundIcon.draw(canvas, paint);
					break;
					
				case GAME_WIN:
					canvas.drawBitmap(bmpGameWin, 0, 0, paint);
					break;
				case GAME_LOST:
					canvas.drawBitmap(bmpGameLost, 0, 0, paint);
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (canvas != null ){
				sfh.unlockCanvasAndPost(canvas);
			}
		}
	}

	/**
	 * 触屏事件监听
	 */
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//触屏监听事件函数根据游戏状态不同进行不同监听
		switch (gameState) {
		case GAME_MENU:
			//菜单的触屏事件处理
			gameMenu.onTouchEvent(event);
			soundIcon.onTouchEvent(event);
			break;
		case GAMEING:
			player.onTouchEvent(event);
			backGround.onTouchEvent(event);						
			break;
		case GAME_PAUSE:
			soundIcon.onTouchEvent(event);
			gamePause.onTouchEvent(event);
			break;
		case GAME_WIN:
			break;
		case GAME_LOST:

			break;
		}
		return true;
	}

	/**
	 * 按键按下事件监听
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//处理back返回按键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//游戏胜利、失败、进行时都默认返回菜单
			if (gameState == GAMEING || gameState == GAME_WIN || gameState == GAME_LOST) {
				gameState = GAME_MENU;
				//Boss状态设置为没出现
				isBoss = false;
				//重置游戏
				initGame();
				//重置怪物出场
				enemyArrayIndex = 0;
			} else if (gameState == GAME_MENU) {
				//当前游戏状态在菜单界面，默认返回按键退出游戏
				MainActivity.instance.finish();
				System.exit(0);
			}
			//表示此按键已处理，不再交给系统处理，
			//从而避免游戏被切入后台
			return true;
		}
		//按键监听事件函数根据游戏状态不同进行不同监听
		switch (gameState) {
		case GAME_MENU:
			break;
		case GAMEING:
			//主角的按键按下事件
			player.onKeyDown(keyCode, event);
			break;
		case GAME_PAUSE:
			break;
		case GAME_WIN:
			break;
		case GAME_LOST:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 按键抬起事件监听
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//处理back返回按键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//游戏胜利、失败、进行时都默认返回菜单
			if (gameState == GAMEING || gameState == GAME_WIN || gameState == GAME_LOST) {
				gameState = GAME_MENU;
			}
			//表示此按键已处理，不再交给系统处理，
			//从而避免游戏被切入后台
			return true;
		}
		//按键监听事件函数根据游戏状态不同进行不同监听
		switch (gameState) {
		case GAME_MENU:
			break;
		case GAMEING:
			//按键抬起事件
			player.onKeyUp(keyCode, event);
			break;
		case GAME_PAUSE:
			break;
		case GAME_WIN:
			break;
		case GAME_LOST:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 游戏逻辑
	 */
	private void logic() {
		//逻辑处理根据游戏状态不同进行不同处理
		switch (gameState) {
		case GAME_MENU:		
			break;
		case GAMEING:
			//背景逻辑
			backGround.logic();
			//主角逻辑
			player.logic();
			//敌机逻辑
			if (isBoss == false) {
				//敌机逻辑
				for (int i = 0; i < vcEnemy.size(); i++) {
					Enemy en = vcEnemy.elementAt(i);
					//因为容器不断添加敌机 ，那么对敌机isDead判定，
					//如果已死亡那么就从容器中删除,对容器起到了优化作用；
					if (en.isDead) {
						vcEnemy.removeElementAt(i);
					} else {
						en.logic();
					}
				}
				//生成敌机
				count++;
				if (count % createEnemyTime == 0) {
					for (int i = 0; i < enemyArray[enemyArrayIndex].length; i++) {
						//苍蝇
						if (enemyArray[enemyArrayIndex][i] == 1) {
							int x = random.nextInt(screenW - 100) + 50;
							vcEnemy.addElement(new Enemy(bmpEnemyFly, 1, x, -50));
							//鸭子左
						} else if (enemyArray[enemyArrayIndex][i] == 2) {
							int y = random.nextInt(30);
							vcEnemy.addElement(new Enemy(bmpEnemyDuck, 2, -50, y));
							//鸭子右
						} else if (enemyArray[enemyArrayIndex][i] == 3) {
							int y = random.nextInt(45);
							vcEnemy.addElement(new Enemy(bmpEnemyDuck, 3, screenW + 50, y));
						}else if (enemyArray[enemyArrayIndex][i] == 4) {
							int x = random.nextInt(screenW-50);
							vcEnemy.addElement(new Enemy(bmpEnemyNew, 4,x,-50));
						}else if (enemyArray[enemyArrayIndex][i] == 5) {
							int x = random.nextInt(screenW - 100) + 50;
							vcEnemy.addElement(new Enemy(bmpEnemyWeapon, 5, x, -50));
						}
					}
					//这里判断下一组是否为最后一组(Boss)
					if (enemyArrayIndex == enemyArray.length - 1) {
						isBoss = true;
					} else {
						enemyArrayIndex++;
					}
				}
				//处理敌机与主角的碰撞
				for (int i = 0; i < vcEnemy.size(); i++) {
					if (player.isCollsionWith(vcEnemy.elementAt(i))) {
						//发生碰撞，主角血量-1
						if (vcEnemy.elementAt(i).type == 5){
							if(playerWeaponLevel<=3)
								playerWeaponLevel++;
						}
						else{
							player.setPlayerHp(player.getPlayerHp() - 1);
							if(playerWeaponLevel>1)
								playerWeaponLevel--;
							if(!soundFlag){
								sp.play(soundId_long, 1f, 1f, 0, 0, 1);
							}
						}
						
						//当主角血量小于0，判定游戏失败
						if (player.getPlayerHp() <= -1) {
							gameState = GAME_LOST;
						}
					}
				}
				//每1秒添加一个敌机子弹
				countEnemyBullet++;
				if (countEnemyBullet % 15 == 0) {
					for (int i = 0; i < vcEnemy.size(); i++) {
						Enemy en = vcEnemy.elementAt(i);
						//不同类型敌机不同的子弹运行轨迹
						int bulletType = 0;
						switch (en.type) {
						//苍蝇
						case Enemy.TYPE_FLY:
							bulletType = Bullet.BULLET_FLY;
							vcBullet.add(new Bullet(bmpEnemyBullet, en.x + 10, en.y + 20, bulletType));
							if(!soundFlag){
								sp.play(enemy_shoot, 1f, 1f, 0, 0, 1);
							}
							break;
						//鸭子
						case Enemy.TYPE_DUCKL:
							bulletType = Bullet.BULLET_DUCK;
							vcBullet.add(new Bullet(bmpEnemyBullet, en.x + 10, en.y + 20, bulletType));
							if(!soundFlag){
								sp.play(enemy_shoot, 1f, 1f, 0, 0, 1);
							}
							break;
						case Enemy.TYPE_DUCKR:
							bulletType = Bullet.BULLET_DUCK;
							vcBullet.add(new Bullet(bmpEnemyBullet, en.x + 10, en.y + 20, bulletType));
							if(!soundFlag){
								sp.play(enemy_shoot, 1f, 1f, 0, 0, 1);
							}
							break;
						case Enemy.TYPE_NEW:
							bulletType = Bullet.BULLET_BOSS;
							vcBullet.add(new Bullet(bmpBossBullet, en.x + 10, en.y + 20, bulletType,Bullet.DIR_DOWN_LEFT));
							if(!soundFlag){
								sp.play(enemy_shoot, 1f, 1f, 0, 0, 1);
							}
							vcBullet.add(new Bullet(bmpBossBullet, en.x + 10, en.y + 20, bulletType,Bullet.DIR_DOWN));
							if(!soundFlag){
								sp.play(enemy_shoot, 1f, 1f, 0, 0, 1);
							}
							vcBullet.add(new Bullet(bmpBossBullet, en.x + 10, en.y + 20, bulletType,Bullet.DIR_DOWN_RIGHT));
							if(!soundFlag){
								sp.play(enemy_shoot, 1f, 1f, 0, 0, 1);
							}		
							break;
						case Enemy.TYPE_WEAPON:
							break;
						}
						
					}
				}
				//处理敌机子弹逻辑
				for (int i = 0; i < vcBullet.size(); i++) {
					Bullet b = vcBullet.elementAt(i);
					if (b.isDead) {
						vcBullet.removeElement(b);
					} else {
						b.logic();
					}
				}
				//处理敌机子弹与主角碰撞
				for (int i = 0; i < vcBullet.size(); i++) {
					if (player.isCollsionWith(vcBullet.elementAt(i))) {
						//发生碰撞，主角血量-1
						player.setPlayerHp(player.getPlayerHp() - 1);
						if(playerWeaponLevel>1)
							playerWeaponLevel--;
						//当主角血量小于0，判定游戏失败
						if (player.getPlayerHp() <= -1) {
							gameState = GAME_LOST;
						}
					}
				}
				//处理主角子弹与敌机碰撞
				for (int i = 0; i < vcBulletPlayer.size(); i++) {
					//取出主角子弹容器的每个元素
					Bullet blPlayer = vcBulletPlayer.elementAt(i);
					for (int j = 0; j < vcEnemy.size(); j++) {
						//添加爆炸效果
						//取出敌机容器的每个元与主角子弹遍历判断
						if (vcEnemy.elementAt(j).isCollsionWith(blPlayer)) {
							vcBoom.add(new Boom(bmpBoom, vcEnemy.elementAt(j).x, vcEnemy.elementAt(j).y, 7));
							if(!soundFlag){
								sp.play(soundId_long, 1f, 1f, 0, 0, 1);
							}
						}
					}
				}
			} else {//Boss相关逻辑
				//每0.25秒添加一个主角子弹
				boss.logic();
				if (boss.hp>=67&&countPlayerBullet % 13 == 0) {
					//Boss的没发疯之前的普通子弹
					vcBulletBoss.add(new Bullet(bmpBossBullet, boss.x+50, boss.y+30, Bullet.BULLET_FLY));
					if(!soundFlag){
						sp.play(enemy_shoot, 1f, 1f, 0, 0, 1);
					}
				}
				else if(67>boss.hp&&boss.hp>=34&&countPlayerBullet % 8 == 0){
						vcBulletBoss.add(new Bullet(bmpEnemyBullet, boss.x+50, boss.y+30, Bullet.BULLET_BOSS, Bullet.DIR_DOWN_Random));			
						if(!MySurfaceView.soundFlag)
							MySurfaceView.sp.play(MySurfaceView.enemy_shoot, 1f, 0.5f, 0, 0, 1);
				}
				else if(34>boss.hp&&countPlayerBullet % 13 == 0){			
					vcBulletBoss.add(new Bullet(bmpBossBullet, boss.x+50, boss.y+10, Bullet.BULLET_BOSS, Bullet.DIR_DOWN));
					vcBulletBoss.add(new Bullet(bmpBossBullet, boss.x+50, boss.y+10, Bullet.BULLET_BOSS, Bullet.DIR_DOWN_LEFT));
					vcBulletBoss.add(new Bullet(bmpBossBullet, boss.x+50, boss.y+10, Bullet.BULLET_BOSS, Bullet.DIR_DOWN_RIGHT));
					if(!MySurfaceView.soundFlag)
						MySurfaceView.sp.play(MySurfaceView.enemy_shoot, 1f, 0.5f, 0, 0, 1);
			}
				if(countPlayerBullet % 100 == 0)
					Bullet.flag = -Bullet.flag;
					
				
				//Boss子弹逻辑
				for (int i = 0; i < vcBulletBoss.size(); i++) {
					Bullet b = vcBulletBoss.elementAt(i);
					if (b.isDead) {
						vcBulletBoss.removeElement(b);
					} else {
						b.logic();
					}
				}
				//Boss子弹与主角的碰撞
				for (int i = 0; i < vcBulletBoss.size(); i++) {
					if (player.isCollsionWith(vcBulletBoss.elementAt(i))) {
						//发生碰撞，主角血量-1
						player.setPlayerHp(player.getPlayerHp() - 1);
						if(playerWeaponLevel>1)
							playerWeaponLevel--;
						//当主角血量小于0，判定游戏失败
						if (player.getPlayerHp() <= -1) {
							gameState = GAME_LOST;
						}
					}
				}
				//Boss被主角子弹击中，产生爆炸效果
				for (int i = 0; i < vcBulletPlayer.size(); i++) {
					Bullet b = vcBulletPlayer.elementAt(i);
					if (boss.isCollsionWith(b)) {
						if(!soundFlag){
							sp.play(soundId_long, 1f, 1f, 0, 0, 1);
						}
						if (boss.hp <= 0) {
							//游戏胜利
							gameState = GAME_WIN;
						} else {
							//及时删除本次碰撞的子弹，防止重复判定此子弹与Boss碰撞、
							b.isDead = true;
							//Boss血量减1
							boss.setHp(boss.hp - 1);
							//在Boss上添加三个Boss爆炸效果
							vcBoom.add(new Boom(bmpBoosBoom, boss.x + 15, boss.y + 25, 5));
							vcBoom.add(new Boom(bmpBoosBoom, boss.x + 35, boss.y + 40, 5));
							vcBoom.add(new Boom(bmpBoosBoom, boss.x + 65, boss.y + 20, 5));
						}
					}
				}
			}
			//每0.5秒添加一个主角子弹
			countPlayerBullet++;
			if (countPlayerBullet % 10 == 0) {
				if(playerWeaponLevel == 1)
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x - bmpPlayer.getWidth()/5, player.y -15 , Bullet.BULLET_PLAYER));
				else if(playerWeaponLevel == 2){
					vcBulletPlayer.add(new Bullet(bmpBullet2, player.x - bmpPlayer.getWidth()/5, player.y -15, Bullet.BULLET_PLAYER,Bullet.DIR_UP_LEFT2));
					vcBulletPlayer.add(new Bullet(bmpBullet2, player.x - bmpPlayer.getWidth()/5, player.y -15, Bullet.BULLET_PLAYER,Bullet.DIR_UP_RIGHT2));
				}else if(playerWeaponLevel == 3){
					vcBulletPlayer.add(new Bullet(bmpBullet2, player.x - bmpPlayer.getWidth()/5, player.y -15, Bullet.BULLET_PLAYER,Bullet.DIR_UP_LEFT2));
					vcBulletPlayer.add(new Bullet(bmpBullet2, player.x - bmpPlayer.getWidth()/5, player.y -15, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet2, player.x - bmpPlayer.getWidth()/5, player.y -15, Bullet.BULLET_PLAYER,Bullet.DIR_UP_RIGHT2));
				}
				if(!soundFlag){
					sp.setVolume(shoot, 0.5f, 0.5f);
					sp.play(shoot, 0.8f, 0.8f, 0, 0, 1);
				}
			}
			//处理主角子弹逻辑
			for (int i = 0; i < vcBulletPlayer.size(); i++) {
				Bullet b = vcBulletPlayer.elementAt(i);
				if (b.isDead) {
					vcBulletPlayer.removeElement(b);
				} else {
					b.logic();
				}
			}
			//爆炸效果逻辑
			for (int i = 0; i < vcBoom.size(); i++) {
				Boom boom = vcBoom.elementAt(i);
				if (boom.playEnd) {
					//播放完毕的从容器中删除
					vcBoom.removeElementAt(i);
				} else {
					vcBoom.elementAt(i).logic();
				}
			}
			break;
		case GAME_PAUSE:
			
			break;
		case GAME_WIN:
			break;
		case GAME_LOST:
			break;
		}
	}

	@Override
	public void run() {
		while (flag) {
			long start = System.currentTimeMillis();
			myDraw();
			logic();
			long end = System.currentTimeMillis();
			try {
				if (end - start < 50) {
					Thread.sleep(50 - (end - start));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//背景模糊函数，百度回来的，原理不懂
	private Bitmap fastblur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

	/**
	 * SurfaceView视图状态发生改变，响应此函数
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	/**
	 * SurfaceView视图消亡时，响应此函数
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
	}
}
