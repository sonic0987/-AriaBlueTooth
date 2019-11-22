package com.norma.abc.utils;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class LimitedLinkHashMap<K, V> extends LinkedHashMap<K, V> {
    private final int mMaxSize;
    public LimitedLinkHashMap(final int maxSize) {
        super(maxSize + 1, 1, false);
        mMaxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return this.size() > mMaxSize;
    }
}