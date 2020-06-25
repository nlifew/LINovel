package cn.nlifew.linovel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityViewHolder<T> extends RecyclerView.ViewHolder {

    public ActivityViewHolder(Activity activity, @LayoutRes int id, ViewGroup parent) {
        super(LayoutInflater.from(activity).inflate(id, parent, false));
        mActivity = activity;
    }

    public ActivityViewHolder(Activity activity, View view) {
        super(view);
        mActivity = activity;
    }

    protected final Activity mActivity;

    public void onBindViewHolder(T t) {  }
}
