package net.seesharpsoft.commons.collection;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Properties implements Map {
    
    private final Map properties;

    private final Properties parentProperties;
    
    public Properties(Properties parentProperties) {
        this.parentProperties = parentProperties;
        this.properties = new HashMap<>();
    }

    public Properties() {
        this(null);
    }
    
    protected String getKey(Object key) {
        if (key == null) {
            return null;
        }
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("only String keys allowed for properties!");
        }
        return (String)key;
    }
    
    public <T> T put(String key, Object value) {
        return (T)properties.put(key, value);
    }

    public <T> T get(String key) {
        Object value = null;
        if (properties.containsKey(key)) {
            value = properties.get(key);
        } else if (parentProperties != null) {
            value = parentProperties.get(key);
        }
        return (T)value;
    }

    public <T> T remove(String key) {
        return (T)properties.remove(key);
    }

    public void putAll(Map map) {
        if (map == null) {
            return;
        }
        properties.putAll(map);
    }

    @Override
    public int size() {
        return entrySet().size();
    }

    @Override
    public boolean isEmpty() {
        return properties.isEmpty() && parentProperties.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return properties.containsKey(key) || parentProperties.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return properties.containsValue(value) || parentProperties.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return get(getKey(key));
    }

    @Override
    public Object put(Object key, Object value) {
        return put(getKey(key), value);
    }

    @Override
    public Object remove(Object key) {
        return remove(getKey(key));
    }

    @Override
    public void clear() {
        properties.clear();
    }

    @Override
    public Set keySet() {
        return entrySet().stream().map(entry -> entry.getKey()).collect(Collectors.toSet());
    }

    @Override
    public Collection values() {
        return entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }

    @Override
    public Set<Entry> entrySet() {
        Set<Entry> result = new HashSet(properties.entrySet());
        if (parentProperties != null) {
            result.addAll(parentProperties.entrySet());
        }
        return result;
    }
    
    public void store(File file, boolean deep) throws IOException {
        if(!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("can not create file " + file);
            }
        }

        try(final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            Set<Entry> entries = deep ? this.entrySet() : properties.entrySet();
            for (Entry entry : entries) {
                if (entry.getKey() != null) {
                    writer.write(entry.getKey().toString());
                    writer.write('=');
                    Object value = entry.getValue();
                    writer.write(value == null ? "" : value.toString());
                    writer.newLine();
                }
            }
        }
    }

    public static Properties read(File file) throws IOException {
        if(!file.exists()) {
            return null;
        }
        Properties result = new Properties();
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.lines().forEach(line -> {
                String trimmedLine = line.trim();
                if (!trimmedLine.startsWith("#")) {
                    int splitIndex = trimmedLine.indexOf('=');
                    if (splitIndex != -1) {
                        result.put(trimmedLine.substring(0, splitIndex).trim(), trimmedLine.substring(splitIndex + 1).trim());
                    }
                }
            });
        }
        return result;
    }
    
    public java.util.Properties legacy() {
        java.util.Properties juProperties = new java.util.Properties();
        if (this.parentProperties != null) {
            juProperties.putAll(parentProperties);
        }
        juProperties.putAll(this.properties);
        return juProperties;
    }
}
