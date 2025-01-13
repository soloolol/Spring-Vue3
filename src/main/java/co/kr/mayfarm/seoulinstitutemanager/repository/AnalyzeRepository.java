package co.kr.mayfarm.seoulinstitutemanager.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import co.kr.mayfarm.seoulinstitutemanager.dto.AnalyzeDto;


@Repository
public interface AnalyzeRepository {

     AnalyzeDto.Analyses searchAnalyses(@Param(value="dto") AnalyzeDto.SearchRequest request);
    
     AnalyzeDto.AnalysisDetailVO getAnalysisDetail(@Param(value="id") int id);

     void insertAnalysis(@Param(value="dto") AnalyzeDto.AddRequest request);

     void updateAnalysis(@Param(value="dto") AnalyzeDto.UpdateRequest request);

    @Delete("""
        DELETE FROM analyzed_data
        WHERE
            id=#{id}
            """)
     void deleteAnalysis(@Param(value="id") int id);

    @Delete("""
        DELETE FROM analyzed_data
        WHERE
            collect_id=#{id}
            """)
     void deleteAnalysisByCollectId(@Param(value="id") int id);

    @Select("""
        SELECT
            crime_type_code
        FROM
            analyzed_crime_type
        WHERE
            analyzed_data_id=#{id}
            """)
    List<Integer> getAnalysisCodes(@Param(value="id") int id);
    
    @Insert("""
        INSERT INTO analyzed_crime_type(
            analyzed_data_id, crime_type_code
        )
        VALUES
            (#{id}, #{crime_type})
            """)
    void insertAnalysisResults(@Param(value="id") int id, @Param(value="crime_type") Integer crime_type);

    @Delete("""
        DELETE FROM analyzed_crime_type
        WHERE
            analyzed_data_id=#{id}
            AND crime_type_code=#{crime_type}
            """)
    void deleteAnalysisResults(@Param(value="id") int id, @Param(value="crime_type") Integer crime_type);

    List<AnalyzeDto.CTStats> getCrimeTypeStats(@Param(value="dto") AnalyzeDto.SearchRequest request);

    @Select("""
        SELECT
            id
        FROM
            analyzed_data
        WHERE
            collect_id=#{collectId}
            """)
    int getAnalysisIdByCollectId(int collectId);

    AnalyzeDto.Analyses searchNewAnalyses(@Param(value="page") int page);

    @Select("""
        SELECT
        GROUP_CONCAT(a.id) as ids,
        SUM(CASE WHEN h.read = 0 THEN 1 ELSE 0 END) AS unread
        FROM history as h
             LEFT JOIN
             analyzed_data as a
             ON h.analyzed_data_id = a.id
        WHERE
            a.status = '대기'
        ORDER BY a.created_at DESC
        """)
    List<Map<String, Object>> checkNew();

    @Update("""
        UPDATE
        history
        SET history.read = 1
        WHERE analyzed_data_id = #{id}
            """)
    void updateRead(@Param(value="id") int id);

    @Delete("""
        DELETE FROM analyzed_crime_type
        WHERE
            analyzed_data_id=#{id}
            """)
    void deleteCrimeType(int id);

    AnalyzeDto.Analyses getNotices(@Param(value="dto") AnalyzeDto.SearchRequest request);
}
