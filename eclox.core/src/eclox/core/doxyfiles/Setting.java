/*******************************************************************************
 * Copyright (C) 2003-2004, 2013 Guillaume Brocker
 * Copyright (C) 2015-2017, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - #171: added sorting column in advanced tab
 *                   - #211: Added support for the += operator
 *                   - #212: add support for multiple lines (lists) concatenated by backslash (\)
 *                   - #214: add support for TAG and VALUE format
 *                   - #215: add support for line separator
 *
 ******************************************************************************/

package eclox.core.doxyfiles;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eclox.core.Plugin;
import eclox.core.doxyfiles.Chunk;

/**
 * Implements the setting chunk.
 *
 * @author gbrocker
 */
public class Setting extends Chunk {

    /**
     * The value pattern.
     */
    private static Pattern valuePattern = Pattern.compile("([^ \"]+)\\s*|\"((\\\\\"|.)*?)\"\\s*");

    private static String fixedLinePrefix = new String("                         ");

    /**
     * The default setting properties.
     */
    private static Properties defaultProperties;

    /**
     * A string containing the node identifier.
     */
    private final String identifier;

    /**
     * A collection with all attached listeners
     */
    private Set<ISettingListener> listeners = new HashSet<ISettingListener>();

    /**
     * The string containing the operator (= or +=).
     */
    private String operator;

    /**
     * The string containing the setting value.
     */
    private String value;

    /**
     * The boolean telling if the setting assignment is continued on multiple line.
     */
    private boolean continued;

    /**
     * The setting local properties.
     */
    private Properties properties = new Properties();

    /**
     * Defines the assignment operator.
     */
    public static final String ASSIGNMENT = "=";

    /**
     * Defines the assignment operator.
     */
    public static final String INCREMENT = "+=";

    /**
     * Defines the group property name.
     */
    public static final String GROUP = "group";

    /**
     * Defines the note property name.
     */
    public static final String NOTE = "note";

    /**
     * Defines the text property name.
     */
    public static final String TEXT = "text";

    /**
     * Defines the type property name.
     */
    public static final String TYPE = "type";

    /**
     * Initializes all setting default properties.
     */
    private static void initDefaultProperties() {
        // Ensures that properties have been loaded.
        if (defaultProperties == null) {
            try {
                InputStream propertiesInput = Plugin.getResourceAsStream("/misc/setting-properties.txt");
                defaultProperties = new Properties();
                if (propertiesInput != null)
                    defaultProperties.load(propertiesInput);
            } catch (Throwable throwable) {
                Plugin.log(throwable);
            }
        }
    }

    /**
     * Retrieves the specified default property given a setting.
     *
     * @param   setting     a setting instance
     * @param   property    a string containing the name of the property to retrieve.
     *
     * @return  a string containing the desired property value or null when
     *          no such property exists
     */
    private static String getDefaultPropertyValue(Setting setting, String property) {
        initDefaultProperties();
        String propertyIdentifier = setting.getIdentifier() + "." + property;
        return defaultProperties.getProperty(propertyIdentifier);
    }

    /**
     * Constructor.
     *
     * @param   identifier  a string containing the setting identifier
     * @param   value       a string containing the setting value
     * @param   operator    a string containing the setting operator
     */
    public Setting(String identifier, String value, String operator, boolean continued) {
        this.identifier = new String(identifier);
        this.value = new String(value);
        this.operator = new String(operator);
        this.continued = continued;
    }

