package org.zhzephi.recycler.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ZHZEPHI on 2015/10/12.
 */
public class ZRecyclerView extends RecyclerView {

    private View emptyView;
    public OnLoadListener onLoadListener; //Load more

    public ZRecyclerView(Context context) {
        super(context);
    }

    public ZRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        checkIfEmpty();
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        super.swapAdapter(adapter, removeAndRecycleExistingViews);
        checkIfEmpty();
    }

    AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmpty();
        }
    };

    private void checkIfEmpty() {
        if (emptyView == null || getAdapter() == null) {
            return;
        }
        if (getAdapter().getItemCount() > 0) {
            emptyView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    public void setEmptyView(View emptyView) {
        if (this.emptyView != null) {
            this.emptyView.setVisibility(View.GONE);
        }
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    public void setOnLoadListener(final OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LayoutManager layoutManager = getLayoutManager();
                int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                //lastVisibleItem>=totalItemCount-1 is lost item，dy>0 is down
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    onLoadListener.onLoad();
                }
            }
        });
    }

    public interface OnLoadListener {
        void onLoad();
    }
}
