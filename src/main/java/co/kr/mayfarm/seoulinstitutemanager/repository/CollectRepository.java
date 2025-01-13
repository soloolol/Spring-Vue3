package co.kr.mayfarm.seoulinstitutemanager.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import co.kr.mayfarm.seoulinstitutemanager.dto.CollectDto;

@Repository
public interface CollectRepository {

    public List<CollectDto.SearchResponse> searchCollection(@Param(value="dto") CollectDto.SearchRequest request);
    
    public int getSearchCollectionCount(@Param(value="dto") CollectDto.SearchRequest request);

    @Select("""
        SELECT 
            id, 
            source_url, 
            source_board, 
            writer,
            collect_keyword, 
            title, 
            status, 
            content, 
            image_file_name as image_files, 
            image_url as image_urls, 
            writed_at,
            created_at
        FROM collected_data
        WHERE 
            1=1 
            AND id=#{id}
            """)
    public CollectDto.CollectionDetailVO getCollectionDetail(@Param(value="id") int id);

    // public void insertCollection(CollectDto.AddRequest request);
    @Delete("""
        DELETE 
        FROM 
            collected_data
        WHERE
            id=#{id}
            """)
    public void deleteCollection(@Param(value="id") int id);

    @Select("""
        SELECT 
            image_url,
            image_file_id,
            image_file_name,
            ocr_result, 
            ocr_boundingbox_position
        FROM
            collected_data
        WHERE
            id=#{id}
            """)
    public CollectDto.ImageResponseVO getCollectionImageData(@Param(value="id") int id);

    @Select("""
            SELECT
                GROUP_CONCAT(DISTINCT source_board) AS filtered_source_board
            FROM
                collected_data
            WHERE
                source_board <> ''
            """)
    String getSourceBoardList();

    List<Map<String, Object>> downloadCollections(@Param(value="dto") CollectDto.SearchRequest request);
    @Select("""
            SELECT COLUMN_NAME
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_NAME = 'collected_data' AND TABLE_SCHEMA = #{db};
            """)
    List<String> getFields(@Param(value="db") String dbName);
}
