package com.frame.sdk.widget;


 

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

// TODO: Auto-generated Javadoc
/**
 * The Class HorizontalListView.ˮƽ��listview
 */
public class HorizontalListView extends AdapterView<ListAdapter> {

	/** Regular layout - usually an unsolicited layout from the view system */
	static final int LAYOUT_NORMAL = 0;

	/**
	 * Make a mSelectedItem appear in a specific location and build the rest of
	 * the views from there. The top is specified by mSpecificTop.
	 */
	static final int LAYOUT_SPECIFIC = 4;

	/**
	 * Layout to sync as a result of a data change. Restore mSyncPosition to
	 * have its top at mSpecificTop
	 */
	static final int LAYOUT_SYNC = 5;

	/** Controls how the next layout will happen */
	int mLayoutMode = LAYOUT_NORMAL;

	/** The m always override touch. */
	public boolean mAlwaysOverrideTouch = true;

	/** The m adapter. */
	protected ListAdapter mAdapter;

	/** The m left view index. */
	private int mLeftViewIndex = -1;

	/** The m right view index. */
	private int mRightViewIndex = 0;

	/** The m current x. */
	protected int mCurrentX;

	/** The m next x. */
	protected int mNextX;

	/** The m max x. */
	private int mMaxX = Integer.MAX_VALUE;
	private int mMinX = Integer.MIN_VALUE;

	/** The m display offset. */
	private int mDisplayOffset = 0;

	/** The m scroller. */
	protected Scroller mScroller;

	/** The m gesture. */
	private GestureDetector mGesture;

	/** The m removed view queue. */
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();

	/** The m on item selected. */
	private OnItemSelectedListener mOnItemSelected;

	/** The m on item clicked. */
	private OnItemClickListener mOnItemClicked;

	private OnItemLongClickListener mOnItemLongClicked;

	/** The m data changed. */
	private boolean mDataChanged = false;

	private int mFirstPosition = 0;

	private int mSpecificLeft; // ����setSelection
	private int mSpecificPosition; // ����setSelection

	/**
	 * Instantiates a new horizontial list view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	/**
	 * Inits the view.
	 */
	private synchronized void initView() {
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mNextX = 0;
		mFirstPosition = 0;
		mSpecificPosition = 0;
		mSpecificLeft = 0;
		mMaxX = Integer.MAX_VALUE;
		mMinX = Integer.MIN_VALUE;
		mScroller = new Scroller(getContext());
		mGesture = new GestureDetector(getContext(), mOnGesture);
	}

