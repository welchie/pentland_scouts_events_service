package uk.org.pentlandscouts.events.model.security;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Stage2IntegrationTest {

    @Test
    public void testApachePoiExcelReadWrite() throws Exception {
        // Create an in-memory Excel file using XSSFWorkbook (Apache POI 5.5.1)
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("TestSheet");
            Row headerRow = sheet.createRow(0);
            Cell cell = headerRow.createCell(0);
            cell.setCellValue("Scouts Event");

            // Write to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();

            // Read the workbook back from the byte array
            try (Workbook readWorkbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
                Sheet readSheet = readWorkbook.getSheet("TestSheet");
                assertNotNull(readSheet);
                Row readRow = readSheet.getRow(0);
                assertNotNull(readRow);
                Cell readCell = readRow.getCell(0);
                assertEquals("Scouts Event", readCell.getStringCellValue());
            }
        }
    }

    @Test
    public void testH2DatabaseConnection() throws Exception {
        // Verify database connection and schema execution using updated H2 (2.4.240)
        String jdbcUrl = "jdbc:h2:mem:stage2test;DB_CLOSE_DELAY=-1";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "")) {
            assertNotNull(conn);

            try (Statement stmt = conn.createStatement()) {
                // Create a test table
                stmt.execute("CREATE TABLE test_event (id INT PRIMARY KEY, name VARCHAR(255))");

                // Insert a test row
                stmt.execute("INSERT INTO test_event VALUES (1, 'Pentland Camp')");

                // Query and assert
                try (ResultSet rs = stmt.executeQuery("SELECT name FROM test_event WHERE id = 1")) {
                    assertTrue(rs.next());
                    assertEquals("Pentland Camp", rs.getString("name"));
                }
            }
        }
    }
}
