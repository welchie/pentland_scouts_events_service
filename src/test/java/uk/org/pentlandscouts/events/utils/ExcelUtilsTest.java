package uk.org.pentlandscouts.events.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import uk.org.pentlandscouts.events.model.Person;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExcelUtilsTest {

    @Test
    public void testImportFromExcelNullFile() {
        ExcelUtils excelUtils = new ExcelUtils();
        assertThrows(IllegalArgumentException.class, () -> {
            excelUtils.importFromExcel((String) null);
        });
    }

    @Test
    public void testImportFromExcelPathTraversal() {
        ExcelUtils excelUtils = new ExcelUtils();
        assertThrows(IllegalArgumentException.class, () -> {
            excelUtils.importFromExcel("../traversal.xlsx");
        });
    }

    @Test
    public void testImportFromExcelNullInputStream() {
        ExcelUtils excelUtils = new ExcelUtils();
        assertThrows(IllegalArgumentException.class, () -> {
            excelUtils.importFromExcel((InputStream) null);
        });
    }

    @Test
    public void testImportFromExcelValidInputStream() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Test");
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("Header");
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("John");
        row1.createCell(1).setCellValue(25.5);
        row1.createCell(2).setCellValue(true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

        ExcelUtils excelUtils = new ExcelUtils();
        Map<Integer, List<String>> result = excelUtils.importFromExcel(bis);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(1).get(0));
    }

    @Test
    public void testExport() throws IOException {
        ExcelUtils excelUtils = new ExcelUtils();
        HttpServletResponse response = mock(HttpServletResponse.class);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ServletOutputStream sos = new ServletOutputStream() {
            @Override
            public boolean isReady() { return true; }
            @Override
            public void setWriteListener(WriteListener writeListener) {}
            @Override
            public void write(int b) throws IOException { bos.write(b); }
        };

        when(response.getOutputStream()).thenReturn(sos);

        Person person = new Person("John", "Doe", "dob", "key");
        person.setSubCamp("Camp");
        person.setScoutGroup("Group");

        excelUtils.export(response, Collections.singletonList(person));

        byte[] exportedBytes = bos.toByteArray();
        assertTrue(exportedBytes.length > 0);

        Workbook checkWorkbook = new XSSFWorkbook(new ByteArrayInputStream(exportedBytes));
        Sheet sheet = checkWorkbook.getSheetAt(0);
        assertNotNull(sheet);
        assertEquals("John", sheet.getRow(1).getCell(0).getStringCellValue());
        checkWorkbook.close();
    }

    @Test
    public void testImportFromExcelInputStreamIOException() throws IOException {
        InputStream badStream = mock(InputStream.class);
        doThrow(new IOException("simulated")).when(badStream).read(any(byte[].class), anyInt(), anyInt());
        doThrow(new IOException("simulated")).when(badStream).read(any(byte[].class));
        doThrow(new IOException("simulated")).when(badStream).read();

        ExcelUtils excelUtils = new ExcelUtils();
        assertThrows(RuntimeException.class, () -> {
            excelUtils.importFromExcel(badStream);
        });
    }

    @Test
    public void testImportFromExcelFileNotFound() {
        ExcelUtils excelUtils = new ExcelUtils();
        assertThrows(RuntimeException.class, () -> {
            excelUtils.importFromExcel("non_existent_file.xlsx");
        });
    }
}
