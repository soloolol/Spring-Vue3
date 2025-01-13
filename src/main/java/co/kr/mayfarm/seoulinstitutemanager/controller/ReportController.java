package co.kr.mayfarm.seoulinstitutemanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import co.kr.mayfarm.seoulinstitutemanager.dto.ReportDto;
import co.kr.mayfarm.seoulinstitutemanager.service.ReportService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController()
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    private final Logger logger = LoggerFactory.getLogger(ReportController.class);

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> getReports(@ModelAttribute ReportDto.SearchRequest request) {
        int page_no = request.getPage();
        request.setOffset((page_no-1)*10);
        ReportDto.ListResponse response = new ReportDto.ListResponse();
        try{
            response = reportService.getReports(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[getReports] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping({"/", ""})
    public ResponseEntity<?> addReport(@RequestBody ReportDto.AddRequest request) {
        ReportDto.Response response = new ReportDto.Response();
        try{
            response = reportService.addReport(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[addReport] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReportDetail(@PathVariable int id) {
        ReportDto.Response response = new ReportDto.Response();
        try{
            response = reportService.getReportDetail(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[getReportDetail] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{id}")
    public ResponseEntity<?> updateReport(@PathVariable int id, @ModelAttribute ReportDto.UpdateRequest request,
                                          @RequestPart(value = "image_attachments", required = false) List<MultipartFile> imageAttachments ) {
        ReportDto.Response response = new ReportDto.Response();
        request.setImage_attachments(imageAttachments);

        try{
            response = reportService.updateReport(id, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[updateReport] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable int id) {
        ReportDto.Response response = new ReportDto.Response();
        try{
            response = reportService.deleteReport(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[deleteReport] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
