package co.kr.mayfarm.seoulinstitutemanager.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.kr.mayfarm.seoulinstitutemanager.dto.AnalyzeDto;
import co.kr.mayfarm.seoulinstitutemanager.repository.AnalyzeRepository;

@Service
public class AnalyzeServiceImpl implements AnalyzeService {

    @Autowired
    AnalyzeRepository analyzeRepository;
    
    @Override
    public AnalyzeDto.Analyses getAnalyses(AnalyzeDto.SearchRequest request) {
        AnalyzeDto.Analyses response = new AnalyzeDto.Analyses();
        AnalyzeDto.Analyses result = analyzeRepository.searchAnalyses(request);
        if (result == null) {
            response.setTotal_count(0);
        } else {
            response = result;
        }
        response.setSuccess(true);
        return response;
    }

    @Override
    public AnalyzeDto.Analyses getNewAnalyses(int page) {
        AnalyzeDto.Analyses response = new AnalyzeDto.Analyses();
        AnalyzeDto.Analyses result = analyzeRepository.searchNewAnalyses(page);
        if (result == null) {
            response.setTotal_count(0);
        } else {
            response = result;
        }
        response.setSuccess(true);
        return response;
    }
    @Override
    public AnalyzeDto.Analyses getNotices(AnalyzeDto.SearchRequest request) {
        AnalyzeDto.Analyses response = new AnalyzeDto.Analyses();
        AnalyzeDto.Analyses result = analyzeRepository.getNotices(request);
        if (result == null) {
            response.setTotal_count(0);
        } else {
            response = result;
        }
        response.setSuccess(true);
        return response;
    }
    @Override
    public AnalyzeDto.DetailResponse getAnalysisDetail(int id) {
        AnalyzeDto.DetailResponse response;
        AnalyzeDto.AnalysisDetailVO result = analyzeRepository.getAnalysisDetail(id);
        response = new AnalyzeDto.DetailResponse(result);
        response.setSuccess(true);
        return response;
    }

    @Override
    public AnalyzeDto.Response updateAnalysis(int id, AnalyzeDto.UpdateRequest request) {
        request.setId(id);
        AnalyzeDto.Response response = new AnalyzeDto.Response();
        try {
            analyzeRepository.updateAnalysis(request);
            int categoryCode = request.getCategory_code();
            List<Integer> analyzedCategoryCodes = analyzeRepository.getAnalysisCodes(id);
            for(int analyzedCode : analyzedCategoryCodes) {
                if(categoryCode!=analyzedCode) {
                    analyzeRepository.deleteAnalysisResults(id, analyzedCode);
                }
            }
            if(!analyzedCategoryCodes.contains(categoryCode)) {
                analyzeRepository.insertAnalysisResults(id, categoryCode);
            }
            response.setSuccess(true);
            response.setResult_id(id);
        }
        catch(Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public AnalyzeDto.Response deleteAnalysis(int id) {
        AnalyzeDto.Response response = new AnalyzeDto.Response();
        try {
            analyzeRepository.deleteAnalysis(id);
            response.setSuccess(true);
            response.setResult_id(id);
        }
        catch(Exception e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public AnalyzeDto.Response getCrimeTypeStats(AnalyzeDto.SearchRequest request) {
        AnalyzeDto.Response response = new AnalyzeDto.Response();

        List<AnalyzeDto.CTStats> ctStats = analyzeRepository.getCrimeTypeStats(request);
        Map<Integer,Integer> result = new HashMap<>();
        response.setSuccess(true);
        for(AnalyzeDto.CTStats item : ctStats){
            result.put(item.getCrimeCode(), item.getNumber());
        }
        response.setData(Collections.singletonList(result));

        return response;
    }

    @Override
    public AnalyzeDto.Response checkNew() {
        AnalyzeDto.Response response = new AnalyzeDto.Response();
        List<Map<String,Object>> result = analyzeRepository.checkNew();
        response.setSuccess(true);
        response.setData(result);
        return response;
    }

    @Override
    public AnalyzeDto.Response updateRead(int id) {
        AnalyzeDto.Response response = new AnalyzeDto.Analyses();
        analyzeRepository.updateRead(id);
        response.setSuccess(true);
        return response;
    }


}
