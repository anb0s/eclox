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

package eclox.core;

import java.util.ArrayList;
import java.util.List;

public enum TagFormat {
    tagFormatDoNotChange("Do not change"),
    tagFormatTrimmed("Trimmed length: 'TAG = VALUE'"),
    tagFormatFixed("Fixed length: 25 characters for TAG, operator and space, e.g.: 'TAG.[..].=.VALUE'");
    // attributes
    private final String name;

    // construct
    TagFormat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TagFormat getFromName(String name) {
        TagFormat ret = tagFormatDoNotChange;
        for (int i = 0; i < TagFormat.values().length; i++) {
            if (TagFormat.values()[i].getName().equals(name)) {
                ret = TagFormat.values()[i];
                break;
            }
        }
        return ret;
    }

    public static TagFormat getFromEnum(String name) {
        return valueOf(name);
    }

    public static List<String> getNamesAsList() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < TagFormat.values().length; i++) {
            list.add(TagFormat.values()[i].getName());
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