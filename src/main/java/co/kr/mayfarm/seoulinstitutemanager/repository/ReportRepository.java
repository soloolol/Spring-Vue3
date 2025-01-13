package co.kr.mayfarm.seoulinstitutemanager.repository;

import java.util.List;
import java.util.Map;

import co.kr.mayfarm.seoulinstitutemanager.dto.AnalyzeDto;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import co.kr.mayfarm.seoulinstitutemanager.dto.ReportDto;

@Repository
public interface ReportRepository {
    
    public List<ReportDto.SearchResponse> searchReport(@Param(value="dto") ReportDto.SearchRequest request);

    public int getSearchReportCount(@Param(value="dto") ReportDto.SearchRequest request);
    
    public ReportDto.ReportDetailVO getReportDetail(@Param(value="id") int id);

    @Insert("""
        INSERT INTO collected_data(
            source_url,
            content,
            reported,
            source_board
        )
        VALUES(
            #{dto.url},
            #{dto.content},
            1,
            ""
        )
            """)
    int insertReport(@Param(value="dto") ReportDto.AddRequest request);


    @Delete("""
        DELETE 
        FROM collected_data
        WHERE
            id=#{id}
            """)
    public void deleteReport(@Param(value="id") int id);

    @Select("SELECT max(id) FROM collected_data WHERE reported=1")
    public int getLastReportId();

    void updateReport(@Param(value="dto") AnalyzeDto.UpdateRequest analyze);

    @Select("SELECT image_file_name, image_file_origin, image_url FROM collected_data WHERE id=#{id}")
    Map<String, String> getFileDetails(@Param(value="id") int collectId);

    @Update("UPDATE collected_data SET status=#{dto.collect_status} WHERE id=#{dto.collect_id}")
    void updateStatus(@Param(value="dto") AnalyzeDto.UpdateRequest analyze);
}
