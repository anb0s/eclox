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

public enum MenuItemType {
    unknown(-1, "Unknown", Images.DEFAULT.getId()), buildDoxyfile(0, "Build Doxyfile",
            Images.DOXYFILE.getId()), chooseDoxyfile(1, "Build Doxyfile", Images.EXPLORE.getId()), updateDoxyfile(2,
                    "Update Doxyfile", Images.UPDATE.getId()), clearHistory(3, "Clear History", Images.ERASE.getId());
    // attributes
    private final int id;
    private final String name;
    private final String imageId;

    // construct
    MenuItemType(int id, String name, String imageId) {
        this.id = id;
        this.name = name;
        this.imageId = imageId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageId() {
        return imageId;
    }

    public static MenuItemType getFromId(int id) {
        MenuItemType ret = unknown;
        for (MenuItemType item : MenuItemType.values()) {
            if (item.getId() == id) {
                ret = item;
                break;
            }
        }
        return ret;
    }

    public static MenuItemType getFromName(String name) {
        MenuItemType ret = unknown;
        for (MenuItemType item : MenuItemType.values()) {
            if (item.getName().equals(name)) {
                ret = item;
                break;
            }
        }
        return ret;
    }

    public static MenuItemType getFromEnum(String name) {
        return MenuItemType.valueOf(name);
    }

    public static List<MenuItemType> getValidValues() {
        List<MenuItemType> list = new ArrayList<MenuItemType>();
        for (MenuItemType item : MenuItemType.values()) {
            if (item != unknown) {
                list.add(item);
            }
        }
        return list;
    }

    public static List<String> getNamesAsList() {
        List<String> list = new ArrayList<String>();
        for (MenuItemType item : MenuItemType.values()) {
            if (item != unknown) {
                list.add(item.getName());
            }
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

    public static List<String> getImageIdsAsList() {
        List<String> list = new ArrayList<String>();
        for (MenuItemType item : MenuItemType.values()) {
            if (item != unknown) {
                list.add(item.getImageId());
            }
        }
        return list;
    }
}
