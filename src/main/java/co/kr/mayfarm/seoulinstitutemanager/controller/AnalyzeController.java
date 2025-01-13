package co.kr.mayfarm.seoulinstitutemanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.kr.mayfarm.seoulinstitutemanager.dto.AnalyzeDto;
import co.kr.mayfarm.seoulinstitutemanager.service.AnalyzeService;



@RestController()
@RequestMapping("/analyses")
public class AnalyzeController {

    private final AnalyzeService analyzeService;

    private final Logger logger = LoggerFactory.getLogger(AnalyzeController.class);

    public AnalyzeController(AnalyzeService analyzeService) {
        this.analyzeService = analyzeService;
    }
    
    @GetMapping({"/", ""})
    public ResponseEntity<?> searchAnalyses(@ModelAttribute AnalyzeDto.SearchRequest request) {
        
        int page_no = request.getPage();
        request.setOffset((page_no-1)*10);
        AnalyzeDto.Analyses response = new AnalyzeDto.Analyses();
        
        try{
            response = analyzeService.getAnalyses(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[searchAnalyses] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping({"/notice"})
    public ResponseEntity<?> getNotices(@ModelAttribute AnalyzeDto.SearchRequest request) {

        int page_no = request.getPage();
        request.setOffset((page_no-1)*10);
        AnalyzeDto.Analyses response = new AnalyzeDto.Analyses();

        try{
            request.setStatus("승인");
            response = analyzeService.getNotices(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[getNotices] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAnalysisDetail(@PathVariable int id) {

        AnalyzeDto.DetailResponse response = new AnalyzeDto.DetailResponse();

        try{
            response = analyzeService.getAnalysisDetail(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[getAnalysisDetail] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping({"/new"})
    public ResponseEntity<?> searchNewAnalyses(@RequestParam(required = false, defaultValue = "1") int page) {

        AnalyzeDto.Analyses response = new AnalyzeDto.Analyses();

        try{
            response = analyzeService.getNewAnalyses(page);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[searchNewAnalyses] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping({"/check"})
    public ResponseEntity<?> checkNew() {

        AnalyzeDto.Response response = new AnalyzeDto.Response();

        try{
            response = analyzeService.checkNew();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[checkNew] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping({"/read/{id}"})
    public ResponseEntity<?> updateRead(@PathVariable int id) {

        AnalyzeDto.Response response = new AnalyzeDto.Response();

        try{
            response = analyzeService.updateRead(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[updateRead] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getCrimeTypeStats(@ModelAttribute AnalyzeDto.SearchRequest request) {
        AnalyzeDto.Response response = new AnalyzeDto.Response();
        try{
            response = analyzeService.getCrimeTypeStats(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.error("[getCrimeTypeStats] :", e );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateAnalysis(@PathVariable int id, @RequestBody AnalyzeDto.UpdateRequest request) {
        AnalyzeDto.Response updateResponse = analyzeService.updateAnalysis(id, request);

        if(updateResponse.isSuccess()) {
            return new ResponseEntity<>("Analysis Data Updated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(updateResponse.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAnalysis(@PathVariable int id) {
        AnalyzeDto.Response deleteResponse = analyzeService.deleteAnalysis(id);

        if(deleteResponse.isSuccess()) {
            return new ResponseEntity<>("Analysis Data Deleted.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(deleteResponse.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
