package com.norma.abc.ui.adt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.lifecycle.ViewModel;

import java.util.List;

abstract class BaseAsyncUIAdapter<E> extends BaseAdapter {
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<E> mList;
    BaseAsyncUIAdapter(Context ctx, int layoutRes, List<E> list) {
        mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutRes = layoutRes;
        this.mList = list;
    }

    public E getItemPosition(int position) {
        if (mList == null) {
            return null;
        }

        return mList.get(position);
    }

    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(mLayoutRes, parent, false);
        } else {
            view = convertView;
        }
        onBindView(view, mList.get(position));
        return view;
    }

    abstract void onBindView(View view, E item);
}