/*******************************************************************************
 * Copyright (C) 2015-2018, Andre Bossert
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

package eclox.core;

import java.util.ArrayList;
import java.util.List;

public enum ListSeparateMode {
    listSeparateModeDoNotChange("Do not change"),
    listSeparateModeSeparate("Separate entries with '\\'"),
    listSeparateOneLine("One line");
    // attributes
    private final String name;

    // construct
    ListSeparateMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ListSeparateMode getFromName(String name) {
        ListSeparateMode ret = listSeparateModeDoNotChange;
        for (int i = 0; i < ListSeparateMode.values().length; i++) {
            if (ListSeparateMode.values()[i].getName().equals(name)) {
                ret = ListSeparateMode.values()[i];
                break;
            }
        }
        return ret;
    }

    public static ListSeparateMode getFromEnum(String name) {
        return valueOf(name);
    }

    public static List<String> getNamesAsList() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < ListSeparateMode.values().length; i++) {
            list.add(ListSeparateMode.values()[i].getName());
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

    public static String getNameFromEnum(String id) {
        return getFromEnum(id).getName();
    }

}