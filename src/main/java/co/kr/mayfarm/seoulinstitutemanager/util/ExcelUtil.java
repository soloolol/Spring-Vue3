package co.kr.mayfarm.seoulinstitutemanager.util;

import co.kr.mayfarm.seoulinstitutemanager.dto.CrimeDictDto;
import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Map;

public class ExcelUtil {

    private static final int MAX_CELL_LENGTH = 32767;

    public static int excelHeader(Sheet sheet, int rowNum, List<String> value, CellStyle cellStyle) {
        Row row;
        Cell cell;
        row = sheet.createRow(rowNum);
        row.setHeight((short) 450);
        int i = 0;
        for(String v : value) {
            if (v.length() > MAX_CELL_LENGTH) {
                v = v.substring(0, MAX_CELL_LENGTH);
            }
            cell = row.createCell(i);
            cell.setCellValue(v);
            cell.setCellStyle(cellStyle);
            i ++;
        }
        return rowNum;
    }

    public static int excelContent(Sheet sheet, int rowNum, List<Map<String, Object>> value, List<String> fields, CellStyle style) {
        Row row;
        Cell cell;

        if(null != value) {
            for(Map<String, Object> v : value) {
                rowNum += 1;
                if(sheet.getRow(rowNum) != null) {
                    row = sheet.getRow(rowNum);
                }else {
                    row = sheet.createRow(rowNum);
                }

                for(int i = 0; i< fields.size(); i++) {
                    cell= row.createCell(i);
                    Object vv = v.get(fields.get(i));
                    if(vv != null) {
                        if(vv.getClass().getSimpleName().equals("Integer")) {
                            cell.setCellValue((Integer)v.get(fields.get(i)));
                        }else {
                            if (vv.toString().length() > MAX_CELL_LENGTH) {
                                vv = vv.toString().substring(0, MAX_CELL_LENGTH);
                            }
                            cell.setCellValue(vv.toString());
                        }
                    }else {
                        cell.setCellValue("");
                    }
                    if(style != null) {
                        cell.setCellStyle(style);
                    }
                }
            }
        }
        return rowNum;
    }
}
