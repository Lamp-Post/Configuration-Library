package com.feildmaster.lib.configuration;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.*;

// mapChildrenValues/Keys
// getValues
// static CreatePath
/**
 * A placeholder for enhanced sections
 *
 * @author Feildmaster
 */
public class EnhancedMemorySection extends MemorySection implements EnhancedConfigurationSection {
    protected final EnhancedConfiguration superParent;

    public EnhancedMemorySection(EnhancedConfiguration superParent, MemorySection parent, String path) {
        super(parent, path);
        this.superParent = superParent;
    }

    @Override
    public void set(String path, Object value) {
        Validate.notNull(path, "Path cannot be null");
        Validate.notEmpty(path, "Cannot set to an empty path");

        if (value != null && !value.equals(get(path)) || value == null && get(path) != null) {
            superParent.modified = true;
        }

        final char seperator = getRoot().options().pathSeparator();
        // i1 is the leading (higher) index
        // i2 is the trailing (lower) index
        int i1 = -1, i2;
        EnhancedConfigurationSection section = this;
        while ((i1 = path.indexOf(seperator, i2 = i1 + 1)) != -1) {
            String node = path.substring(i2, i1);
            EnhancedConfigurationSection subSection = section.getConfigurationSection(node);
            if (subSection == null) {
                section = section.createLiteralSection(node);
            } else {
                section = subSection;
            }
        }

        String key = path.substring(i2);
        if (section == this) {
            if (value == null) {
                map.remove(key);
            } else {
                map.put(key, value);
            }
        } else {
            section.set(key, value);
        }
    }

    @Override
    public Object get(String path, Object def) {
        Validate.notNull(path, "Path cannot be null");
        if (path.length() == 0) {
            return this;
        }

        final char seperator = getRoot().options().pathSeparator();
        // i1 is the leading (higher) index
        // i2 is the trailing (lower) index
        int i1 = -1, i2;
        ConfigurationSection section = this;
        while ((i1 = path.indexOf(seperator, i2 = i1 + 1)) != -1) {
            section = section.getConfigurationSection(path.substring(i2, i1));
            if (section == null) {
                return def;
            }
        }

        String key = path.substring(i2);
        if (section == this) {
            Object result = map.get(key);
            return (result == null) ? def : result;
        }
        return section.get(key, def);
    }

    @Override
    public EnhancedConfigurationSection getConfigurationSection(String path) {
        return (EnhancedConfigurationSection) super.getConfigurationSection(path);
    }

    @Override
    public EnhancedConfigurationSection createSection(String path) {
        Validate.notNull(path, "Path cannot be null");
        Validate.notEmpty(path, "Cannot create section at empty path");

        final char seperator = getRoot().options().pathSeparator();
        // i1 is the leading (higher) index
        // i2 is the trailing (lower) index
        int i1 = -1, i2;
        EnhancedConfigurationSection section = this;
        while ((i1 = path.indexOf(seperator, i2 = i1 + 1)) != -1) {
            String node = path.substring(i2, i1);
            EnhancedConfigurationSection subSection = section.getConfigurationSection(node);
            if (subSection == this) {
                section = section.createLiteralSection(node);
            } else {
                section = subSection;
            }
        }

        String key = path.substring(i2);
        if (section == this) {
            return createLiteralSection(key);
        }
        return section.createLiteralSection(key);
    }

    @Override
    public EnhancedConfigurationSection createLiteralSection(String key) {
        EnhancedConfigurationSection section = new EnhancedMemorySection(superParent, this, key);
        map.put(key, section);
        return section;
    }
}
