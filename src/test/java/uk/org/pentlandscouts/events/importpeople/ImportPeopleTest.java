package uk.org.pentlandscouts.events.importpeople;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.pentlandscouts.events.controller.VersionController;
import uk.org.pentlandscouts.events.model.Person;
import uk.org.pentlandscouts.events.utils.ExcelUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImportPeopleTest {

    static final String XLS_FILE = "people_import.xlsx";

    private static final Logger logger = LoggerFactory.getLogger(VersionController.class);

    private ExcelUtils excelUtils = new ExcelUtils();
    @Test
    public void testGetExcelData() throws FileNotFoundException {
        Map<Integer, List<String>> data = excelUtils.importFromExcel(XLS_FILE);
        assertFalse(data.isEmpty());
        for(int i =0 ; i <data.size();i++)
        {
            logger.info(String.valueOf(data.get(i)));
        }
    }

    @Test
    public void testConvertExcelDataToObjects() throws FileNotFoundException {
        Map<Integer, List<String>> data = excelUtils.importFromExcel(XLS_FILE);
        assertFalse(data.isEmpty());

        List<Person> personList = new ArrayList<Person>();
        for(int i =1 ; i <data.size();i++)
        {
            List<String> row = data.get(i);
            /**
             * Data structure
             *
             * row[0] = firstName
             * row[1] = lastName
             * row[2] = Section
             * row[3] = Age At Start
             * row[4] = Contact Email
             * row[5] = Photo Permission
             * row[6] = Allergies
             * row[7] = Medical Requirements
             * row[8] = Dietary Requirements
             */

            Person p = new Person();
            if (!row.isEmpty()) {
                if (row.get(0) != " ") {
                    p.setFirstName(row.get(0));
                    p.setLastName(row.get(1));
                    p.setScoutSection(row.get(2));
                    p.setDob(row.get(3));
                    p.setContactEmail(row.get(4));
                    p.setAllergies(row.get(6));
                    p.setMedicine(row.get(7));
                    p.setDietary(row.get(8));

                    personList.add(p);
                }
            }



           // logger.info(String.valueOf(data.get(i)));
        }
        for(Person p:personList)
        {
            logger.info("{}",p);
        }
    }
}
