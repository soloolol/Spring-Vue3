package co.kr.mayfarm.seoulinstitutemanager.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.kr.mayfarm.seoulinstitutemanager.dto.AnalyzeDto;
import co.kr.mayfarm.seoulinstitutemanager.dto.ReportDto;
import co.kr.mayfarm.seoulinstitutemanager.repository.AnalyzeRepository;
import co.kr.mayfarm.seoulinstitutemanager.repository.ReportRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReportServiceImpl implements ReportService {
    
    @Autowired
    ReportRepository reportRepository;

    @Autowired
    AnalyzeRepository analyzeRepository;

    @Value("${dir.attachment}")
    String ATTACHMENT_DIR;

    @Override
    public ReportDto.ListResponse getReports(ReportDto.SearchRequest request) {
        ReportDto.ListResponse response = new ReportDto.ListResponse();

        List<ReportDto.SearchResponse> reportsList = reportRepository.searchReport(request);
        int reportListCount = reportRepository.getSearchReportCount(request);
        response.setData(reportsList);
        response.setTotal_count(reportListCount);
        response.setSuccess(true);
        return response;
    }
    
    @Override
    public ReportDto.Response addReport(ReportDto.AddRequest request) {
        ReportDto.Response response = new ReportDto.Response();

        reportRepository.insertReport(request);
        int lastId = reportRepository.getLastReportId();
        AnalyzeDto.AddRequest addRequest = new AnalyzeDto.AddRequest(lastId, null, null, 0, 0, "대기", null);
        analyzeRepository.insertAnalysis(addRequest);
        response.setSuccess(true);
        return response;
    }
    
    @Override
    public ReportDto.Response getReportDetail(int id) {
        ReportDto.DetailResponse  response;

        ReportDto.ReportDetailVO reportDetail = reportRepository.getReportDetail(id);
        response = new ReportDto.DetailResponse(reportDetail);
        response.setSuccess(true);
        return response;
    }
    
    @Override
    @Transactional
    public ReportDto.Response updateReport(int collectId, ReportDto.UpdateRequest report) throws IOException {

        ReportDto.Response response = new ReportDto.Response();

        AnalyzeDto.UpdateRequest analyze = AnalyzeDto.UpdateRequest.builder()
                .collect_status(report.getCollect_status())
                .category_code(report.getCategory_code())
                .time(report.getTime())
                .place_addr(report.getPlace_addr())
                .place_x(report.getPlace_x())
                .place_y(report.getPlace_y())
                .admin_comment(report.getAdmin_comment())
                .collect_id(collectId).build();

        analyzeRepository.updateAnalysis(analyze);
        reportRepository.updateStatus(analyze);

        Integer categoryCode = analyze.getCategory_code();
        int id = analyzeRepository.getAnalysisIdByCollectId(collectId);
        if (categoryCode == null) {
            analyzeRepository.deleteCrimeType(id);
        } else {
            List<Integer> analyzedCategoryCodes = analyzeRepository.getAnalysisCodes(id);
            for(Integer analyzedCode : analyzedCategoryCodes) {
                if(categoryCode!=analyzedCode) {
                    analyzeRepository.deleteAnalysisResults(id, analyzedCode);
                }
            }
            if(!analyzedCategoryCodes.contains(categoryCode)) {
                analyzeRepository.insertAnalysisResults(id, categoryCode);
            }
        }

        List<String> delNames = report.getImage_dels();
        List<MultipartFile> newFiles = report.getImage_attachments();
        List<String> tempFileNames = new ArrayList<>();
        List<String> tempFileNameOrgs = new ArrayList<>();
        List<String> tempFileUrls = new ArrayList<>();
        String filePath = ATTACHMENT_DIR+File.separator+collectId;
        String del = ",,,";

        if (delNames != null || newFiles != null) {
            Map<String,String> fileDetails = reportRepository.getFileDetails(collectId);
            if (fileDetails != null && StringUtils.isNotBlank(fileDetails.get("image_file_name"))){
                tempFileNames.addAll( List.of(fileDetails.get("image_file_name").split(del)));
                tempFileNameOrgs.addAll(List.of(fileDetails.get("image_file_origin").split(del)));
                tempFileUrls.addAll(List.of(fileDetails.get("image_url").split(del)));
            }
            if (delNames != null) {
                for (String delName : delNames) {
                    String fullPath = Path.of(filePath, delName).toString();
                    File file = new File(fullPath);
                    file.deleteOnExit();
                    int removeIndex = tempFileNames.indexOf(delName);
                    tempFileNameOrgs.remove(removeIndex);
                    tempFileUrls.remove(removeIndex);
                    tempFileNames.remove(removeIndex);
                }
            }

            if (newFiles != null) {
                File dir = new File(filePath);
                if(!dir.exists() && !dir.isDirectory()) {
                    dir.mkdir();
                }
                for(MultipartFile file : newFiles){
                    if (!file.isEmpty()) {
                        String fileName = UUID.randomUUID().toString();
                        String fileNameOrg = file.getOriginalFilename();

                        String extension = fileNameOrg.split("\\.")[1];
                        String fullPath = Path.of(filePath, fileName+"."+extension).toString();
                        file.transferTo(new File(fullPath));
                        tempFileNames.add(fileName);
                        tempFileNameOrgs.add(fileNameOrg);
                        tempFileUrls.add("images/"+collectId+"/"+ fileName+"."+extension);
                    }
                }
            }
            analyze.setImage_file_path(filePath);
            analyze.setImage_file_name(String.join(del, tempFileNames));
            analyze.setImage_file_origin(String.join(del, tempFileNameOrgs));
            analyze.setImage_url(String.join(del, tempFileUrls));

            reportRepository.updateReport(analyze);
        }

        response.setSuccess(true);
        response.setResult_id(id);

        return response;
    }
    
    @Override
    public ReportDto.Response deleteReport(int id) {
        ReportDto.Response response = new ReportDto.Response();

        reportRepository.deleteReport(id);
        response.setSuccess(true);
        response.setResult_id(id);

        return response;
    }
}
