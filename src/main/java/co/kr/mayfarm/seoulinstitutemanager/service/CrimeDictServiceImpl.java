package co.kr.mayfarm.seoulinstitutemanager.service;

import java.util.*;
import java.util.stream.Collectors;

import co.kr.mayfarm.seoulinstitutemanager.util.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import co.kr.mayfarm.seoulinstitutemanager.dto.CrimeDictDto;
import co.kr.mayfarm.seoulinstitutemanager.repository.CrimeDictRepository;

import static java.util.Map.Entry.comparingByValue;

@Service
public class CrimeDictServiceImpl implements CrimeDictService {

    @Autowired
    CrimeDictRepository crimeDictRepository;

    @Override
    public CrimeDictDto.ListResponse getCrimeDicts(CrimeDictDto.SearchRequest request) {
        List<CrimeDictDto.SearchResponseVO> crimeDictVOList = crimeDictRepository.searchCrimeDict(request);
        int crimeDictListCount = crimeDictRepository.searchCrimeDictCount(request);
        CrimeDictDto.ListResponse response = new CrimeDictDto.ListResponse();
        if(crimeDictVOList.size() != 0) {
            List<CrimeDictDto.SearchResponse> crimeDictList = new ArrayList<CrimeDictDto.SearchResponse>();
            for (CrimeDictDto.SearchResponseVO crimeDictVO : crimeDictVOList) {
                crimeDictList.add(new CrimeDictDto.SearchResponse(crimeDictVO));
            }
            response.setData(crimeDictList);
            response.setSuccess(true);
            response.setTotal_count(crimeDictListCount);
        } else {
            response.setSuccess(false);
            response.setMessage("조회된 데이터가 없습니다.");
        }
        return response;
    }

    @Override
    public CrimeDictDto.Response addCrimeDict(CrimeDictDto.AddRequest request) {
        CrimeDictDto.Response response = new CrimeDictDto.Response();
        try {
            crimeDictRepository.insertCrimeDict(request);
            response.setSuccess(true);
        }
        catch(Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public XSSFWorkbook downloadExcel(HttpServletResponse response) {
        List<CrimeDictDto.SearchResponseVO> crimeDictDtoList = crimeDictRepository.getAllCrimeDict();

        XSSFWorkbook workbook;
        CellStyle headerStyle;
        CellStyle contentStyle;

        List<Map<String, Object>> contentList = crimeDictDtoList.stream()
                .map(CrimeDictDto.SearchResponseVO::toMap)
                .collect(Collectors.toList());

        workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("범죄용어관리");

        int rowNum =0;

        List<String> titles = new ArrayList<>();
        List<String> fields = new ArrayList<>();

        headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.LEFT);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderTop(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) (12*20));
        headerStyle.setFont(font);

        contentStyle = workbook.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.LEFT);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        titles = Arrays.asList("카테고리 코드","카테고리 대분류","카테고리 중분류","범죄용어","동의어");
        fields = Arrays.asList("category_code","category_big","category_middle","term","synonym");
        rowNum = ExcelUtil.excelHeader(sheet,rowNum,titles,headerStyle);
        rowNum = ExcelUtil.excelContent(sheet,rowNum,contentList, fields,contentStyle);

