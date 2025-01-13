package co.kr.mayfarm.seoulinstitutemanager.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import co.kr.mayfarm.seoulinstitutemanager.dto.AnalyzeDto;
import co.kr.mayfarm.seoulinstitutemanager.dto.ReportDto;

@Service
public interface ReportService {
    
    public ReportDto.ListResponse getReports(ReportDto.SearchRequest request);
    public ReportDto.Response addReport(ReportDto.AddRequest request);
    public ReportDto.Response getReportDetail(int id);
    public ReportDto.Response updateReport(int id, ReportDto.UpdateRequest request) throws IOException;
    public ReportDto.Response deleteReport(int id);
}
