package com.rk_tech.riggle_runner.utils.extension;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jatin on 6/5/2018.
 */

public class EndlessVerticalScroller extends RecyclerView.OnScrollListener {

    private int totalItemCount = 0;
    private int visibleItemCount = 0;
    private int pastVisiblesItems = 0;
    public boolean isLoading = false;
    private VerticalScrollListener onEndLessScrollListener = null;
    private int firstVisibleItem = 0;
    private int previousTotal = 0;
    private LinearLayoutManager linearLayoutManager;
    private int VISIBLE_THRESHOLD = 3;

    public EndlessVerticalScroller(LinearLayoutManager linearLayoutManager, int VISIBLE_THRESHOLD) {
        this.linearLayoutManager = linearLayoutManager;
        this.VISIBLE_THRESHOLD = VISIBLE_THRESHOLD;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        if (dy > 0) //check for scroll down{
            visibleItemCount = linearLayoutManager.getChildCount();
        totalItemCount = linearLayoutManager.getItemCount();
        pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
        if (!isLoading) {
            if (visibleItemCount + pastVisiblesItems >= totalItemCount - VISIBLE_THRESHOLD) {
                isLoading = true;
                if (onEndLessScrollListener != null)
                    onEndLessScrollListener.onLoadMore();
            }
        }
    }

    public void setListener(VerticalScrollListener onEndLessScroll) {
        onEndLessScrollListener = onEndLessScroll;
    }

    public interface VerticalScrollListener {
        void onLoadMore();
    }

}