        return workbook;
    }

    @Override
    public CrimeDictDto.Response uploadExcel(MultipartFile file) {
        CrimeDictDto.Response response = new CrimeDictDto.Response();
        List<CrimeDictDto.AddRequest> addRequestList = new ArrayList<>();
        Workbook workbook;
        Sheet sheet;
        try {
            workbook = WorkbookFactory.create(file.getInputStream());
            sheet = workbook.getSheetAt(0);

            Map<String,Integer> columnMap = getColumnMap(sheet.getRow(0));
            for(Row row : sheet) {
                if(row.getRowNum() ==0) continue;

                CrimeDictDto.AddRequest addRequest = new CrimeDictDto.AddRequest();
                String categoryCode = getValueFromRowMap(row,columnMap,"카테고리 코드").get(0);
                String categoryBig = getValueFromRowMap(row,columnMap,"카테고리 대분류").get(0);
                String categoryMiddle = getValueFromRowMap(row,columnMap,"카테고리 중분류").get(0);
                String term = getValueFromRowMap(row,columnMap,"범죄용어").get(0);
                String synonym = "";
                List<String> synonymList = getValueFromRowMap(row,columnMap,"동의어");
                if(!ObjectUtils.isEmpty(synonymList)) {
                    synonym = synonymList.get(0).replace(",","@@@@@").replace("[","")
                            .replace("]","");
                }

                Double categoryCodeDouble = Double.parseDouble(categoryCode);
                addRequest.setCategory_code((int) Math.floor(categoryCodeDouble));
                addRequest.setTerm(term);
                addRequest.setSynonym(synonym);
                addRequest.setCategory_big(categoryBig);
                addRequest.setCategory_middle(categoryMiddle);
                addRequestList.add(addRequest);
            }

            //TODO: 전부 삭제후 다시 저장하는것으로 변경 가능성있음
            for(CrimeDictDto.AddRequest request : addRequestList) {
                if(0<crimeDictRepository.countByCategoryCodeAndTerm(request.getCategory_code(),request.getTerm())) {
                    updateCrimeDict(request.getCategory_code(), request.getTerm().replace(",","@@@@@"),
                            CrimeDictDto.UpdateRequest.builder().term(request.getTerm()).synonym(request.getSynonym())
                                    .category_code(request.getCategory_code()).build());
                }else {
                    crimeDictRepository.insertCrimeDict(request);
                }
            }

            response.setSuccess(true);
        }
        catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    private Map<String, Integer> getColumnMap(Row row) {
        Map<String, Integer> rowNumMap = new HashMap<>();

        int cellCount = row.getPhysicalNumberOfCells();

        for ( int i = 0; i < cellCount; i++ ) {
            String fieldName = ((String) getValueFromCell(row.getCell(i))).toLowerCase();
            rowNumMap.put(fieldName, i);
        }

        Map<String, Integer> sortedMap = rowNumMap.entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect(
                        Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                                LinkedHashMap::new));

        return sortedMap;
    }

    private Object getValueFromCell(Cell cell) {
        if ( ObjectUtils.isEmpty(cell) ) return "";

        switch ( cell.getCellType() ) {
            case 1:
                return cell.getStringCellValue().trim();
            case 4:
                return cell.getBooleanCellValue();
            case 0:
                if ( DateUtil.isCellDateFormatted(cell) ) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case 2:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private List<String> getValueFromRowMap(Row row, Map<String, Integer> columnMap, String fieldName){
        List<String> list = new ArrayList<>();

        for ( Map.Entry<String, Integer> entry : columnMap.entrySet() ) {
            if ( entry.getKey().toLowerCase().startsWith(fieldName) ) {
                if ( ObjectUtils.isEmpty(getValueFromCell(row.getCell(entry.getValue()))) ) continue;

                String cellValue = getValueFromCell(row.getCell(entry.getValue())).toString();
                if ( !ObjectUtils.isEmpty(cellValue) ) list.add(cellValue.trim());
            }
        }

        return list;
    }
    @Override
    public CrimeDictDto.Response updateCrimeDict(int categoryCode, String term, CrimeDictDto.UpdateRequest request) {
        request.setOld_term(term);
        request.setOld_category_code(categoryCode);
        CrimeDictDto.Response response = new CrimeDictDto.Response();
        try {
            crimeDictRepository.updateCrimeDict(request);
            response.setSuccess(true);
        }
        catch(Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }
    
    @Override
    public CrimeDictDto.Response deleteCrimeDict(int categoryCode, String term) {
        CrimeDictDto.Response response = new CrimeDictDto.Response();
        try {
            crimeDictRepository.deleteCrimeDict(categoryCode, term);
            response.setSuccess(true);
        }
        catch(Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public CrimeDictDto.ListResponse getCategoryBigList() {
        CrimeDictDto.ListResponse response = new CrimeDictDto.ListResponse();
        List<String> data = crimeDictRepository.getCategoryBigList();
        response.setData(data);
        if(data.size() != 0) {
            response.setSuccess(true);
        } else {
            response.setSuccess(false);
            response.setMessage("조회된 데이터가 없습니다.");
        }
        return response;
    }

    @Override
    public CrimeDictDto.ListResponse getCategoryMiddleList() {
        CrimeDictDto.ListResponse response = new CrimeDictDto.ListResponse();
        List<CrimeDictDto.CategoryMiddle> data = crimeDictRepository.getCategoryMiddleList();
        response.setData(data);
        if(data.size() != 0) {
            response.setSuccess(true);
        } else {
            response.setSuccess(false);
            response.setMessage("조회된 데이터가 없습니다.");
        }
        return response;
    }

    @Override
    public CrimeDictDto.ListResponse getCategoryMiddleByCode() {
        CrimeDictDto.ListResponse response = new CrimeDictDto.ListResponse();
        List<Map<Integer,String>> result = new ArrayList<>();
        Map<Integer,String> map = new HashMap<>();
        List<CrimeDictDto.CategoryMiddle> data = crimeDictRepository.getCategoryMiddleByCode();
        for(CrimeDictDto.CategoryMiddle cate : data){
            map.put(cate.getCategory_code(), cate.getCategory_middle());
        }
        result.add(map);
        response.setData(result);
        if(data.size() != 0) {
            response.setSuccess(true);
        } else {
            response.setSuccess(false);
            response.setMessage("조회된 데이터가 없습니다.");
        }
        return response;
    }
}
