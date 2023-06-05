/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.datatable.export;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExporterOptions;
import org.primefaces.component.export.TableExporter;
import org.primefaces.model.LazyDataModel;

public abstract class DataTableExporter<P, O extends ExporterOptions> extends TableExporter<DataTable, P, O> {

    private static final int NO_ROW_INDEX_REQUIRED = Integer.MIN_VALUE;

    protected DataTableExporter(O defaultOptions) {
        super(defaultOptions);
    }

    protected DataTableExporter(O defaultOptions, Set<FacetType> supportedFacetTypes, boolean joinComponents) {
        super(defaultOptions, supportedFacetTypes, joinComponents);
    }

    @Override
    protected void exportSelectionOnly(FacesContext context, DataTable table) {
        Object selection = table.getSelection();
        String var = table.getVar();

        if (selection != null) {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

            if (selection.getClass().isArray()) {
                int size = Array.getLength(selection);

                for (int i = 0; i < size; i++) {
                    requestMap.put(var, Array.get(selection, i));
                    exportRow(context, table, NO_ROW_INDEX_REQUIRED);
                }
            }
            else if (Collection.class.isAssignableFrom(selection.getClass())) {
                for (Object obj : (Collection) selection) {
                    requestMap.put(var, obj);
                    exportRow(context, table, NO_ROW_INDEX_REQUIRED);
                }
            }
            else {
                requestMap.put(var, selection);
                exportRow(context, table, NO_ROW_INDEX_REQUIRED);
            }
        }
    }

    @Override
    protected void exportPageOnly(FacesContext context, DataTable table) {
        int first = table.getFirst();
        int rows = table.getRows();
        if (rows == 0) {
            rows = table.getRowCount();
        }

        int rowsToExport = first + rows;

        for (int rowIndex = first; rowIndex < rowsToExport; rowIndex++) {
            exportRow(context, table, rowIndex);
        }
    }

    @Override
    protected void exportAll(FacesContext context, DataTable table) {
        int first = table.getFirst();
        int rowCount = table.getRowCount();
        int rows = table.getRows();
        boolean lazy = table.isLazy();

        if (lazy) {
            LazyDataModel<?> lazyDataModel = (LazyDataModel<?>) table.getValue();
            List<?> wrappedData = lazyDataModel.getWrappedData();

            if (rowCount > 0) {
                table.setFirst(0);
                table.setRows(rowCount);
                table.loadLazyDataIfEnabled();
            }

            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                exportRow(context, table, rowIndex);
            }

            //restore
            table.setFirst(first);
            table.setRows(rows);
            table.setRowIndex(-1);
            lazyDataModel.setWrappedData(wrappedData);
            lazyDataModel.setPageSize(rows);
            lazyDataModel.setRowIndex(-1);
        }
        else {
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                exportRow(context, table, rowIndex);
            }

            //restore
            table.setFirst(first);
        }
    }

    protected void exportRow(FacesContext context, DataTable table, int rowIndex) {
        if (rowIndex != NO_ROW_INDEX_REQUIRED) {
            table.setRowIndex(rowIndex);
            if (!table.isRowAvailable()) {
                return;
            }
        }

        super.addCells(context, table);
    }

}
