package com.hyd.hybatis.row;

import java.util.HashMap;
import java.util.Map;

/**
 * 对 key 忽略大小写的 HashMap，注意存进去的 key 都会转为小写，也就是说 keys() 方法返回的都是小写
 */
public class CaseInsensitiveHashMap<V> extends HashMap<String, V> {

    private static final long serialVersionUID = 1L;

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        if (m instanceof CaseInsensitiveHashMap) {
            super.putAll(m);
        } else if (m == null) {
            return;
        }

        Map<String, V> m2 = new HashMap<>();
        m.forEach((k, v) -> m2.put(k.toLowerCase(), v));
        super.putAll(m2);
    }

    /**
     * 根据 key 获取值
     *
     * @param key 键
     *
     * @return 相对应的值
     *
     * @throws IllegalArgumentException 如果 key 不是一个字符串
     */
    public V get(String key) {
        return super.get(key.toLowerCase());
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     *
     * @throws IllegalArgumentException 如果 key 不是一个字符串
     */
    public V put(String key, V value) {
        return super.put(key.toLowerCase(), value);
    }

    /**
     * 检查 key 是否存在
     *
     * @param key 要检查的 key
     *
     * @return 如果存在则返回 true
     */
    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Key must be a string.");
        }

        String lcKey = String.valueOf(key).toLowerCase();
        return super.containsKey(lcKey);
    }

    @Override
    public V remove(Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Key must be a string.");
        }

        String lcKey = String.valueOf(key).toLowerCase();
        return super.remove(lcKey);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Key must be a string.");
        }

        String lcKey = String.valueOf(key).toLowerCase();
        return super.remove(lcKey, value);
    }
}
