package com.frame.sdk.widget;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.frame.sdk.R;

/* 
 使用说明：
 1、设置刷新加载的监听器（setPullListViewListener）
 2、设置刷新结果（setRefreshResult）
 3、设置加载结果（setLoadResult）
 4、设置是否可以下拉刷新（setRefreshable）
 5、自动刷新（autoRefresh）
 6、设置滚动监听器（setScrollListener）
 7、滑动动画删除某一项（slideRemoveItem）
 8、设置滑动动画删除某一项的监听器（setSlideRemoveListener） 
 9、设置右边滚动条颜色（setScrollColor）
 10、设置没有头部或尾部（请在xml中设置headvisiable或footvisiable为false）
 11、listview中item能在一屏显示时，底部上拉加载更多会自动隐藏。  
 12、此控件在没网时自动变为不能下拉刷新和上拉加载。
 13、网络断开时自动变为不可刷新和加载
 */
public class PullListView extends ListView implements OnScrollListener {
	private long defaultDuration = 500; // 动画删除某个子项的动画持续时间(ms)
	private float upStep = 70;// 动画删除某个子项之后该子项高度减小的速度

	private Context context;
	// 头部各个子控件
	private LinearLayout headView;
	private ImageView headImageView;
	// private ProgressBar headProgressBar;
	private TextView headTextView;

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
	private final long ANIMATION_DURATION = 100;// 箭头动画转动持续时间
	private boolean isRefreshable = true;// 是否可以下拉刷新
	private boolean refreshResult;// 刷新结果，true表示成功，false表示失败
	private boolean isRefreshed;// 一次刷新中记录是否刷新完毕
	private boolean isRefreshing;// 是否正在刷新，刷新中下拉时，防止重复刷新

	// 尾部各个子控件
	private LinearLayout footView;
	// private ProgressBar footProgressBar;
	private ImageView footImageView;
	private TextView footTextView;

	// 上拉加载更多相关变量
	private int footHeight;// 尾部高度，不包括padding
	private int maxFootHeight;// 尾部（包括padding）的最大高度，即尾部最大上拉高度
	private boolean isLoadMore = true;// 是否可以上拉加载更多
	private boolean isLoadComplete;// 一次上拉加载更多中是否加载完成
	private boolean isLoading;// 是否正在加载，加载中上拉，防止重复加载
	private boolean havehead = true, havefooter = true;
	private int loadResult = 1;
	public static final int LOAD_SUCCESS = 1;
	public static final int LOAD_FAIL = 2;
	public static final int LOAD_NO_MORE = 3;

	// 尾部的各种状态(一般状态，加载中,加载失败，没有更多)
	private enum FooterState {
		NORMAL, LOADING, LOAD_FAIL, LOAD_NO_MORE
	}

	private FooterState footState = FooterState.NORMAL;// 尾部状态

	private int firstVisibleItem, lastVisibleItem, totalItemCount;
	private float density;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	public PullListView(Context context) {
		super(context);
		init(context);
	}

