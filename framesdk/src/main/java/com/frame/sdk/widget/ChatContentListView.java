package com.frame.sdk.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.frame.sdk.R;

/**
 * 聊天界面的聊天记录列表
 */
public class ChatContentListView extends ListView implements OnScrollListener {
	// 头部各个子控件
	private LinearLayout headView;
	private ImageView headImageView;

	// 头部的各种状态(隐藏、下拉刷新、释放立即刷新，刷新中，刷新完成)
	private enum HeaderState {
		HIDE, PULL_TO_REFRESH, RELEASE_TO_REFRESH, REFRESHING, REFRESHED
	}

	private HeaderState headState = HeaderState.HIDE;// 头部状态

	// 下拉刷新相关变量
	private int headHeight;// 头部高度，不包括padding
	private int maxHeadHeight;// 头部（包括padding）的最大高度，即头部最大下拉高度
	private float ratio = 3f;// 下拉(上拉)距离比
	private float step = 20;// 移动的step
	private long minRefreshTime = 1000;// 最小刷新时间ms
	private long refreshedTime = 300;// 刷新完成停顿的时间ms，刷新完成之后会停顿然后隐藏头部
	private long startRefreshTime;// 开始刷新时间
	private boolean isRefreshable = true;// 是否可以下拉刷新
	private boolean refreshResult;// 刷新结果，true表示成功，false表示失败
	private boolean isRefreshed;// 一次刷新中记录是否刷新完毕
	private boolean isRefreshing;// 是否正在刷新，刷新中下拉时，防止重复刷新

	// 尾部各个子控件
	private LinearLayout footView;

	// 加载更多相关变量
	private int footHeight;// 尾部高度，不包括padding
	private int maxFootHeight;// 尾部（包括padding）的最大高度，即尾部最大上拉高度

	private int firstVisibleItem, lastVisibleItem, totalItemCount;
	private float density;

	public ChatContentListView(Context context) {
		super(context);
		init(context);
	}

	public ChatContentListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 设置是否可以刷新
	 * 
	 * @param isRefreshable
	 */
	public void setRefreshable(boolean isRefreshable) {
		if (!isRefreshable)
			headView.setVisibility(View.INVISIBLE);
		this.isRefreshable = isRefreshable;
	}

	/**
	 * 设置刷新结果，刷新完毕后外部调用
	 * 
	 * @param refreshResult
	 *            true刷新成功，false失败
	 */
	public void setRefreshResult(boolean refreshResult) {
		if (headState != HeaderState.REFRESHING)
			return;
		this.isRefreshed = true;
		this.refreshResult = refreshResult;
		changeHeaderState();
		if (!refreshResult) {
			headSmoothToHide(0);
			setRefreshable(false);
		}
	}

	private PullListViewListener pullListViewListener;

	/**
	 * 设置监听器
	 * 
	 * @param pullListViewListener
	 */
	public void setPullListViewListener(PullListViewListener pullListViewListener) {
		this.pullListViewListener = pullListViewListener;
	}

	public interface PullListViewListener {
		void refresh();
	}

	private void init(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		this.density = outMetrics.density;
		step = step * density;
		setOnScrollListener(this);
		initHeaderView(context);
		initFooterView(context);
	}

