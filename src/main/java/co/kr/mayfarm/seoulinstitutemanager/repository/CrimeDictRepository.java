package co.kr.mayfarm.seoulinstitutemanager.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import co.kr.mayfarm.seoulinstitutemanager.dto.CrimeDictDto;

@Repository
public interface CrimeDictRepository {
    
    public List<CrimeDictDto.SearchResponseVO> searchCrimeDict(@Param(value="dto") CrimeDictDto.SearchRequest request);

    int searchCrimeDictCount(@Param(value="dto") CrimeDictDto.SearchRequest request);
    
    public int insertCrimeDict(@Param(value="dto") CrimeDictDto.AddRequest request);

    public void updateCrimeDict(@Param(value="dto") CrimeDictDto.UpdateRequest request);

    @Select("""
            SELECT count(*) FROM crime_dictionary WHERE category_code=#{category_code} AND term=#{term}
            """)
    int countByCategoryCodeAndTerm(@Param(value="category_code") int categoryCode, @Param(value="term") String term);

    @Delete("""
        DELETE 
        FROM 
            crime_dictionary
        WHERE
            category_code=#{category_code}
            AND term=#{term}
            """)
    public void deleteCrimeDict(@Param(value="category_code") int categoryCode, @Param(value="term") String term);

    @Select("SELECT * FROM crime_dictionary")
    public List<CrimeDictDto.SearchResponseVO> getAllCrimeDict();

    @Select("SELECT DISTINCT(category_big) FROM crime_dictionary")
    public List<String> getCategoryBigList();

    @Select("SELECT category_code, category_middle, category_big FROM crime_dictionary GROUP BY category_middle")
    public List<CrimeDictDto.CategoryMiddle> getCategoryMiddleList();

    @Select("SELECT category_code, category_middle FROM crime_dictionary GROUP BY category_middle")
    public List<CrimeDictDto.CategoryMiddle> getCategoryMiddleByCode();
}
