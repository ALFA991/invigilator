package pl.edu.agh.mwo.itracker.model.parser;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import pl.edu.agh.mwo.itracker.model.report.Report;
import pl.edu.agh.mwo.itracker.model.report.ReportEmployeeProjectHoursSimplest;
import pl.edu.agh.mwo.itracker.model.report.ReportProjectHoursSimplest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class SimplestExcelParserForReport2 implements ExcelParser {
    private final static int ROW_ID_DATE = 0;
    private final static int ROW_ID_TASK = 1;
    private final static int ROW_ID_HOURS = 2;
    private List<File> files;

    public SimplestExcelParserForReport2(List<File> files) {
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
    public Report getReportOption1EmployeeProjectHours() {
        //TODO: rename this method to getReport
        Report report = new ReportProjectHoursSimplest("");

        for (File file : files) {
            String employeeName = file.getName();
            employeeName = employeeName.replaceAll(".xls", "");
            employeeName = employeeName.replaceAll("_", " ");
            report.setEmployee(employeeName);

            Workbook workbook;
            try(FileInputStream fileInputStream = new FileInputStream(file)) {
                workbook = new HSSFWorkbook(fileInputStream);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            while (sheetIterator.hasNext()) {
                double sumOfHours = 0;
                Sheet sheet = sheetIterator.next();
                String projectName = sheet.getSheetName();

                int rowId = 0;

                while (true) {
                    Row row = sheet.getRow(rowId);
                    if (row == null) {
                        break;
                    }
                    Cell cell = row.getCell(ROW_ID_HOURS);
                    if (cell == null) {
                        break;
                    }
                    CellType cellType = cell.getCellType();
                    if (cellType.equals(CellType.NUMERIC)) {
                        double value = cell.getNumericCellValue();
                        sumOfHours += value;
                    }
                    rowId++;
                }
                report.setProject(employeeName, projectName, sumOfHours);
            }
        }
        return report;
    }
}
