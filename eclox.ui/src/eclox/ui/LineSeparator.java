/*******************************************************************************
 * Copyright (C) 2015-2017, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andre Bossert - first implementation
 *
 ******************************************************************************/

package eclox.ui;

import java.util.ArrayList;
import java.util.List;

public enum LineSeparator {
    lineSeparatorSystem("System", System.lineSeparator()), lineSeparatorLinux("Linux (CR = \\r)", "\r"),
    lineSeparatorMacOSX("Mac (LF = \\n)", "\n"), lineSeparatorWindows("Windows (CRLF = \\r\\n)", "\r\n");
    // attributes
    private final String name;
    private final String value;

    // construct
    LineSeparator(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static LineSeparator getFromId(String id) {
        return valueOf(id);
    }

    public static LineSeparator getFromName(String name) {
        LineSeparator ret = lineSeparatorSystem;
        for (int i = 0; i < LineSeparator.values().length; i++) {
            if (LineSeparator.values()[i].getName().equals(name)) {
                ret = LineSeparator.values()[i];
                break;
            }
        }
        return ret;
    }

    public static LineSeparator getFromValue(String value) {
        LineSeparator ret = lineSeparatorSystem;
        for (int i = 0; i < LineSeparator.values().length; i++) {
            if (LineSeparator.values()[i].getValue().equals(value)) {
                ret = LineSeparator.values()[i];
                break;
            }
        }
        return ret;
    }

    public static LineSeparator getFromEnum(String name) {
        return LineSeparator.valueOf(name);
    }

    public static List<String> getNamesAsList() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < LineSeparator.values().length; i++) {
            list.add(LineSeparator.values()[i].getName());
        }
        return list;
    }

    public static String[] getNamesAsArray() {
        List<String> list = getNamesAsList();
        String[] arr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    public static String getValueFromId(String id) {
        return getFromId(id).getValue();
    }

    public static String getNameFromId(String id) {
        return getFromId(id).getName();
    }

}