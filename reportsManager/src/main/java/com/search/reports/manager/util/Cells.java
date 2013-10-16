package com.search.reports.manager.util;

import com.search.reports.manager.exception.ReportsException;
import org.apache.poi.ss.usermodel.Cell;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 11, 2013
 * @version 1.0
 */
public class Cells {

    public static String getEitherNumericOrStringValueAsString(Cell cell)
            throws ReportsException {
        int cellType = cell.getCellType();

        if (cellType == Cell.CELL_TYPE_NUMERIC) {
            return cell.getNumericCellValue() + "";
        } else if (cellType == Cell.CELL_TYPE_STRING) {
            return cell.getStringCellValue();
        }

        throw new ReportsException("Cell object not numeric nor a string");
    }
}
