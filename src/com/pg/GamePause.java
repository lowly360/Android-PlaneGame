package com.pg;

import android.R.bool;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * @author Himi
 *
 */
public class GamePause {
	//菜单背景图
	private Bitmap bmpPause_bg;
	//按钮图片资源(按下和未按下图)
	private Bitmap bmpButton, bmpButtonPress,bmpPause_continue,bmpPause_exit;
	//按钮的坐标
	private int btnX, btnY,btn_continue_x,btn_continue_y,btn_exit_x,btn_exit_y;
	//按钮是否按下标识位
	private Boolean isPress,flag;
	//菜单初始化
	public GamePause(Bitmap bmpPause_bg,Bitmap bmpPause_back,Bitmap bmpPause_continue,Bitmap bmpPause_exit) {
		this.bmpPause_bg = bmpPause_bg;
		this.bmpButton = bmpPause_back;
		this.bmpPause_continue = bmpPause_continue;
		this.bmpPause_exit = bmpPause_exit;
		
		btn_continue_x = MySurfaceView.screenW / 2 - bmpPause_continue.getWidth() / 2;
		btn_continue_y = MySurfaceView.screenH *2/5- bmpPause_continue.getHeight();
		
		btnX = MySurfaceView.screenW / 2 - bmpButton.getWidth() / 2;
		btnY = MySurfaceView.screenH *4/6- bmpButton.getHeight();

		
		btn_exit_x = MySurfaceView.screenW / 2 - bmpPause_exit.getWidth() / 2;
		btn_exit_y = MySurfaceView.screenH*5/6- bmpPause_exit.getHeight();
	
		isPress = false;
	}
	//菜单绘图函数
	public void draw(Canvas canvas, Paint paint) {
		//绘制菜单背景图
		canvas.drawBitmap(bmpPause_bg, 0, 0, paint);
		//绘制未按下按钮图
		if (isPress) {//根据是否按下绘制不同状态的按钮图
			canvas.drawBitmap(bmpButtonPress, btnX, btnY, paint);
		} else {
			canvas.drawBitmap(bmpButton, btnX, btnY, paint);
		}
		canvas.drawBitmap(bmpPause_continue, btn_continue_x,btn_continue_y, paint);
		canvas.drawBitmap(bmpPause_exit, btn_exit_x,btn_exit_y, paint);
	}
	//菜单触屏事件函数，主要用于处理按钮事件
	public void onTouchEvent(MotionEvent event) {
		//获取用户当前触屏位置
		int pointX = (int) event.getX();
		int pointY = (int) event.getY();
		//当用户是按下动作或移动动作
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			//判定用户是否点击了按钮
			if (pointX > btnX && pointX < btnX + bmpButton.getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton.getHeight()) {
					isPress = true;
				} else {
					isPress = false;
				}
			} else {
				isPress = false;
			}
			//当用户是抬起动作
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			//抬起判断是否点击按钮，防止用户移动到别处
			if (pointX > btnX && pointX < btnX + bmpButton.getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton.getHeight()) {
					//还原Button状态为未按下状态
					isPress = false;
					//改变当前游戏状态为开始游戏
					if(MySurfaceView.soundFlag){
						MySurfaceView.mediaPlayer.pause();
						MySurfaceView.mediaPlayer2.pause();
					} else {
						MySurfaceView.mediaPlayer2.pause();
						MySurfaceView.mediaPlayer.start();
					}		
					MySurfaceView.Pause_flag = 1;				
					MySurfaceView.gameState = MySurfaceView.GAME_MENU;
				}
			}
		}
		
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			//判定用户是否点击了按钮
			if (pointX > btn_exit_x && pointX < btn_exit_x + bmpPause_exit.getWidth()) {
				if (pointY > btn_exit_y && pointY < btn_exit_y + bmpPause_exit.getHeight()) {
					isPress = true;
				} else {
					isPress = false;
				}
			} else {
				isPress = false;
			}
			//当用户是抬起动作
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			//抬起判断是否点击按钮，防止用户移动到别处
			if (pointX > btn_exit_x && pointX < btn_exit_x + bmpPause_exit.getWidth()) {
				if (pointY > btn_exit_y && pointY < btn_exit_y + bmpPause_exit.getHeight())  {
					//还原Button状态为未按下状态
					isPress = false;
					//改变当前游戏状态为开始游戏
					MainActivity.instance.finish();
					System.exit(0);                           //退出游戏
					
				}
			}   
		}
		
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			//判定用户是否点击了按钮
			if (pointX > btn_continue_x && pointX < btn_continue_x + bmpPause_continue.getWidth()) {
				if (pointY > btn_continue_y && pointY < btn_continue_y + bmpPause_continue.getHeight()) {
					isPress = true;
				} else {
					isPress = false;
				}
			} else {
				isPress = false;
			}
			//当用户是抬起动作
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			//抬起判断是否点击按钮，防止用户移动到别处
			if (pointX > btn_continue_x && pointX < btn_continue_x + bmpPause_continue.getWidth()) {
				if (pointY > btn_continue_y && pointY < btn_continue_y + bmpPause_continue.getHeight()) {
					//还原Button状态为未按下状态
					isPress = false;
					//改变当前游戏状态为开始游戏
					MySurfaceView.gameState = MySurfaceView.GAMEING;
				}
			}   
		}			
	}	
}
