/*******************************************************************************
 * Copyright (C) 2015-2018, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andre Bossert - #171: added sorting column in advanced tab
 *     see: https://github.com/eclipse/eclipse.platform.ui/blob/master/examples/org.eclipse.jface.snippets/Eclipse%20JFace%20Snippets/org/eclipse/jface/snippets/viewers/Snippet040TableViewerSorting.java
 *
 ******************************************************************************/

package eclox.ui.editor.advanced;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;

public abstract class ColumnViewerComparator extends ViewerComparator {

    public static final int ASC = 1;
    public static final int NONE = 0;
    public static final int DESC = -1;

    private int direction = 0;
    private TableViewerColumn column;
    private ColumnViewer viewer;

    public ColumnViewerComparator(ColumnViewer viewer, TableViewerColumn column) {
        this.column = column;
        this.viewer = viewer;
        SelectionAdapter selectionAdapter = createSelectionAdapter();
        this.column.getColumn().addSelectionListener(selectionAdapter);
    }

    private SelectionAdapter createSelectionAdapter() {
        return new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (ColumnViewerComparator.this.viewer.getComparator() != null) {
                    if (ColumnViewerComparator.this.viewer.getComparator() == ColumnViewerComparator.this) {
                        int tdirection = ColumnViewerComparator.this.direction;
                        if (tdirection == ASC) {
                            setSorter(ColumnViewerComparator.this, DESC);
                        } else if (tdirection == DESC) {
                            setSorter(ColumnViewerComparator.this, NONE);
                        }
                    } else {
                        setSorter(ColumnViewerComparator.this, ASC);
                    }
                } else {
                    setSorter(ColumnViewerComparator.this, ASC);
                }
            }
        };
    }

    public void setSorter(ColumnViewerComparator sorter, int direction) {
        Table columnParent = column.getColumn().getParent();
        if (direction == NONE) {
            columnParent.setSortColumn(null);
            columnParent.setSortDirection(SWT.NONE);
            viewer.setComparator(null);

        } else {
            columnParent.setSortColumn(column.getColumn());
            sorter.direction = direction;
            columnParent.setSortDirection(direction == ASC ? SWT.DOWN : SWT.UP);

            if (viewer.getComparator() == sorter) {
                viewer.refresh();
            } else {
                viewer.setComparator(sorter);
            }

        }
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        return direction * doCompare(viewer, e1, e2);
    }

    protected abstract int doCompare(Viewer viewer, Object e1, Object e2);
}
