package uk.org.pentlandscouts.events.utils;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelUtilsTest {

    private final ExcelUtils excelUtils = new ExcelUtils();

    @Test
    public void testImportFromExcelNullFileName() {
        assertThrows(IllegalArgumentException.class, () -> {
            excelUtils.importFromExcel((String) null);
        });
    }

    @Test
    public void testImportFromExcelPathTraversal() {
        assertThrows(IllegalArgumentException.class, () -> {
            excelUtils.importFromExcel("../etc/passwd");
        });
    }

    @Test
    public void testImportFromExcelNullInputStream() {
        assertThrows(IllegalArgumentException.class, () -> {
            excelUtils.importFromExcel((InputStream) null);
        });
    }

    @Test
    public void testImportFromExcelInputStream() throws Exception {
        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet();
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("First Name");
            header.createCell(1).setCellValue("Last Name");
            header.createCell(2).setCellValue("Section");
            header.createCell(3).setCellValue("SubCamp");
            header.createCell(4).setCellValue("Age");

            org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("John");
            dataRow.createCell(1).setCellValue("Doe");
            dataRow.createCell(2).setCellValue("Scouts");
            dataRow.createCell(3).setCellValue("Camp A");
            dataRow.createCell(4).setCellValue(12.5);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray())) {
                Map<Integer, List<String>> result = excelUtils.importFromExcel(inputStream);

                assertNotNull(result);
                assertEquals(2, result.size());
                assertEquals("John", result.get(1).get(0));
                assertEquals("Doe", result.get(1).get(1));
                assertEquals("Scouts", result.get(1).get(2));
                assertEquals("Camp A", result.get(1).get(3));
                // numeric cells are converted to double string representation
                assertEquals("12.5", result.get(1).get(4));
            }
        }
    }
}
