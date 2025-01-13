package co.kr.mayfarm.seoulinstitutemanager.service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import co.kr.mayfarm.seoulinstitutemanager.util.ExcelUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.kr.mayfarm.seoulinstitutemanager.dto.CollectDto;
import co.kr.mayfarm.seoulinstitutemanager.repository.CollectRepository;

@Service
public class CollectServiceImpl implements CollectService {

    @Autowired
    CollectRepository collectRepository;

    @Value("${database.name}")
    private String DATABASE_NAME;
    
    @Override
    public CollectDto.ListResponse getCollections(CollectDto.SearchRequest request) {
        List<CollectDto.SearchResponse> collectionList = collectRepository.searchCollection(request);
        int collectionListCount = collectRepository.getSearchCollectionCount(request);
        CollectDto.ListResponse response = new CollectDto.ListResponse();
            response.setData(collectionList);
            response.setTotal_count(collectionListCount);
            response.setSuccess(true);

        return response;
    }

    @Override
    public CollectDto.DetailResponse getCollectionDetail(int id) {
        CollectDto.DetailResponse response;
        CollectDto.CollectionDetailVO collectionDetail = collectRepository.getCollectionDetail(id);

        if(collectionDetail != null) {
            response = new CollectDto.DetailResponse(collectionDetail);
            response.setSuccess(true);
        }
        else {
            response = new CollectDto.DetailResponse();
            response.setSuccess(false);
            response.setMessage("해당 데이터가 존재하지 않습니다.");
        }
        return response;
    }

    @Override
    public CollectDto.Response deleteCollection(int id) {
        CollectDto.Response response = new CollectDto.Response();
        response.setResult_id(id);
        try {
            collectRepository.deleteCollection(id);
            response.setSuccess(true);
        }
        catch(Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public CollectDto.ImageResponse getCollectionImages(int id, String image_id) {
        CollectDto.ImageResponseVO responseVO = collectRepository.getCollectionImageData(id);
        String[] image_ids = responseVO.getImage_file_id().split(",,,");

        CollectDto.ImageResponse response = null;
        for(int i=0; i < image_ids.length ; i++) {
            if(image_ids[i].equals(image_id)) {
                try {
                    String ocrImage = responseVO.getImage_file_name().split(",,,")[i];
                    String ocrText = responseVO.getOcr_result().split("@@@@@")[i];
                    String ocrBoundingBox = responseVO.getOcr_boundingbox_position().split("@@@@@")[i];

                    List<String> ocrTextList = !ocrText.equals("") ? new ArrayList<String>(Arrays.asList(ocrText.split(" "))) : new ArrayList<>();
                    List<Map<String, Float>> ocrBoundingboxPositionList = !ocrBoundingBox.equals("") ? parseOcrBoundingBoxString(ocrBoundingBox) : new ArrayList<>();

                    response = new CollectDto.ImageResponse(ocrImage, ocrTextList, ocrBoundingboxPositionList);
                    response.setSuccess(true);
                } catch(Exception e) {
                    response = new CollectDto.ImageResponse();
                    response.setSuccess(false);
                    response.setMessage("Database data is wrong. Check Database data.\n Internal Message: "+e.getMessage());
                }
            }
        }
        if(response == null) {
            response = new CollectDto.ImageResponse();
            response.setSuccess(false);
            response.setMessage("Data Not Exists");
        }

        return response;
    }

    @Override
    public CollectDto.ListResponse getSourceBoardList() {
        CollectDto.ListResponse response = new CollectDto.ListResponse();
        response.setData(List.of(collectRepository.getSourceBoardList().split(",")));
        return response;
    }

    @Override
    public XSSFWorkbook downloadExcel(CollectDto.SearchRequest request) {

        List<String> fields = collectRepository.getFields(DATABASE_NAME);
        List<Map<String,Object>> collectionList = collectRepository.downloadCollections(request);

        XSSFWorkbook workbook;
        CellStyle headerStyle;
        CellStyle contentStyle;

        workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("범죄용어관리");

        int rowNum =0;

        headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.LEFT);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) (12*20));
        headerStyle.setFont(font);

        contentStyle = workbook.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.LEFT);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        rowNum = ExcelUtil.excelHeader(sheet, rowNum, fields, headerStyle);
        rowNum = ExcelUtil.excelContent(sheet, rowNum, collectionList, fields, contentStyle);

        return workbook;
    }

    private List<Map<String, Float>> parseOcrBoundingBoxString(String boundingBoxString) {
        List<Map<String, Float>> boundingBoxList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray boundingBox3DJsonArray = (JSONArray) parser.parse(boundingBoxString);
            for(Object boundingBox3DJsonObject : boundingBox3DJsonArray) {
                JSONArray boundingBox2DJsonArray = (JSONArray) boundingBox3DJsonObject;
                Map<String, Float> boundingBox = new HashMap<>();
                for(int i=0; i < boundingBox2DJsonArray.size(); i++) {
                    boundingBox.put("x"+(i+1), Float.parseFloat(((JSONArray) boundingBox2DJsonArray.get(i)).get(0).toString()));
                    boundingBox.put("y"+(i+1), Float.parseFloat(((JSONArray) boundingBox2DJsonArray.get(i)).get(1).toString()));
                }
                boundingBoxList.add(boundingBox);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return boundingBoxList;
    }
    
}
