package pl.edu.agh.mwo.invigilator.parser;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import pl.edu.agh.mwo.invigilator.report.Report;
import pl.edu.agh.mwo.invigilator.report.ReportEmployeeProjectHoursSimplest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimplestExcelParser implements ExcelParser {
    private final static int ROW_ID_DATE = 0;
    private final static int ROW_ID_TASK = 1;
    private final static int ROW_ID_HOURS = 2;
    private List<File> files;

    public SimplestExcelParser(List<File> files) {
        this.files = files;
    }

    @Override
    public String getListOfFiles() {
        StringBuilder result = new StringBuilder();
        for (File file : files) {
            result.append(file.getAbsoluteFile());
            result.append(";");
        }
        return result.toString();
    }

    @Override
    public List<Report> getReportsEmployeeProjectHours() {
        List<Report> reports = new ArrayList<>();

        for (File file : files) {
            Report report = new ReportEmployeeProjectHoursSimplest();
            String userName = file.getName();
            report.addEmployeeName(userName);

            Workbook workbook;
            try(FileInputStream fileInputStream = new FileInputStream(file)) {
                workbook = new HSSFWorkbook(fileInputStream);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            double sumOfHours = 0;
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                String projectName = sheet.getSheetName();

                int rowId = 0;
                boolean shouldRead = true;

                while (shouldRead) {
                    Row row = sheet.getRow(rowId);
                    if (row == null) {
                        break;
                    }
                    Cell cell = row.getCell(ROW_ID_HOURS);

                    CellType cellType = cell.getCellType();
                    if (cellType.equals(CellType.NUMERIC)) {
                        double value = cell.getNumericCellValue();
                        sumOfHours += value;
                    }
                    rowId++;
                }
            }

            report.setTotalHours(sumOfHours);
            reports.add(report);
        }

        return reports;
    }

    private boolean isHoursValue(String value) {
        try {
            double v = Double.parseDouble(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