    /**
     * Attaches a new setting listener instance.
     *
     * @param	listener	a new setting listener instance
     */
    public void addSettingListener(ISettingListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Retrieves the node identifier.
     *
     * @return	a string containing the node identifier
     */
    public final String getIdentifier() {
        return this.identifier;
    }

    /**
     * Retrieves the value of the specified property.
     *
     * @param   property    a string containing a property name
     *
     * @return  a string containing the property value or null when the property was not found
     */
    public String getProperty(String property) {
        if (properties.containsKey(property)) {
            return properties.getProperty(property);
        } else {
            return getDefaultPropertyValue(this, property);
        }
    }

    /**
     * Retrieves the setting operator.
     *
     * @return  a string containing the setting operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Retrieves the setting value.
     *
     * @return	a string containing the setting value
     */
    public String getValue() {
        return value;
    }

    /**
     * Retrieves the setting continued flag.
     *
     * @return  a boolean containing the setting continued flag
     */
    public boolean isContinued() {
        return this.continued;
    }

    /**
     * Retrieves the splitted setting value.
     *
     * @param collection	a collection instance that will receive the value parts
     *
     * @return	the collection that received the value parts
     */
    public Collection<String> getSplittedValue(Collection<String> collection) {
        Matcher valueMatcher = valuePattern.matcher(value);
        while (valueMatcher.find() == true) {
            String value = valueMatcher.group(1);
            if (value == null) {
                value = valueMatcher.group(2).trim();
            }
            collection.add(value);
        }
        return collection;
    }

    /**
     * Tells if the setting has the givgen property set.
     *
     * @param	property		a string containing a property name
     *
     * @return	a boolean
     */
    public boolean hasProperty(String property) {
        if (properties.containsKey(property)) {
            return true;
        } else {
            return getDefaultPropertyValue(this, property) != null;
        }
    }

    /**
     * Detaches a new setting listener instance.
     *
     * @param	listener	a attached setting listener instance
     */
    public void removeSettingListener(ISettingListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Updates the value of a given property.
     *
     * @param	property	a string containing the name of property
     * @param	value		a string containing the property value
     */
    public void setProperty(String property, String value) {
        // Updates the given property.
        properties.setProperty(property, value);

        // Walks through the attached listeners and notify them.
        Iterator<ISettingListener> i = this.listeners.iterator();
        while (i.hasNext() == true) {
            ISettingListener listener = (ISettingListener) i.next();
            if (listener instanceof ISettingPropertyListener) {
                ISettingPropertyListener propertyListener = (ISettingPropertyListener) listener;
                propertyListener.settingPropertyChanged(this, property);
            }
        }
    }

    /**
     * Removes the given property for the setting.
     *
     * @param	property		a string containing a property name
     */
    public void removeProperty(String property) {
        if (properties.containsKey(property)) {
            properties.remove(property);

            // Walks through the attached listeners and notify them.
            Iterator<ISettingListener> i = this.listeners.iterator();
            while (i.hasNext() == true) {
                ISettingListener listener = (ISettingListener) i.next();
                if (listener instanceof ISettingPropertyListener) {
                    ISettingPropertyListener propertyListener = (ISettingPropertyListener) listener;
                    propertyListener.settingPropertyRemoved(this, property);
                }
            }
        }
    }

    /**
     * Updates the operator of the setting.
     *
     * @param   operator   a string representing a operator to set
     */
    public void setOperator(String operator) {
        this.operator = new String(operator);
    }

    /**
     * Updates the value of the setting.
     *
     * @param	value	a string representing a value to set
     */
    public void setValue(String value) {
        this.value = new String(value);
        fireValueChangedEvent();
    }

    /**
     * Updates the continued flag of the setting.
     *
     * @param   continued   a boolean representing a continued flag to set
     */
    public void setContinued(boolean continued) {
        this.continued = continued;
    }

    /**
     * Updates the value of the string with the given object collection. All objects
     * of the given collection will be converted to strings.
     *
     * @param compounds	a collection of objects representing compounds of the new value
     */
    public void setValue(Collection<?> compounds) {
        // Resets the managed value string.
        value = new String();
        // Walks through the comounds to rebuild the value.
        Iterator<?> i = compounds.iterator();
        while (i.hasNext()) {
            // Retrieves the current compound.
            String compound = i.next().toString();
            // Removes any leading and tralling spaces and manage the insertion.
            compound = compound.trim();
            if (compound.length() == 0) {
                continue;
            } else if (compound.indexOf(' ') != -1) {
                value = value.concat("\"" + compound + "\" ");
            } else {
                value = value.concat(compound + " ");
            }
        }

        // Notifies all observers.
        fireValueChangedEvent();
    }

    private String toString_ListSeparated(String linePrefix, String lineSeparator) {
        String valueOut = new String();
        Collection<String> compounds = new Vector<String>();
        getSplittedValue(compounds);
        // Walks through the comounds to rebuild the value.
        Iterator<?> i = compounds.iterator();
        boolean first = true;
        while (i.hasNext()) {
            // Retrieves the current compound.
            String compound = i.next().toString();
            // Removes any leading and tralling spaces and manage the insertion.
            compound = compound.trim();
            if (compound.length() == 0) {
                continue;
            } else {
                // add quotes ?
                String quotes = new String();
                if (compound.indexOf(' ') != -1) {
                    quotes = "\"";
                }
                if (first) {
                    first = false;
                } else {
                    valueOut = valueOut.concat(" \\" + lineSeparator + linePrefix);
                }
                valueOut = valueOut.concat(quotes + compound + quotes);
            }
        }
        return valueOut;
    }

    private String toString_ListSeparate(String lineSeparator) {
        if (Plugin.getDefault().isIdFixedLengthEnabled()) {
            return toString_ListSeparated(fixedLinePrefix, lineSeparator);
        } else {
            String linePrefix = new String();
            //ID.=.
            for (int i=0;i<(this.identifier.length() + 1 + this.operator.length() + 1);i++) {
                linePrefix = linePrefix.concat(" ");
            }
            return toString_ListSeparated(linePrefix, lineSeparator);
        }
    }

    private String toString_List(String lineSeparator) {
        switch(Plugin.getDefault().listSeparateMode()) {
            case 1 : return toString_ListSeparate(lineSeparator);
            case 2 : return value;
            //case 0 :
            default: return this.continued ? toString_ListSeparate(lineSeparator) : value;
        }
    }

    private String toString_IdLengthFixed(String valueOut, String lineSeparator) {
        String idStr = String.format("%-" + Integer.toString(fixedLinePrefix.length() - 1 - this.operator.length()) + "s", this.identifier);
        String valueStr = ((valueOut != null && !valueOut.isEmpty()) ? " " + valueOut : "");
        return idStr + this.operator + valueStr + lineSeparator;
    }

    private String toString_IdLengthTrimmed(String valueOut, String lineSeparator) {
        return this.identifier + " " + this.operator + ((valueOut != null && !valueOut.isEmpty()) ? " " : "") + valueOut + lineSeparator;
    }

    private String toString_Id(String valueOut, String lineSeparator) {
        if (Plugin.getDefault().isIdFixedLengthEnabled()) {
            return toString_IdLengthFixed(valueOut, lineSeparator);
        } else {
            return toString_IdLengthTrimmed(valueOut, lineSeparator);
        }
    }

    public String getString(String lineSeparator) {
        return toString_Id(toString_List(lineSeparator), lineSeparator);
    }

    /**
     * Notifies all observers that the setting value has changed.
     */
    private void fireValueChangedEvent() {
        Iterator<ISettingListener> i = listeners.iterator();
        while (i.hasNext() == true) {
            ISettingListener listener = (ISettingListener) i.next();
            if (listener instanceof ISettingValueListener) {
                ISettingValueListener valueListener = (ISettingValueListener) listener;
                valueListener.settingValueChanged(this);
            }
        }
    }

    public String getTextLabel(String propDirty) {
        String columnText;
        columnText = hasProperty(Setting.TEXT) ? getProperty(Setting.TEXT) : getIdentifier();
        columnText = hasProperty(propDirty) ? ("*").concat(columnText) : columnText;
        return columnText;
    }

    public String getValueLabel() {
        return getValue();
    }

}
