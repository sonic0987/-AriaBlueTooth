package com.norma.abc.ui.adt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

abstract class BaseUIAdapter<E> extends BaseAdapter {
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    List<E> mList;
    private List<E> originList;
    BaseUIAdapter(Context ctx,int layoutRes) {
        mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutRes = layoutRes;
    }

    void setSource(List<E> list) {
        mList = list;
        originList = list;
        notifyDataSetChanged();
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
    List<E> getList(){
        return mList;
    }

    List<E> getOriginList() {
        return originList;
    }

    abstract void onBindView(View view, E item);
}