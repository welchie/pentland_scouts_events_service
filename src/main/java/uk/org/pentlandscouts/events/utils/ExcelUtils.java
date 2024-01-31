package uk.org.pentlandscouts.events.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.pentlandscouts.events.controller.admin.PersonAdminController;
import uk.org.pentlandscouts.events.model.Person;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    private  static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Person> listPeople;

    public Map<Integer, List<String>> importFromExcel(String fileName) throws FileNotFoundException
    {
        try {
            //InputStream file = getFileFromResourceAsStream(fileName);
            FileInputStream file = new FileInputStream(new File(fileName));
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);

            Map<Integer, List<String>> data = new HashMap<>();
            int i = 0;  //Skip row 0 that is a header row
            for (Row row : sheet) {
                data.put(i, new ArrayList<String>());
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING: {
                            data.get(new Integer(i)).add(cell.getRichStringCellValue().getString().trim());
                            break;
                        }
                        case NUMERIC: {
                            if (DateUtil.isCellDateFormatted(cell)) {
                                data.get(i).add(cell.getDateCellValue() + "");
                            } else {
                                data.get(i).add(cell.getNumericCellValue() + "");
                            }
                            ;

                            break;
                        }
                        case BOOLEAN: {
                            data.get(i).add(cell.getBooleanCellValue() + "");
                            break;
                        }
                        case FORMULA: {
                            data.get(i).add(cell.getCellFormula() + "");
                            break;
                        }
                        default:
                            data.get(new Integer(i)).add(" ");
                    }
                }
                i++;
            }
            return data;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }
    private void writeHeaderLine() {
        logger.info("Writing Excel Header row");
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "First Name", style);
        createCell(row, 1, "Last Name", style);
        createCell(row, 2, "Age", style);
        createCell(row, 3, "Sub Camp", style);
        createCell(row, 4, "Group", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        logger.info("Writing out Person data to Execl Worksheet");
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Person person : listPeople) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            /**
             *  createCell(row, 0, "First Name", style);
             *         createCell(row, 1, "Last Name", style);
             *         createCell(row, 2, "Age", style);
             *         createCell(row, 3, "Sub Camp", style);
             *         createCell(row, 4, "Group", style);
             */
            createCell(row, columnCount++, person.getFirstName(), style);
            createCell(row, columnCount++, person.getLastName(), style);
            createCell(row, columnCount++, person.getDob(), style);
            createCell(row, columnCount++, person.getSubCamp(), style);
            createCell(row, columnCount++, person.getScoutGroup(), style);
            logger.info("Written: {}, {}, {},{},{}", person.getFirstName(), person.getLastName(),
                                                    person.getDob(),person.getSubCamp(),
                                                    person.getScoutGroup());

        }
    }

    public void export(HttpServletResponse response, List<Person> listPeople) throws IOException {
        this.listPeople = listPeople;
        workbook = new XSSFWorkbook();
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        logger.info("Closing Excel stream");
        outputStream.close();
        logger.info("Stream closed");

    }
}
