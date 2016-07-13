# PlaneGame
Android 游戏--- 一款飞机小游戏

大二学习的android游戏编程实训作品，通过这个作品加深了对 SurfaceView 的初步认识，以及游戏状态机制认识。

## 游戏状态机制

* 分为五部分

		public static final int GAME_MENU = 0;//游戏菜单
		public static final int GAMEING = 1;//游戏中
		public static final int GAME_WIN = 2;//游戏胜利
		public static final int GAME_LOST = 3;//游戏失败
		public static final int GAME_PAUSE = -1;//游戏菜单

* 整个游戏画面都是由MySurfaceView（不断刷新的View）来绘制
		
		@Override
			public void run() {  // 这里是MySurfaceview 线程 运行逻辑
				while (flag) {
					long start = System.currentTimeMillis();
					myDraw();  // 根据状态绘制
					logic();   // 游戏中敌机，主角，子弹逻辑
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

## 部分截图

<img src="https://raw.githubusercontent.com/lowly360/PlaneGame/18226060f505bc2cc989618e28f0d8e91ea7910d/srceenshot/menu.png" width = "240" height = "360" alt="菜单" align=center />

<img src="https://raw.githubusercontent.com/lowly360/PlaneGame/18226060f505bc2cc989618e28f0d8e91ea7910d/srceenshot/menu_stop.png" width = "240" height = "360" alt="" align=center />

<img src="https://raw.githubusercontent.com/lowly360/PlaneGame/18226060f505bc2cc989618e28f0d8e91ea7910d/srceenshot/play1.png" width = "240" height = "360" alt="" align=center />

<img src="https://raw.githubusercontent.com/lowly360/PlaneGame/18226060f505bc2cc989618e28f0d8e91ea7910d/srceenshot/play2.png" width = "240" height = "360" alt="" align=center />

<img src="https://raw.githubusercontent.com/lowly360/PlaneGame/18226060f505bc2cc989618e28f0d8e91ea7910d/srceenshot/play3.png" width = "240" height = "360" alt="" align=center />