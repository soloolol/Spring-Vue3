package co.kr.mayfarm.seoulinstitutemanager.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import co.kr.mayfarm.seoulinstitutemanager.dto.CrimeDictDto;
import co.kr.mayfarm.seoulinstitutemanager.service.CrimeDictService;

import java.io.IOException;
import java.net.URLEncoder;


@RestController()
@RequestMapping("/crime-dictionary")
public class CrimeDictController {
    
    private final CrimeDictService crimeDictService;

    private final Logger logger = LoggerFactory.getLogger(CrimeDictController.class);

    public CrimeDictController(CrimeDictService crimeDictService) {
        this.crimeDictService = crimeDictService;
    }
    
    @GetMapping({"/", ""})
    public ResponseEntity<?> searchCrimeDicts(@ModelAttribute CrimeDictDto.SearchRequest request) {
        
        request.setOffset((request.getPage()-1)*10);
        CrimeDictDto.ListResponse searchCrimeDicts = crimeDictService.getCrimeDicts(request);

        if (searchCrimeDicts.isSuccess()) {
            return new ResponseEntity<CrimeDictDto.ListResponse>(searchCrimeDicts, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(searchCrimeDicts.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TODO: 기능 추가시 주석 해제
//    @PostMapping("/")
//    public ResponseEntity<String> addCrimeDict(@RequestBody CrimeDictDto.AddRequest request) {
//
//        CrimeDictDto.Response addResponse = crimeDictService.addCrimeDict(request);
//
//        if (addResponse.isSuccess()) {
//            return new ResponseEntity<>("Crime Dictionary Data Added.", HttpStatus.CREATED);
//        }
//        else {
//            return new ResponseEntity<>(addResponse.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PutMapping("")
    public ResponseEntity<?> updateCrimeDict(@RequestBody CrimeDictDto.AddRequest request) {
        CrimeDictDto.Response updateResponse = new CrimeDictDto.Response();
        try {
            updateResponse =
                    crimeDictService.updateCrimeDict(request.getCategory_code(), request.getTerm().replace(",","@@@@@"),
                            CrimeDictDto.UpdateRequest.builder().term(request.getTerm()).synonym(request.getSynonym())
                                    .category_code(request.getCategory_code()).build());
            return new ResponseEntity<>(updateResponse,HttpStatus.OK);
        }catch (Exception e) {
            logger.error("[updateCrimeDict] :", e );
            updateResponse.setSuccess(false);
            updateResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(updateResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/bulk")
    public ResponseEntity<?> updateCrimeDictBulk(@RequestBody CrimeDictDto.BulkRequest request) {
        CrimeDictDto.Response updateResponse = new CrimeDictDto.Response();
        try {
            for(CrimeDictDto.AddRequest addRequest : request.getRequest()) {
                updateResponse =
                        crimeDictService.updateCrimeDict(addRequest.getCategory_code(), addRequest.getTerm().replace(",","@@@@@"),
                                CrimeDictDto.UpdateRequest.builder().term(addRequest.getTerm()).synonym(addRequest.getSynonym())
                                        .category_code(addRequest.getCategory_code()).build());
            }
            return new ResponseEntity<>(updateResponse,HttpStatus.OK);
        }catch (Exception e) {
            logger.error("[updateCrimeDict] :", e );
            updateResponse.setSuccess(false);
            updateResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(updateResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping("/excel")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        try {
            XSSFWorkbook workbook = crimeDictService.downloadExcel(response);
            response.reset();

            response.addHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode("crime-dictionary", "UTF-8") + ".xlsx\"");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
            response.addHeader("Content-Transfer-Encoding", "binary");
            response.setContentType("application/vnd.ms-excel");

            workbook.write(response.getOutputStream());
            workbook.close();
            response.getOutputStream().flush();
        }catch (Exception e) {
            logger.error("[updateCrimeDict] :", e );
            response.sendError(500, e.getMessage());
        }
    }

    @PostMapping("/excel")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile excelFile) {
        CrimeDictDto.Response uploadResponse = new CrimeDictDto.Response();
        try{
            uploadResponse = crimeDictService.uploadExcel(excelFile);
            return new ResponseEntity<>(uploadResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error("[updateCrimeDict] :", e );
            uploadResponse.setSuccess(false);
            uploadResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(uploadResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{category_code}/{term}")
    public ResponseEntity<?> deleteCrimeDict(@PathVariable("category_code") int categoryCode, @PathVariable("term") String term) {
        CrimeDictDto.Response deleteResponse = new CrimeDictDto.Response();

        try {
            deleteResponse = crimeDictService.deleteCrimeDict(categoryCode, term);
            return new ResponseEntity<>(deleteResponse, HttpStatus.OK);
        }catch (Exception e) {
            logger.error("[updateCrimeDict] :", e );
            deleteResponse.setSuccess(false);
            deleteResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(deleteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<?> deleteCrimeDict(@RequestBody CrimeDictDto.BulkRequest request) {
        CrimeDictDto.Response deleteResponse = new CrimeDictDto.Response();

        try {
            for(CrimeDictDto.AddRequest addRequest : request.getRequest()) {
                deleteResponse = crimeDictService.deleteCrimeDict(addRequest.getCategory_code(), addRequest.getTerm());
            }
            return new ResponseEntity<>(deleteResponse, HttpStatus.OK);
        }catch (Exception e) {
            logger.error("[updateCrimeDict] :", e );
            deleteResponse.setSuccess(false);
            deleteResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(deleteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/category_big")
    public ResponseEntity<?> getCategoryBigList() {
        CrimeDictDto.ListResponse categoryBigList = new CrimeDictDto.ListResponse();
        try{
            categoryBigList = crimeDictService.getCategoryBigList();
            return new ResponseEntity<>(categoryBigList, HttpStatus.OK);
        }catch (Exception e) {
            logger.error("[updateCrimeDict] :", e );
            categoryBigList.setSuccess(false);
            categoryBigList.setMessage(e.getMessage());
            return new ResponseEntity<>(categoryBigList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/category_middle")
    public ResponseEntity<?> getCategoryMiddleList() {
        CrimeDictDto.ListResponse categoryMiddleList = new CrimeDictDto.ListResponse();
        try{
            categoryMiddleList = crimeDictService.getCategoryMiddleList();
            return new ResponseEntity<>(categoryMiddleList, HttpStatus.OK);
        }catch (Exception e) {
            logger.error("[getCategoryMiddleList] :", e );
            categoryMiddleList.setSuccess(false);
            categoryMiddleList.setMessage(e.getMessage());
            return new ResponseEntity<>(categoryMiddleList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/category_middle_code")
    public ResponseEntity<?> getCategoryMiddleByCode() {
        CrimeDictDto.ListResponse categoryMiddleByList = new CrimeDictDto.ListResponse();
        try{
            categoryMiddleByList = crimeDictService.getCategoryMiddleByCode();
            return new ResponseEntity<>(categoryMiddleByList, HttpStatus.OK);
        }catch (Exception e) {
            logger.error("[getCategoryMiddleByCode] :", e );
            categoryMiddleByList.setSuccess(false);
            categoryMiddleByList.setMessage(e.getMessage());
            return new ResponseEntity<>(categoryMiddleByList, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
