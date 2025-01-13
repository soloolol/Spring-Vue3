package co.kr.mayfarm.seoulinstitutemanager.dto;

import java.lang.reflect.Field;
import java.util.*;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class CrimeDictDto {

    @Setter
    @Getter
    public static class BulkRequest{
       private List<AddRequest> request;
    }


    @Setter
    @Getter
    public static class AddRequest {
        @NonNull
        int category_code;
        @NonNull
        String term;
        String synonym;
        String category_big;
        String category_middle;
    }

    @Setter
    public static class SearchRequest {
        String query;
        String startDate;
        String endDate;
        int page = 1;
        int offset = 0;

        public int getPage() {
            return this.page;
        }
    }

    @Setter
    @Builder
    public static class UpdateRequest {
        int old_category_code;
        String old_term;
        int category_code;
        String term;
        String synonym;
    }

    @Getter
    @NoArgsConstructor
    public static class CategoryMiddle extends Response {
        String category_big;
        String category_middle;
        int category_code;
    }

    @Getter
    @Setter
    public static class Response {
        boolean success;
        String message;
        int result_id;
        int total_count = -1;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ListResponse extends Response {
        List<?> data;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResponseVO {
        int category_code;
        String category_big;
        String category_middle;
        String term;
        String synonym;
        String created_at;
        String updated_at;

        public static Map<String, Object> toMap(SearchResponseVO searchResponse) {
            try {
                Field[] fields = searchResponse.getClass().getDeclaredFields();
                Map<String, Object> results = new HashMap<>();
                for (Field field : fields) {
                    if (field.getName().equals("synonym")) {
                        if(!field.get(searchResponse).equals("")) {
                            results.put(field.getName(), Arrays.toString(field.get(searchResponse).toString().split("@@@@@")));
                        }
                        else {
                            results.put(field.getName(),"");
                        }
                    } else {
                        results.put(field.getName(), field.get(searchResponse));
                    }
                }
                return results;

            } catch (IllegalAccessException | IllegalArgumentException e) {
                return null;
            }

        }

    }

    @Getter
    @NoArgsConstructor
    @ToString
    public static class SearchResponse {
        int category_code;
        String category_big;
        String category_middle;
        String term;
        String synonym;
        String created_at;
        String updated_at;


        public SearchResponse(SearchResponseVO searchResponseVO) {
            this.category_code = searchResponseVO.getCategory_code();
            this.category_big = searchResponseVO.getCategory_big();
            this.category_middle = searchResponseVO.getCategory_middle();
            this.term = searchResponseVO.getTerm();
            this.synonym = searchResponseVO.getSynonym()!=null && !searchResponseVO.getSynonym().equals("") ? searchResponseVO.getSynonym().replace("@@@@@",","):null;
            this.created_at = searchResponseVO.getCreated_at();
            this.updated_at = searchResponseVO.getUpdated_at();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    public static class ExcelResponse extends Response {
        MultipartFile file;
    }

}
