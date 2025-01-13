package co.kr.mayfarm.seoulinstitutemanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.kr.mayfarm.seoulinstitutemanager.dto.AnalyzeDto;

@Service
public interface AnalyzeService {
    
     AnalyzeDto.Analyses getAnalyses(AnalyzeDto.SearchRequest request);
     AnalyzeDto.Analyses getNewAnalyses(int page);
     AnalyzeDto.DetailResponse getAnalysisDetail(int id);
     AnalyzeDto.Response updateAnalysis(int id, AnalyzeDto.UpdateRequest request);
     AnalyzeDto.Response deleteAnalysis(int id);
    AnalyzeDto.Response getCrimeTypeStats(AnalyzeDto.SearchRequest request);
     AnalyzeDto.Response checkNew();
    AnalyzeDto.Response updateRead(int id);

    AnalyzeDto.Analyses getNotices(AnalyzeDto.SearchRequest request);
}