	private synchronized void initViewForSpecific() {
		mLeftViewIndex = mSpecificPosition - 1;
		mRightViewIndex = mSpecificPosition + 1;
		mFirstPosition = mSpecificPosition;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		mScroller = new Scroller(getContext());
		mGesture = new GestureDetector(getContext(), mOnGesture);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#setOnItemSelectedListener(android.widget.
	 * AdapterView.OnItemSelectedListener)
	 */
	@Override
	public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView#setOnItemClickListener(android.widget.AdapterView
	 * .OnItemClickListener)
	 */
	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		mOnItemClicked = listener;
	}

	@Override
	public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
		mOnItemLongClicked = listener;
	}

	/** The m data observer. */
	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized (HorizontalListView.this) {
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();
			invalidate();
			requestLayout();
		}

	};

	/** The m height measure spec. */
	private int mHeightMeasureSpec;

	/** The m width measure spec. */
	private int mWidthMeasureSpec;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#getAdapter()
	 */
	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#getSelectedView()
	 */
	@Override
	public View getSelectedView() {
		// TODO: implement
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#setAdapter(android.widget.Adapter)
	 */
	@Override
	public void setAdapter(ListAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		reset();
	}

	/**
	 * Reset.
	 */
	private synchronized void reset() {
		initView();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public int getFirstVisiblePosition() {
		return mFirstPosition;
	}

	@Override
	public int getLastVisiblePosition() {
		return mFirstPosition + getChildCount() - 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#setSelection(int)
	 */
	@Override
	public void setSelection(int position) {
		// TODO: implement
		setSelectionFromLeft(position, 0);

	}

	/**
	 * Sets the selected item and positions the selection y pixels from the left
	 * edge * of the ListView. (If in touch mode, the item will not be selected
	 * but it will 208 * still be positioned appropriately.)
	 * 
	 * @param position
	 *            Index (starting at 0) of the data item to be selected.
	 * @param x
	 *            The distance from the left edge of the ListView (plus padding)
	 *            that the item will be positioned.
	 */
	public void setSelectionFromLeft(int position, int x) {
		if (mAdapter == null) {
			return;
		}
		if (!isInTouchMode()) {
			position = lookForSelectablePosition(position, true);
		}
		if (position >= 0) {
			mLayoutMode = LAYOUT_SPECIFIC;
			mSpecificPosition = position;
			mSpecificLeft = getPaddingLeft() + x;
			requestLayout();
		}
	}

	/**
	 * Find a position that can be selected (i.e., is not a separator).
	 * 
	 * @param position
	 *            The starting position to look at.
	 * @param lookDown
	 *            Whether to look down for other positions.
	 * @return The next selectable position starting at position and then
	 *         searching either up or * down. Returns{@link #INVALID_POSITION}
	 *         if nothing can be found.
	 */
	int lookForSelectablePosition(int position, boolean lookDown) {
		final ListAdapter adapter = mAdapter;
		if (adapter == null || isInTouchMode()) {
			return INVALID_POSITION;
		}
		final int count = adapter.getCount();
		if (!adapter.areAllItemsEnabled()) {
			if (lookDown) {
				position = Math.max(0, position);
				while (position < count && !adapter.isEnabled(position)) {
					position++;
				}
			} else {
				position = Math.min(position, count - 1);
				while (position >= 0 && !adapter.isEnabled(position)) {
					position--;
				}
			}
			if (position < 0 || position >= count) {
				return INVALID_POSITION;
			}
			return position;
		} else {
			if (position < 0 || position >= count) {
				return INVALID_POSITION;
			}
			return position;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mHeightMeasureSpec = heightMeasureSpec;
		mWidthMeasureSpec = widthMeasureSpec;
	}

	/**
	 * Adds the and measure child.
	 * 
	 * @param child
	 *            the child
	 * @param viewPos
	 *            the view pos
	 */
	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();

		// �����޸�
		// if (params == null) {
		// params = new LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT);
		// }

		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

		addViewInLayout(child, viewPos, params, true);

		// �����޸�
		child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
		// int heightMeasureSpec = MeasureSpec.makeMeasureSpec(
		// getMeasuredHeight(), MeasureSpec.EXACTLY);
		// int childHeightSpec =
		// ViewGroup.getChildMeasureSpec(heightMeasureSpec,
		// getPaddingTop() + getPaddingBottom(), params.height);
		// int childWidthSpec = MeasureSpec.makeMeasureSpec(
		// params.width > 0 ? params.width : 0, MeasureSpec.UNSPECIFIED);
		// child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * Gets the real height.
	 * 
	 * @return the real height
	 */
	@SuppressWarnings("unused")
	private int getRealHeight() {
		return getHeight() - (getPaddingTop() + getPaddingBottom());
	}

	/**
	 * Gets the real width.
	 * 
	 * @return the real width
	 */
	@SuppressWarnings("unused")
	private int getRealWidth() {
		return getWidth() - (getPaddingLeft() + getPaddingRight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (mAdapter == null) {
			return;
		}

		if (mDataChanged) {
			int oldCurrentX = mCurrentX;
			initView();
			removeAllViewsInLayout();
			mNextX = oldCurrentX;
			mDataChanged = false;
		}

		if (mScroller.computeScrollOffset()) {
			int scrollx = mScroller.getCurrX();
			mNextX = scrollx;
		}

		// �����޸�
		if (mNextX < 0) {
			mNextX = 0;
			mScroller.forceFinished(true);
		}
		// if (mNextX < mMinX) {
		// mNextX = mMinX;
		// mScroller.forceFinished(true);
		// }

		if (mNextX > mMaxX) {
			mNextX = mMaxX;
			mScroller.forceFinished(true);
		}

		// �����޸�
		int dx = mCurrentX - mNextX;

		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);
		// int dx = 0;
		// switch (mLayoutMode) {
		// case LAYOUT_SPECIFIC:
		// dx = mSpecificLeft;
		// detachAllViewsFromParent();
		// initViewForSpecific();
		// fillSpecific(mSpecificPosition, dx);
		// positionItems(dx);
		// mNextX = mSpecificLeft;
		// mSpecificPosition = -1;
		// mSpecificLeft = 0;
		// break;
		// default:
		// dx = mCurrentX - mNextX;
		// removeNonVisibleItems(dx);
		// fillList(dx);
		// positionItems(dx);
		// }

		mCurrentX = mNextX;
		mLayoutMode = LAYOUT_NORMAL;

		if (!mScroller.isFinished()) {
			post(new Runnable() {

				@Override
				public void run() {
					requestLayout();
				}
			});

		}
	}

	/**
	 * Fill list.
	 * 
	 * @param dx
	 *            the dx
	 */
	private void fillList(final int dx) {
		int edge = 0;
		View child = getChildAt(getChildCount() - 1);
		if (child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);

		edge = 0;
		child = getChildAt(0);
		if (child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);
	}

	private void fillSpecific(int position, int top) {
		View child = mAdapter.getView(position, null, null);
		if (child == null)
			return;
		addAndMeasureChild(child, -1);
		int edge = 0;
		if (child != null) {
			edge = child.getRight();
			fillListRight(edge, top);
			edge = child.getLeft();
			fillListLeft(edge, top);
		}
	}

	/**
	 * Fill list right.
	 * 
	 * @param rightEdge
	 *            the right edge
	 * @param dx
	 *            the dx
	 */
	private void fillListRight(int rightEdge, final int dx) {
		while (rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {

			View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, -1);
			rightEdge += child.getMeasuredWidth();

			if (mRightViewIndex == mAdapter.getCount() - 1) {
				mMaxX = mCurrentX + rightEdge - getWidth();
			}
			if (mMaxX < 0) { // �����������Ҷ����bug
				mMaxX = 0;
			}
			mRightViewIndex++;
		}

	}

	/**
	 * Fill list left.
	 * 
	 * @param leftEdge
	 *            the left edge
	 * @param dx
	 *            the dx
	 */
	private void fillListLeft(int leftEdge, final int dx) {
		while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
			View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, 0);
			leftEdge -= child.getMeasuredWidth();

			/* ������� */
			if (mLeftViewIndex == 0) {
				mMinX = mCurrentX + leftEdge;
			}
			if (mMinX > 0) {
				mMinX = 0;
			}

			mLeftViewIndex--;
			mDisplayOffset -= child.getMeasuredWidth();
		}
		mFirstPosition = mLeftViewIndex + 1;
	}

	/**
	 * Removes the non visible items.
	 * 
	 * @param dx
	 *            the dx
	 */
	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while (child != null && child.getRight() + dx <= 0) {
			mDisplayOffset += child.getMeasuredWidth();
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mLeftViewIndex++;
			child = getChildAt(0);
		}

		child = getChildAt(getChildCount() - 1);
		while (child != null && child.getLeft() + dx >= getWidth()) {
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mRightViewIndex--;
			child = getChildAt(getChildCount() - 1);
		}
	}

	/**
	 * Position items.
	 * 
	 * @param dx
	 *            the dx
	 */
	private void positionItems(final int dx) {
		if (getChildCount() > 0) {
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				int childTop = getPaddingTop();
				// �����޸�
				child.layout(left, childTop, left + childWidth, childTop + child.getMeasuredHeight());
				left += childWidth;
				// child.layout(left, 0, left + childWidth,
				// child.getMeasuredHeight());
				// left += childWidth + child.getPaddingRight();
			}
		}
	}

	/**
	 * Scroll to.
	 * 
	 * @param x
	 *            the x
	 */
	public synchronized void scrollTo(int x) {
		mScroller.startScroll(mNextX, 0, x - mNextX, 0);
		requestLayout();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		handled |= mGesture.onTouchEvent(ev);
		return handled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
	 */
	/*
	 * @Override public boolean dispatchTouchEvent( MotionEvent ev ) { boolean
	 * handled = mGesture.onTouchEvent( ev ); return handled; }
	 */

	/**
	 * On fling.
	 * 
	 * @param e1
	 *            the e1
	 * @param e2
	 *            the e2
	 * @param velocityX
	 *            the velocity x
	 * @param velocityY
	 *            the velocity y
	 * @return true, if successful
	 */
	public long flipingtime;

	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		synchronized (HorizontalListView.this) {
			// �����޸�
			flipingtime = System.currentTimeMillis();
			// Log.i( LoggerFactory.LOG_TAG, "onFling: " + -velocityX + ", "
			// +
			// mMaxX );
			mScroller.fling(mNextX, 0, (int) -(velocityX / 2), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
			// mScroller.fling(mNextX, 0, (int) -velocityX, 0, mMinX, mMaxX, 0,
			// 0);
		}
		requestLayout();

		return true;
	}

	/** The m scroller running. */
	boolean mScrollerRunning;

	/**
	 * On down.
	 * 
	 * @param e
	 *            the e
	 * @return true, if successful
	 */
	protected boolean onDown(MotionEvent e) {

		if (!mScroller.isFinished()) {
			mScrollerRunning = true;
		} else {
			mScrollerRunning = false;
		}

		mScroller.forceFinished(true);
		return true;
	}

	/** The m on gesture. */
	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return HorizontalListView.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

			synchronized (HorizontalListView.this) {
				mNextX += (int) distanceX;
			}
			requestLayout();

			return true;
		}

		// �����޸�
		// @Override
		// public boolean onSingleTapConfirmed(MotionEvent e) {
		// // Log.i( LoggerFactory.LOG_TAG, "onSingleTapConfirmed" );
		//
		// if (mScrollerRunning)
		// return false;
		//
		// Rect viewRect = new Rect();
		// for (int i = 0; i < getChildCount(); i++) {
		// View child = getChildAt(i);
		// int left = child.getLeft();
		// int right = child.getRight();
		// int top = child.getTop();
		// int bottom = child.getBottom();
		// viewRect.set(left, top, right, bottom);
		// if (viewRect.contains((int) e.getX(), (int) e.getY())) {
		// if (mOnItemClicked != null) {
		// mOnItemClicked.onItemClick(HorizontalListView.this,
		// child, mLeftViewIndex + 1 + i,
		// mAdapter.getItemId(mLeftViewIndex + 1 + i));
		// }
		// if (mOnItemSelected != null) {
		// mOnItemSelected.onItemSelected(HorizontalListView.this,
		// child, mLeftViewIndex + 1 + i,
		// mAdapter.getItemId(mLeftViewIndex + 1 + i));
		// }
		// break;
		// }
		//
		// }
		// return true;
		// }

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if (mOnItemClicked != null) {
						mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					if (mOnItemSelected != null) {
						mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					return true;
				}
			}
			if (onHLVBlackClickListener != null) {
				onHLVBlackClickListener.onBlankClick();
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if (mOnItemLongClicked != null) {
						mOnItemLongClicked.onItemLongClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					break;
				}
			}
		}

		private boolean isEventWithinView(MotionEvent e, View child) {
			Rect viewRect = new Rect();
			int[] childPosition = new int[2];
			child.getLocationOnScreen(childPosition);
			int left = childPosition[0];
			int right = left + child.getWidth();
			int top = childPosition[1];
			int bottom = top + child.getHeight();
			viewRect.set(left, top, right, bottom);
			return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
		}

	};

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// boolean handled = mGesture.onTouchEvent(event);
	// return handled;
	// }
	//
	// private float mInitialX;
	// private float mInitialY;
	//
	// @Override
	// public boolean onInterceptTouchEvent(MotionEvent ev) {
	// boolean intercept = false;
	// switch (ev.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// mInitialX = ev.getX();
	// mInitialY = ev.getY();
	// intercept = false;
	// break;
	// case MotionEvent.ACTION_MOVE:
	// float deltaX = Math.abs(ev.getX() - mInitialX);
	// float deltaY = Math.abs(ev.getY() - mInitialY);
	// intercept = (deltaX > 5 || deltaY > 5);
	// break;
	// default:
	// intercept = super.onInterceptTouchEvent(ev);
	// }
	// if (!intercept) {
	// mGesture.onTouchEvent(ev);
	// }
	// return intercept;
	// }

	private OnHLVBlackClickListener onHLVBlackClickListener = null;

	public void setOnHLVBlackClickListener(OnHLVBlackClickListener l) {
		this.onHLVBlackClickListener = l;
	}

	/**
	 * ��HorizontalListView��item��������������ؼ�ʱ������հ״����¼�����
	 */
	public interface OnHLVBlackClickListener {
		public void onBlankClick();
	}

}
