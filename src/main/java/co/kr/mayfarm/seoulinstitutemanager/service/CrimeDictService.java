package co.kr.mayfarm.seoulinstitutemanager.service;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.kr.mayfarm.seoulinstitutemanager.dto.CrimeDictDto;

@Service
public interface CrimeDictService {
    CrimeDictDto.ListResponse getCrimeDicts(CrimeDictDto.SearchRequest request);
    CrimeDictDto.Response addCrimeDict(CrimeDictDto.AddRequest request);
    XSSFWorkbook downloadExcel(HttpServletResponse response);
    CrimeDictDto.Response uploadExcel(MultipartFile file);
    CrimeDictDto.Response updateCrimeDict(int categoryCode, String term, CrimeDictDto.UpdateRequest request);
    CrimeDictDto.Response deleteCrimeDict(int categoryCode, String term);
    CrimeDictDto.ListResponse getCategoryBigList();
    CrimeDictDto.ListResponse getCategoryMiddleList();
    CrimeDictDto.ListResponse getCategoryMiddleByCode();
}