	private void initHeaderView(Context context) {
		headView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.part_pull_head_chat, null);
		headImageView = (ImageView) headView.findViewById(R.id.head_image_view);
		measureView(headView);
		addHeaderView(headView, null, false);
		headHeight = headView.getMeasuredHeight();
		maxHeadHeight = headHeight * 3;
		headView.setPadding(0, -headHeight, 0, 0);
	}

	private void initFooterView(Context context) {
		footView = new LinearLayout(context);
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1, 1);
		footView.addView(new TextView(context), lp);
		addFooterView(footView, null, false);
		footHeight = footView.getMeasuredHeight();
		maxFootHeight = headHeight * 3;
		footView.setPadding(0, 0, 0, 0);
	}

	// 测量view的高度
	private void measureView(ViewGroup v) {
		ViewGroup.LayoutParams p = v.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int heightMeasureSpec;
		if (p.height > 0)
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(p.height, MeasureSpec.EXACTLY);
		else
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int widthMeasureSpec;
		if (p.width > 0)
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(p.width, MeasureSpec.EXACTLY);
		else
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		v.measure(widthMeasureSpec, heightMeasureSpec);
	}

	private float downY, moveY, lastY;
	private boolean isUp;

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (touchListener != null)
				touchListener.onDown();
			isUp = false;
			lastY = downY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (touchListener != null)
				touchListener.onMove();
			moveY = event.getRawY();
			if (firstVisibleItem == 0) {
				if (moveY - downY > 0 || headView.getPaddingTop() > -headHeight) {
					moveHeader((moveY - lastY));
					changeHeaderState();
				}
			} else if (lastVisibleItem == totalItemCount && footView.getBottom() == getHeight()) {
				if (moveY - downY < 0) {
					moveFooter(lastY - moveY);
				} else if (footView.getPaddingBottom() > 0)
					moveFooter(lastY - moveY);
			}
			lastY = moveY;
			break;
		case MotionEvent.ACTION_UP:
			if (touchListener != null)
				touchListener.onUp();
			isUp = true;
			if (firstVisibleItem == 0) {
				if (isRefreshable)
					changeHeaderState();
				else
					headSmoothToHide(0);
			}
			footSmoothToNormal();
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	private void moveHeader(float moveLen) {
		setSelection(0);
		int paddingTop = headView.getPaddingTop() + (int) (moveLen / ratio);
		if (paddingTop <= -headHeight) {
			paddingTop = -headHeight;
		} else if (paddingTop > maxHeadHeight - headHeight)
			paddingTop = maxHeadHeight - headHeight;
		headView.setPadding(0, paddingTop, 0, 0);
	}

	private void moveFooter(float moveLen) {
		if (moveLen < 0)
			setSelection(lastVisibleItem);
		int paddingBottom = footView.getPaddingBottom() + (int) (moveLen / ratio);
		if (paddingBottom < 0) {
			paddingBottom = 0;
		} else if (paddingBottom > maxFootHeight - footHeight)
			paddingBottom = maxFootHeight - footHeight;
		footView.setPadding(0, 0, 0, paddingBottom);
	}

	// 改变头部状态，根据当前状态以及是否满足一些条件来改变头部状态
	private void changeHeaderState() {
		if (!isRefreshable)
			return;
		int paddingTop = headView.getPaddingTop();
		switch (headState) {
		case HIDE:
			if (paddingTop > -headHeight)
				setHeadState(HeaderState.PULL_TO_REFRESH);
			break;
		case PULL_TO_REFRESH:
			if (paddingTop > 0)
				setHeadState(HeaderState.RELEASE_TO_REFRESH);
			else if (paddingTop <= -headHeight || isUp) {
				headSmoothToHide(0);
				setHeadState(HeaderState.HIDE);
			}
			break;
		case RELEASE_TO_REFRESH:
			if (paddingTop <= 0)
				setHeadState(HeaderState.PULL_TO_REFRESH);
			else if (isUp) {
				setHeadState(HeaderState.REFRESHING);
				headSmoothToHead(0);
			}
			break;
		case REFRESHING:
			if (isRefreshed && !isRefreshing) {
				isRefreshing = true;
				long passedTime = System.currentTimeMillis() - startRefreshTime;
				if (passedTime < minRefreshTime)// 保证至少刷新时间minRefreshTime
					handler.sendEmptyMessageDelayed(REFRESH_DOWN, minRefreshTime - passedTime);
				else
					handler.sendEmptyMessage(REFRESH_DOWN);
			}
			if (isUp && paddingTop > 0) {
				headSmoothToHead(0);
			}
			break;
		case REFRESHED:
			if (isUp) {
				if (paddingTop > 0)
					headSmoothToHide(0);
				else
					headSmoothToHide(refreshedTime);
			}
			break;
		}
	}

	private void setHeadState(HeaderState toState) {
		switch (toState) {
		case HIDE:
			((AnimationDrawable) headImageView.getBackground()).stop();
			break;
		case PULL_TO_REFRESH:
			((AnimationDrawable) headImageView.getBackground()).stop();
			break;
		case RELEASE_TO_REFRESH:
			((AnimationDrawable) headImageView.getBackground()).stop();
			break;
		case REFRESHING:
			((AnimationDrawable) headImageView.getBackground()).start();
			break;
		case REFRESHED:
			((AnimationDrawable) headImageView.getBackground()).stop();
			break;
		}
		headState = toState;
	}

	// 头部从当前位置逐步到隐藏
	private void headSmoothToHide(long delayMillis) {
		if (null != headSmoothRunnable) {
			headSmoothRunnable.stop();
		}
		headSmoothRunnable = new HeadSmooth(headView.getPaddingTop(), -headHeight);
		postDelayed(headSmoothRunnable, delayMillis);
	}

	// 头部padding从当前padding逐步到0
	private void headSmoothToHead(long delayMillis) {
		if (null != headSmoothRunnable) {
			headSmoothRunnable.stop();
		}
		headSmoothRunnable = new HeadSmooth(headView.getPaddingTop(), 0);
		postDelayed(headSmoothRunnable, delayMillis);
	}

	// 尾部从当前位置逐步到正常位置
	private void footSmoothToNormal() {
		if (null != footSmoothRunnable) {
			footSmoothRunnable.stop();
		}
		footSmoothRunnable = new FootSmooth();
		post(footSmoothRunnable);
	}

	private final int HEAD_SMOOTH_DONE = 0x1;
	private final int REFRESH_DOWN = 0x1 + 1;
	private final int FOOT_SMOOTH_DONE = 0x1 + 2;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case HEAD_SMOOTH_DONE:
				// 刷新状态回到头部再刷新
				if (headState == HeaderState.REFRESHING && pullListViewListener != null && !isRefreshed && isRefreshable) {
					startRefreshTime = System.currentTimeMillis();
					pullListViewListener.refresh();
				}
				if (headState == HeaderState.REFRESHED) {
					setHeadState(HeaderState.HIDE);
					isRefreshed = false;
					isRefreshing = false;
				}
				break;
			case REFRESH_DOWN:
				setHeadState(HeaderState.REFRESHED);
				changeHeaderState();
				break;
			case FOOT_SMOOTH_DONE:
				break;
			}
		}
	};

	// 以下头部、尾部逐步反弹的runnable
	private HeadSmooth headSmoothRunnable;

	private class HeadSmooth implements Runnable {
		private int toPaddingTop;
		private int curPaddingTop;
		private boolean isRunning;

		public HeadSmooth(int fromPaddingTop, int toPaddingTop) {
			this.curPaddingTop = fromPaddingTop;
			this.toPaddingTop = toPaddingTop;
			isRunning = true;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			curPaddingTop -= step;
			if (curPaddingTop < toPaddingTop) {
				curPaddingTop = toPaddingTop;
			}
			headView.setPadding(0, curPaddingTop, 0, 0);
			if (curPaddingTop > toPaddingTop && isRunning)
				ChatContentListView.this.postDelayed(this, 0);
			else
				handler.sendEmptyMessage(HEAD_SMOOTH_DONE);
		}

		public void stop() {
			isRunning = false;
			removeCallbacks(this);
		}
	}

	private FootSmooth footSmoothRunnable;

	private class FootSmooth implements Runnable {
		private int curPaddingBottom;
		private boolean isRunning;

		public FootSmooth() {
			curPaddingBottom = footView.getPaddingBottom();
			isRunning = true;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			curPaddingBottom -= step;
			if (curPaddingBottom < 0) {
				curPaddingBottom = 0;
			}
			footView.setPadding(0, 0, 0, curPaddingBottom);
			if (curPaddingBottom > 0 && isRunning)
				ChatContentListView.this.postDelayed(this, 0);
			else
				handler.sendEmptyMessage(FOOT_SMOOTH_DONE);
		}

		public void stop() {
			isRunning = false;
			removeCallbacks(this);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		this.lastVisibleItem = firstVisibleItem + visibleItemCount;
		this.totalItemCount = totalItemCount;

		if (firstVisibleItem == 0 && visibleItemCount == 0 && totalItemCount == 0) // 首次显示控件时第一次调用onScroll，所有值为0
			return;

		if (totalItemCount <= 2) {// listview没有任何子项
			setRefreshable(false);
			return;
		}
	}

	private TouchListener touchListener;

	// 增加一个touch的监听器，用于touch该listview,隐藏软键盘
	public void setTouchListener(TouchListener touchListener) {
		this.touchListener = touchListener;
	}

	public interface TouchListener {
		void onDown();

		void onMove();

		void onUp();
	}
}
