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

public enum Images {
    UNKNOWN("unknown"),
    // wizards
    DOXYFILE_WIZARD("doxyfile_wiz"),
    // console
    CLEAR_CONSOLE("clear_console"),
    ERASE("erase"),
    LOCK_CONSOLE("lock_console"),
    TERMINATE("terminate"),
    REMOVE("remove_console"),
    // other
    DEFAULT("default"),
    DOXYFILE("doxyfile"),
    ECLIPSE("eclipse"),
    ECLOX("eclox"),
    EXPLORE("explore2"),
    RUN("run"),
    USER("user");
    // attributes
    private final String id;
    // construct
    Images(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public static Images getFromId(String id) {
        Images ret = UNKNOWN;
        for(Images item : Images.values()) {
            if (item.getId().equals(id)) {
                ret = item;
                break;
            }
        }
        return ret;
    }
    public static Images getFromName(String name) {
        Images ret = UNKNOWN;
        for(Images item : Images.values()) {
            if (item.toString().equals(name)) {
                ret = item;
                break;
            }
        }
        return ret;
    }
    public static Images getFromEnum(String name) {
        return Images.valueOf(name);
    }
    public static List<Images> getValidValues() {
        List<Images> list = new ArrayList<Images>();
        for(Images item : Images.values()) {
            if (item != UNKNOWN) {
                list.add(item);
            }
        }
        return list;
    }
    public static List<String> getNamesAsList() {
        List<String> list = new ArrayList<String>();
        for(Images item : Images.values()) {
            if (item != UNKNOWN) {
                list.add(item.toString());
            }
        }
        return list;
    }
    public static String[] getNamesAsArray() {
        List<String> list = getNamesAsList();
        String[] arr = new String[list.size()];
        for (int i=0;i<list.size();i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
    public static List<String> getIdsAsList() {
        List<String> list = new ArrayList<String>();
        for(Images item : Images.values()) {
            if (item != UNKNOWN) {
                list.add(item.getId());
            }
        }
        return list;
    }
}
