package com.starfish.android;

import com.starfish.game.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class Welcome extends Activity {

	private ViewFlipper viewFlipper;
	private ViewGroup group;
	private Button btnStart;
	private ImageView[] imageViews;
	private GestureDetector detector;
	private Context ctx;
	private int curPos;
	private ImageView img1, img2, img3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);

		ctx = this;
		curPos = 0;

		SharedPreferences preferences = getSharedPreferences("config.xml",
				MODE_PRIVATE);
		preferences.edit().putInt("count", 1).commit();

		viewFlipper = (ViewFlipper) findViewById(R.id.guidePages);
		group = (ViewGroup) findViewById(R.id.viewGroup);
		btnStart = (Button) findViewById(R.id.btnStart);

		btnStart.setVisibility(View.INVISIBLE);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(ctx, MainActivity.class);
//				startActivity(intent);
				finish();
			}

		});

		img1 = new ImageView(this);
		img1.setBackgroundResource(R.drawable.introduction1);
		viewFlipper.addView(img1);

		img2 = new ImageView(this);
		img2.setBackgroundResource(R.drawable.introduction2);
		viewFlipper.addView(img2);

		img3 = new ImageView(this);
		img3.setBackgroundResource(R.drawable.introduction3);
		viewFlipper.addView(img3);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20, 20);
		// 将小圆点放到imageView数组当中
		imageViews = new ImageView[viewFlipper.getChildCount()];
		for (int i = 0; i < viewFlipper.getChildCount(); i++) {
			imageViews[i] = new ImageView(Welcome.this);
			imageViews[i].setLayoutParams(lp);
			imageViews[i].setPadding(20, 0, 20, 0);

			if (i == 0) {
				// 默认选中第一张图片
				imageViews[i]
						.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.page_indicator);
			}

			group.addView(imageViews[i], lp);
		}

		detector = new GestureDetector(this, listener);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);
	}

	private OnGestureListener listener = new OnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > 120) {
				showNext();
			} else if (e1.getX() - e2.getX() < -120) {
				showPrev();
			}

			group.getChildAt(curPos).setBackgroundResource(
					R.drawable.page_indicator_focused);

			checkEnd();
			return true;
		}

		private void showNext() {
			if (viewFlipper.getCurrentView() != img3) {
				// if (curPos < viewFlipper.getChildCount()) {
				viewFlipper.setInAnimation(AnimationUtils.loadAnimation(ctx,
						R.anim.push_left_in));
				viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(ctx,
						R.anim.push_left_out));
				viewFlipper.showNext();

				group.getChildAt(curPos).setBackgroundResource(
						R.drawable.page_indicator);
				curPos++;
			}
		}

		private void showPrev() {
			if (viewFlipper.getCurrentView() != img1) {
				viewFlipper.setInAnimation(AnimationUtils.loadAnimation(ctx,
						R.anim.push_right_in));
				viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(ctx,
						R.anim.push_right_out));
				viewFlipper.showPrevious();

				group.getChildAt(curPos).setBackgroundResource(
						R.drawable.page_indicator);
				curPos--;
			}
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		private void checkEnd() {
			if (curPos == viewFlipper.getChildCount() - 1) {
				btnStart.setVisibility(View.VISIBLE);
			} else {
				btnStart.setVisibility(View.INVISIBLE);
			}
		}

	};
}
