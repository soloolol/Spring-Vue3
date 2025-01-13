package co.kr.mayfarm.seoulinstitutemanager.service;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import co.kr.mayfarm.seoulinstitutemanager.dto.CollectDto;

@Service
public interface CollectService {
    
    CollectDto.ListResponse getCollections(CollectDto.SearchRequest request);
    // public CollectDto.Response addCollection(CollectDto.AddRequest request);
    CollectDto.DetailResponse getCollectionDetail(int id);
    CollectDto.Response deleteCollection(int id);
    CollectDto.ImageResponse getCollectionImages(int id, String image_id);
    CollectDto.ListResponse getSourceBoardList();
    XSSFWorkbook downloadExcel(CollectDto.SearchRequest request);
}