	public PullListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PullListView);
		havehead = ta.getBoolean(R.styleable.PullListView_headvisiable, true);
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
		// Log.i(LOG_TAG, "setRefreshResult isRefreshed==" + isRefreshed);
		// Log.i(LOG_TAG, "setRefreshResult isRefreshable==" + isRefreshable);
		// Log.i(LOG_TAG, "setRefreshResult headState==" + headState);
		if (headState != HeaderState.REFRESHING)
			return;
		this.isRefreshed = true;
		this.refreshResult = refreshResult;
		changeHeaderState();
		// 刷新完重设底部状态
		if (refreshResult) {
			loadResult = LOAD_SUCCESS;
			isLoadMore = true;
			setFootState(FooterState.NORMAL);
		}
	}

	/**
	 * 设置加载结果
	 * 
	 * @param loadResult
	 *            PullListView.LOAD_SUCCESS、PullListView.LOAD_FAIL、PullListView.
	 *            LOAD_NO_MORE
	 */
	public void setLoadResult(int loadResult) {
		this.isLoadComplete = true;
		this.loadResult = loadResult;
		this.footState=FooterState.LOADING;
		changeFooterState();
	}

	/**
	 * 自动刷新，调用此函数头部直接显示正在刷新，进入刷新状态
	 */
	public void autoRefresh() {
		if (!isRefreshable || headState != HeaderState.HIDE || !isNetworkConnected())
			return;
		setSelection(0);
		headView.setPadding(0, 0, 0, 0);
		isUp = true;
		setHeadState(HeaderState.REFRESHING);
		handler.sendEmptyMessage(HEAD_SMOOTH_DONE);
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

		void loadMore();
	}

	private void init(Context context) {
		this.context = context;
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		this.density = outMetrics.density;
		step = step * density;
		upStep = upStep * density;
		setOnScrollListener(this);
		initHeaderView();
		initFooterView();
		initAnimation();
		// ColorDrawable drawable = new
		// ColorDrawable(getResources().getColor(R.color.scroll_bar_color));
		// setScrollColor(drawable);
	}

	public void setScrollColor(Drawable draw) {
		this.setFastScrollEnabled(true);
		try {
			Field f = AbsListView.class.getDeclaredField("mFastScroller");
			f.setAccessible(true);
			Object o = f.get(this);
			f = f.getType().getDeclaredField("mThumbDrawable");
			f.setAccessible(true);
			f.set(o, draw);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initHeaderView() {
		headView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.part_pull_head, null);
		headImageView = (ImageView) headView.findViewById(R.id.head_image_view);
		// headProgressBar = (ProgressBar)
		// headView.findViewById(R.id.head_prog);
		headTextView = (TextView) headView.findViewById(R.id.head_text_view);
		measureView(headView);
		if (havehead)
			addHeaderView(headView, null, false);
		headHeight = headView.getMeasuredHeight();
		maxHeadHeight = headHeight * 3;
		headView.setPadding(0, -headHeight, 0, 0);
	}

	private void initFooterView() {
		footView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.part_pull_foot, null);
		// footProgressBar = (ProgressBar)
		// footView.findViewById(R.id.foot_prog);
		footImageView = (ImageView) footView.findViewById(R.id.foot_image_view);
		footTextView = (TextView) footView.findViewById(R.id.foot_text_view);
		measureView(footView);
		if (havefooter)
			addFooterView(footView, null, false);
		footHeight = footView.getMeasuredHeight();
		maxFootHeight = footHeight * 3 * 2;
		footView.setPadding(0, 0, 0, 0);
	}

	private void initAnimation() {
		animation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(ANIMATION_DURATION);
		animation.setFillAfter(false);

		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(ANIMATION_DURATION);
		reverseAnimation.setFillAfter(false);
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

	// public void setLoadMore(boolean isLoadMore) {
	// if (!isLoadMore) {
	// // footView.setLayoutParams(new AbsListView.LayoutParams(1, 1));
	// footView.setVisibility(View.INVISIBLE);
	// }
	// this.isLoadMore = isLoadMore;
	// }

	private float downY, moveY, lastY;
	private boolean isUp;

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isUp = false;
			lastY = downY = event.getRawY();
			if (!isNetworkConnected()) {
				isLoadMore = false;
				setHeadState(HeaderState.HIDE);
				footTextView.setText("");
				footTextView.setVisibility(View.GONE);
				footImageView.setVisibility(View.GONE);
				// footProgressBar.setVisibility(View.GONE);
			} else {
				isLoadMore = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			moveY = event.getRawY();
			if (havehead && firstVisibleItem == 0) {
				if (moveY - downY > 0 || headView.getPaddingTop() > -headHeight) {
					moveHeader((moveY - lastY));
					changeHeaderState();
				}
			} else if (lastVisibleItem == totalItemCount && footView.getBottom() == getHeight()) {
				if (moveY - downY < 0) {
					moveFooter(lastY - moveY);
					changeFooterState();
				} else if (footView.getPaddingBottom() > 0)
					moveFooter(lastY - moveY);
			}
			lastY = moveY;
			break;
		case MotionEvent.ACTION_UP:
			isUp = true;
			if (firstVisibleItem == 0) {
				if (!isNetworkConnected())
					headSmoothToHide(0);
				else if (isRefreshable)
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
		if (moveLen < 0 && footState == FooterState.LOADING)
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
		if (!isRefreshable || !isNetworkConnected())
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

	private void changeFooterState() {
		if (!isLoadMore)
			return;
		switch (footState) {
		case NORMAL:
			if (lastVisibleItem == totalItemCount && footView.getBottom() == getHeight() && !isLoading) {
				isLoading = true;
				setFootState(FooterState.LOADING);
				if (pullListViewListener != null && isLoadMore)
					pullListViewListener.loadMore();
			}
			break;
		case LOADING:
			if (isLoadComplete) {
				if (loadResult == LOAD_FAIL) {
					setFootState(FooterState.LOAD_FAIL);
				} else if (loadResult == LOAD_SUCCESS) {
					setFootState(FooterState.NORMAL);
				} else if (loadResult == LOAD_NO_MORE) {
					setFootState(FooterState.LOAD_NO_MORE);
				} else
					;
				if (isUp) {
					footSmoothToNormal();
				}
			}
			break;
		case LOAD_FAIL:
			if (footView.getPaddingBottom() > 0 && !isLoading) {
				isLoading = true;
				setFootState(FooterState.LOADING);
				if (pullListViewListener != null && isLoadMore)
					pullListViewListener.loadMore();
			}
			break;
		case LOAD_NO_MORE:
			break;
		}
	}

	private void setFootState(FooterState toState) {
		switch (toState) {
		case NORMAL:
			footTextView.setText("上拉加载更多");
			// footProgressBar.setVisibility(View.VISIBLE);
			footImageView.clearAnimation();
			footImageView.setVisibility(View.GONE);
			footTextView.setVisibility(View.VISIBLE);
			break;
		case LOADING:
			footView.setVisibility(View.VISIBLE);
			footTextView.setText("正在加载...");
			footTextView.setVisibility(View.VISIBLE);
			footImageView.setVisibility(View.VISIBLE);
			startLoading(footImageView);
			// footProgressBar.setVisibility(View.VISIBLE);
			break;
		case LOAD_NO_MORE:
			// footTextView.setText("下面没有啦~");
			footTextView.setText("");
//			Toast.makeText(context, "下面没有啦~", Toast.LENGTH_SHORT).show();
			// DialogUtil.showT("下面没有啦~");
			// footProgressBar.setVisibility(View.GONE);
			footImageView.clearAnimation();
			footImageView.setVisibility(View.GONE);
			footTextView.setVisibility(View.GONE);
			isLoadMore = false;
			break;
		case LOAD_FAIL:
			// footTextView.setText("加载失败,上拉重新加载");
			footTextView.setText("");
//			Toast.makeText(context, "加载失败,上拉重新加载", Toast.LENGTH_SHORT).show();
			// DialogUtil.showT("加载失败,上拉重新加载");
			// footProgressBar.setVisibility(View.GONE);
			footImageView.clearAnimation();
			footImageView.setVisibility(View.GONE);
			footTextView.setVisibility(View.GONE);
			isLoadMore = true;
			break;
		}
		footState = toState;
	}

	private void setHeadState(HeaderState toState) {
		switch (toState) {
		case HIDE:
			headImageView.setBackgroundResource(0);
			headImageView.setImageResource(R.drawable.refresh_arrow_down);
			headTextView.setText("");
			headImageView.setVisibility(View.GONE);
			// headProgressBar.setVisibility(View.GONE);
			break;
		case PULL_TO_REFRESH:
			headTextView.setText("下拉刷新");
			headImageView.setVisibility(View.VISIBLE);
			if (headState == HeaderState.RELEASE_TO_REFRESH) {
				headImageView.clearAnimation();
				headImageView.setBackgroundResource(0);
				headImageView.setImageResource(R.drawable.refresh_arrow_down);
				headImageView.startAnimation(reverseAnimation);
			}
			// headProgressBar.setVisibility(View.GONE);
			break;
		case RELEASE_TO_REFRESH:
			headTextView.setText("释放立即刷新");
			headImageView.setVisibility(View.VISIBLE);
			headImageView.clearAnimation();
			headImageView.setBackgroundResource(0);
			headImageView.setImageResource(R.drawable.refresh_arrow_up);
			headImageView.startAnimation(animation);
			// headProgressBar.setVisibility(View.GONE);
			break;
		case REFRESHING:
			headTextView.setText("正在刷新...");
			headImageView.clearAnimation();
			headImageView.setVisibility(View.VISIBLE);
			startLoading(headImageView);
			// headProgressBar.setVisibility(View.VISIBLE);
			break;
		case REFRESHED:
			if (refreshResult)
				headTextView.setText("刷新成功");
			else
				headTextView.setText("刷新失败");
			headImageView.clearAnimation();
			headImageView.setVisibility(View.GONE);
			// headProgressBar.setVisibility(View.GONE);
			break;
		}
		headState = toState;
	}

	private void startLoading(ImageView view) {
		view.clearAnimation();
		view.setImageResource(0);
		view.setBackgroundResource(R.anim.loading);
		AnimationDrawable anim = (AnimationDrawable) view.getBackground();
		anim.start();
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
				if (footState != FooterState.LOADING) {
					isLoadComplete = false;
					isLoading = false;
				}
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
				PullListView.this.postDelayed(this, 0);
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
				PullListView.this.postDelayed(this, 0);
			else
				handler.sendEmptyMessage(FOOT_SMOOTH_DONE);
		}

		public void stop() {
			isRunning = false;
			removeCallbacks(this);
		}
	}

	private ScrollListener scrollListener;// listview滚动监听

	public interface ScrollListener {
		void idle();

		void fling();

		void touchScroll();
	}

	public void setScrollListener(ScrollListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollListener == null)
			return;
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
			scrollListener.idle();
		else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING)
			scrollListener.fling();
		else
			scrollListener.touchScroll();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		this.firstVisibleItem = firstVisibleItem;
		this.lastVisibleItem = firstVisibleItem + visibleItemCount;
		this.totalItemCount = totalItemCount;

		if (getAdapter() == null) // 还没设置adapter
			return;

		// Log.i(LOG_TAG, "onScroll");
		// Log.i(LOG_TAG,
		// "onScroll onScroll onScroll onScroll firstVisibleItem==" +
		// firstVisibleItem);
		// Log.i(LOG_TAG,
		// "onScroll onScroll onScroll onScroll lastVisibleItem==" +
		// (firstVisibleItem + visibleItemCount));
		// Log.i(LOG_TAG,
		// "onScroll onScroll onScroll onScroll visibleItemCount==" +
		// visibleItemCount);
		// Log.i(LOG_TAG, "onScroll onScroll onScroll onScroll totalItemCount=="
		// + totalItemCount);
		// Log.i(LOG_TAG, "onScroll footView.getBottom()==" +
		// footView.getBottom());
		// Log.i(LOG_TAG, "onScroll getHeight()==" + getHeight());
		// add by lwy,当没有头，尾时，调整判断空数目
		int empty = 0;
		if (havehead)
			empty++;
		if (havefooter)
			empty++;
		if (totalItemCount <= empty) {// listview没有任何子项
			setRefreshable(false);
			this.isLoadMore = false;
			footTextView.setText("");
			// footProgressBar.setVisibility(View.GONE);
			footImageView.clearAnimation();
			footImageView.setVisibility(View.GONE);
			footTextView.setVisibility(View.GONE);
//			Toast.makeText(context, "没有获得数据", Toast.LENGTH_SHORT).show();
			// DialogUtil.showT("没有获得数据");
			return;
		}
		if (totalItemCount <= visibleItemCount) {// 一个屏幕显示所有子项，设为不能加载更多
			this.isLoadMore = false;
			// footTextView.setText("onScroll没有更多数据");
			footTextView.setText("");
			// footProgressBar.setVisibility(View.GONE);
			footImageView.clearAnimation();
			footImageView.setVisibility(View.GONE);
			footTextView.setVisibility(View.GONE);
			// Log.i(LOG_TAG, "onScroll没有更多数据");
			return;
		} else {
			// 刷新完之后如果有更多的数据、就可以上拉加载了
			if (headState != HeaderState.HIDE || !isNetworkConnected())
				return;
			this.isLoadMore = true;
			if (footState == FooterState.NORMAL) {
				footTextView.setText("上拉加载更多");
				// footProgressBar.setVisibility(View.GONE);
				footImageView.clearAnimation();
				footImageView.setVisibility(View.GONE);
				footTextView.setVisibility(View.VISIBLE);
				// Log.i(LOG_TAG, "onScroll上拉加载更多");
			}
		}
		changeFooterState();
	}

	private boolean isNetworkConnected() {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// /以下增加滑动删除某一项，下面一项逐步向上靠拢的动画
	public enum Direction {
		DIRECTION_LEFT, DIRECTION_RIGHT
	}

	View itemView;

	/**
	 * 滑动删除item，如果有头部，pos=0是头部
	 */
	public boolean slideRemoveItem(int pos, Direction direction) {
		itemView = getItemView(pos);
		if (itemView == null) {
			return false;
		}
		slideItem(direction);
		upItem();
		return true;
	}

	private View getItemView(int pos) {
		Adapter adapter = getAdapter();
		if (adapter == null || adapter.getCount() <= 0) {
			return null;
		}
		if (pos < 0 || pos >= adapter.getCount())
			return null;
		return getChildAt(pos - firstVisibleItem);
	}

	private void slideItem(Direction direction) {
		AnimationSet animSet = new AnimationSet(true);
		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0);
		animSet.addAnimation(alphaAnim);
		TranslateAnimation transAnim = null;
		switch (direction) {
		case DIRECTION_LEFT:
			transAnim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
			break;
		case DIRECTION_RIGHT:
			transAnim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
			break;
		}
		if (transAnim != null)
			animSet.addAnimation(transAnim);
		animSet.setDuration(defaultDuration);
		animSet.setFillAfter(true);
		animSet.setFillEnabled(false);
		itemView.startAnimation(animSet);
		if (slideRemoveListener != null)
			slideRemoveListener.start();
	}

	private int itemViewHeight;

	private void upItem() {
		itemViewHeight = itemView.getHeight();
		postDelayed(upRunable, defaultDuration);
	}

	private Runnable upRunable = new Runnable() {
		@Override
		public void run() {
			ViewGroup.LayoutParams lp = itemView.getLayoutParams();
			lp.height -= upStep;
			if (lp.height < 0)
				lp.height = 0;
			itemView.setLayoutParams(lp);
			if (lp.height > 0) {
				post(upRunable);
			} else {
				lp.height = itemViewHeight;
				itemView.setLayoutParams(lp);
				itemView.clearAnimation();
				if (slideRemoveListener != null)
					slideRemoveListener.end();
			}
		}
	};

	private SlideRemoveListener slideRemoveListener;

	public void setSlideRemoveListener(SlideRemoveListener slideRemoveListener) {
		this.slideRemoveListener = slideRemoveListener;
	}

	public interface SlideRemoveListener {
		void start();

		void end();
	}
}
