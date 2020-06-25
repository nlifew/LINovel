package cn.nlifew.linovel.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentViewHolder<T> extends RecyclerView.ViewHolder {

    public FragmentViewHolder(Fragment fragment, @LayoutRes int layoutId, ViewGroup parent) {
        super(LayoutInflater.from(fragment.getContext()).inflate(layoutId, parent, false));
        mFragment = fragment;
    }

    public FragmentViewHolder(Fragment fragment, View view) {
        super(view);
        mFragment = fragment;
    }

    protected final Fragment mFragment;
    private SparseArray<View> mViews;

    public void onBindViewHolder(T t) {  }

    @SuppressWarnings("unchecked")
    public <V extends View> V getView(@IdRes int id) {
        View view = null;

        if (mViews == null) {
            mViews = new SparseArray<>(6);
        }
        else {
            view = mViews.get(id);
        }

        if (view == null) {
            view = itemView.findViewById(id);
            mViews.put(id, view);
        }
        return (V) view;
    }
}
