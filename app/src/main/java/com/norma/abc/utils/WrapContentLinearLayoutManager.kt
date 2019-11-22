package com.norma.abc.utils

import android.content.Context

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class WrapContentLinearLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean)
    : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

    }
}