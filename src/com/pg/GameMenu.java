package com.pg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;


/**
 * @author Himi
 *
 */
public class GameMenu {
	//菜单背景图
	private Bitmap bmpMenu;
	//按钮图片资源(按下和未按下图)
	private Bitmap bmpButton, bmpButtonPress,bmpstart1,bmpstart2;
	//按钮的坐标
	private int btnX, btnY;
	//按钮是否按下标识位
	private Boolean isPress;
	
	//菜单初始化
	public GameMenu(Bitmap bmpMenu, Bitmap bmpButton, Bitmap bmpButtonPress,Bitmap bmpStart1,Bitmap bmpStart2) {
		this.bmpMenu = bmpMenu;
		this.bmpButton = bmpButton;
		this.bmpButtonPress = bmpButtonPress;
		this.bmpstart1 = bmpStart1;
		this.bmpstart2 = bmpStart2;
		//X居中，Y紧接屏幕底部
		btnX = MySurfaceView.screenW / 2 - bmpButton.getWidth() / 2;
		btnY = MySurfaceView.screenH - bmpButton.getHeight();
		
		isPress = false;
	}
	//菜单绘图函数
	public void draw(Canvas canvas, Paint paint) {
		//绘制菜单背景图
		canvas.drawBitmap(bmpMenu, 0, 0, paint);
		//绘制未按下按钮图
			canvas.drawBitmap(bmpstart1, -35, MySurfaceView.screenH/9,paint);
		if (isPress) {//根据是否按下绘制不同状态的按钮图
			canvas.drawBitmap(bmpButtonPress, btnX, btnY, paint);
		} else {
			canvas.drawBitmap(bmpButton, btnX, btnY, paint);
		}
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
						//MySurfaceView.mediaPlayer2.start();
					} else {
						MySurfaceView.mediaPlayer.pause();
						MySurfaceView.mediaPlayer2.start();
					}
					MySurfaceView.gameState = MySurfaceView.GAMEING;
				}
			}
		}
	}
	
}
