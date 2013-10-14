package com.search.reports.manager.util;

import com.search.reports.manager.exception.ReportsException;
import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 14, 2013
 * @version 1.0
 */
public class CellsTest {

    private XSSFCell cell0;

    @Before
    public void setup() throws FileNotFoundException, IOException {
        FileInputStream in = new FileInputStream(
                FileUtils.getFile("src/test/resources/test1.xlsx").getAbsolutePath());
        XSSFWorkbook workbook = new XSSFWorkbook(in);
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        XSSFRow row = sheet0.getRow(0);
        cell0 = row.getCell(0);

        in.close();
    }

    @Test
    public void testGetEitherNumericOrStringValueAsString() {
        assertEquals("100.0", Cells.getEitherNumericOrStringValueAsString(cell0));
    }
}
