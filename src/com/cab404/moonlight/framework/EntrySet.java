package com.cab404.moonlight.framework;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Just for order-dependent request data. It isn't actually a map.
 *
 * @author cab404
 */
public class EntrySet<K, V> implements Iterable<Map.Entry<K, V>> {
    private ArrayList<Map.Entry<K, V>> data;

    public EntrySet() {
        data = new ArrayList<>();
    }

    public void put(K key, V value) {
        data.add(new AbstractMap.SimpleEntry<>(key, value));
    }

    @Override public Iterator<Map.Entry<K, V>> iterator() {
        return data.iterator();
    }
}
