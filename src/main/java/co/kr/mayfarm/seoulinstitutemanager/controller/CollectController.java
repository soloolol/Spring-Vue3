package co.kr.mayfarm.seoulinstitutemanager.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import co.kr.mayfarm.seoulinstitutemanager.dto.CollectDto;
import co.kr.mayfarm.seoulinstitutemanager.service.CollectService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URLEncoder;


@RestController()
@RequestMapping("/collections")
public class CollectController {
    private final CollectService collectService;

    private final Logger logger = LoggerFactory.getLogger(CollectController.class);

    public CollectController(CollectService collectService) {
        this.collectService = collectService;
    }

    @ResponseBody
    @GetMapping(value={"/", ""})
    public ResponseEntity<?> getCollections(@ModelAttribute CollectDto.SearchRequest request) {
        if(request.getStatus().equals("전체")) request.setStatus("");
        CollectDto.ListResponse response = new CollectDto.ListResponse();
//        request.setOffset((request.getPage()-1)*10);
        try {
            response = collectService.getCollections(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[getCollections] :", e );
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCollectionDetail(@PathVariable int id) {

        CollectDto.DetailResponse collectionDetail = collectService.getCollectionDetail(id);

        if (collectionDetail.isSuccess()) {
            return new ResponseEntity<>(collectionDetail, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<String>(collectionDetail.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCollection(@PathVariable int id) {

        CollectDto.Response deleteResponse = collectService.deleteCollection(id);

        if (deleteResponse.isSuccess()) {
            return new ResponseEntity<>("Collect Data Deleted.", HttpStatus.OK);    
        }
        else {
            return new ResponseEntity<>(deleteResponse.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/image/{image_id}")
    public ResponseEntity<?> getCollectionImages(@PathVariable int id, @PathVariable String image_id) {

        CollectDto.ImageResponse response = collectService.getCollectionImages(id, image_id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);    
        }
        else {
            return new ResponseEntity<>(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value={"/sourceList"})
    public ResponseEntity<?> getSourceBoardList() {
        CollectDto.ListResponse response = new CollectDto.ListResponse();
        try {
            response = collectService.getSourceBoardList();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[getSourceBoardList] :", e );
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value={"/excel"})
    public void downloadExcel(HttpServletResponse response, @ModelAttribute CollectDto.SearchRequest request) throws IOException {
        if(request.getStatus().equals("전체")) request.setStatus("");
        try {
            request.setLimit(false);
            XSSFWorkbook workbook = collectService.downloadExcel(request);
            response.reset();

            response.addHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode("collections", "UTF-8") + ".xlsx\"");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
            response.addHeader("Content-Transfer-Encoding", "binary");
            response.setContentType("application/vnd.ms-excel");

            workbook.write(response.getOutputStream());
            workbook.close();
            response.getOutputStream().flush();
        }catch (Exception e) {
            logger.error("[downloadExcel] :", e );
            response.sendError(500, e.getMessage());
        }
    }
}
