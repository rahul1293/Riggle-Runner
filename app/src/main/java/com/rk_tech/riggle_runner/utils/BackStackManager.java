package com.rk_tech.riggle_runner.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.rk_tech.riggle_runner.R;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by jatin on 7/25/2018.
 */

public class BackStackManager {

    private HashMap<String, Stack<Fragment>> backStack;
    private static BackStackManager _instance;
    private FragmentManager manager;
    @Nullable
    private String currentTab;

    @Nullable
    public String getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(@Nullable String currentTab) {
        this.currentTab = currentTab;
    }

    private BackStackManager() {
        backStack = new HashMap<>();
    }

    public static BackStackManager getInstance(FragmentActivity context) {
        if (_instance == null) {
            _instance = new BackStackManager();
        }
        _instance.manager = context.getSupportFragmentManager();
        return _instance;
    }

    public HashMap<String, Stack<Fragment>> getBackStack() {
        return backStack;
    }

    public void clear() {
        currentTab = null;
        _instance = null;
    }

    public void pushFragments(int containorid, String tag, Fragment fragment, boolean shouldAnimate) {
        currentTab = tag;
        FragmentTransaction ft = manager.beginTransaction();
        if (shouldAnimate)
            ft.setCustomAnimations(R.anim.alpha_undim,
                    R.anim.alpha_dim,
                    R.anim.alpha_undim,
                    R.anim.alpha_dim);
        if (backStack.containsKey(tag)) {
            Stack<Fragment> s = backStack.get(tag);
            if (s != null) {
                ft.replace(containorid, s.lastElement());
                ft.commit();
                if (fragmentChangeListioner != null) {
                    fragmentChangeListioner.onFragmentReplace(tag, s.size() > 1);
                }
            }
        } else {
            Stack<Fragment> s = new Stack<>();
            s.push(fragment);
            backStack.put(tag, s);
            ft.replace(containorid, fragment);
            ft.commit();
            if (fragmentChangeListioner != null) {
                fragmentChangeListioner.onFragmentReplace(tag, false);
            }
        }
    }

    public void pushSubFragments(int containorid, String tag, Fragment fragment, boolean shouldAnimate) {
        currentTab = tag;
        FragmentTransaction ft = manager.beginTransaction();
        if (shouldAnimate)
            ft.setCustomAnimations(R.anim.alpha_undim,
                    R.anim.alpha_dim,
                    R.anim.alpha_undim,
                    R.anim.alpha_dim);
        if (backStack.containsKey(tag)) {
            Stack<Fragment> s = backStack.get(tag);
            s.push(fragment);
            ft.replace(containorid, s.lastElement());
        } else {
            Stack<Fragment> s = new Stack<>();
            s.push(fragment);
            backStack.put(tag, s);
            ft.replace(containorid, fragment);
        }
        ft.commit();
        if (fragmentChangeListioner != null) {
            fragmentChangeListioner.onFragmentReplace(tag, true);
        }
    }

    public void pushSubFragmentsWithoutCache(int containorid, String tag, Fragment fragment, boolean shouldAnimate) {
        currentTab = tag;
        FragmentTransaction ft = manager.beginTransaction();
        if (shouldAnimate)
            ft.setCustomAnimations(R.anim.alpha_undim,
                    R.anim.alpha_dim,
                    R.anim.alpha_undim,
                    R.anim.alpha_dim);
        ft.replace(containorid, fragment);
        ft.commit();
    }

    public void setCurrentFragments(int containorid) {
        if (currentTab != null) {
            if (backStack.containsKey(currentTab)) {
                FragmentTransaction ft = manager.beginTransaction();
                Stack<Fragment> s = backStack.get(currentTab);
                ft.replace(containorid, s.lastElement());
                ft.commit();
            }
        }
    }

    public boolean removeFragment(int containorid, boolean animate) {
        if (currentTab == null)
            return true;
        if (backStack.containsKey(currentTab)) {
            Stack<Fragment> fragments = backStack.get(currentTab);
            if (fragments != null && fragments.size() > 1) {
                fragments.pop();
                FragmentTransaction ft = manager.beginTransaction();
                if (animate)
                    ft.setCustomAnimations(R.anim.alpha_undim,
                            R.anim.alpha_dim,
                            R.anim.alpha_undim,
                            R.anim.alpha_dim);
                ft.replace(containorid, fragments.lastElement());
                ft.commit();
                if (fragmentChangeListioner != null) {
                    fragmentChangeListioner.onFragmentReplace(currentTab, fragments.size() > 1);
                }
                Log.d(currentTab, "sub fragment removed");
                return false;
            } else return true;
        }
        return true;
    }

    @Nullable
    private FragmentChangeListioner fragmentChangeListioner;

    @Nullable
    public FragmentChangeListioner getFragmentChangeListioner() {
        return fragmentChangeListioner;
    }

    public void setFragmentChangeListioner(@Nullable FragmentChangeListioner fragmentChangeListioner) {
        this.fragmentChangeListioner = fragmentChangeListioner;
    }

    public interface FragmentChangeListioner {
        void onFragmentReplace(@NonNull String tag, boolean isSubFragment);
    }

}
