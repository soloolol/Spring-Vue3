package co.kr.mayfarm.seoulinstitutemanager.dto;

import java.util.*;

import io.micrometer.common.util.StringUtils;
import lombok.*;

public class AnalyzeDto {

    @Setter
    public static class SearchRequest {
        String crime_category_big;
        String crime_category_code;
        String analyzed_place_addr;
        String status;
        String query;
        String startDate;
        String endDate;
        int page=1;
        int offset=0;

        public int getPage() {
            return this.page;
        }
    }

    @Setter
    @AllArgsConstructor
    public static class AddRequest {
        int collect_id;
        String time;
        String place_addr;
        float place_x;
        float place_y;
        String status;
        String admin_comment;
    }

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequest {
        int id;
        int collect_id;
        String status;
        String collect_status;
        Integer category_code;
        String time;
        String place_addr;
        Float place_x;
        Float place_y;
        String image_file_name;
        String image_file_path;
        String image_file_origin;
        String image_url;
        String ocr_results;
        String ocr_boundingbox_position;
        String admin_comment;

    }

    @Setter
    @Getter
    public static class Response {
        boolean success;
        String message;
        int result_id;
        Integer total_count = -1;
        List<?> data;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Analyses extends Response{
        List<Analysis> analyses;
    }

    @Getter
    @NoArgsConstructor
    public static class Analysis {
        int id;
        int read;
        int reported;
        String source_board;
        String source_category;
        String title;
        String content;
        String status;
        String analyzed_type_big;
        String analyzed_type_middle;
        Integer analyzed_type_code;
        String analyzed_time;
        String analyzed_place_addr;
        Float analyzed_place_x;
        Float analyzed_place_y;
        String collected_at;
        String analyzed_at;
    }

    @Getter
    @NoArgsConstructor
    public static class CTStats {
        int crimeCode;
        int number;
    }

    @Getter
    @NoArgsConstructor
    public static class AnalysisDetailVO{
        int id;
        int collect_id;
        String status;
        int reported;
        String analyzed_type_big;
        String analyzed_type_middle;
        Integer analyzed_type_code;
        String analyzed_type2;
        String analyzed_type3;
        String analyzed_time;
        String analyzed_place_addr;
        Float analyzed_place_x;
        Float analyzed_place_y;
        String analyzed_target;
        String analyzed_action;
        String collect_keyword;
        String source_url;
        String source_board;
        String title;
        String content;
        String image_file_names;
        String image_file_origins;
        String image_urls;
        String writed_at;
        String collected_at;
        String analyzed_at;
        String ocr_result;
    }
    
    @Getter
    @NoArgsConstructor
    public static class DetailResponse extends Response {
        int id;
        int collect_id;
        String status;
        int reported;
        String analyzed_type_big;
        String analyzed_type_middle;
        Integer analyzed_type_code;
        String analyzed_type2;
        String analyzed_type3;
        String analyzed_time;
        String analyzed_place_addr;
        Float analyzed_place_x;
        Float analyzed_place_y;
        String analyzed_target;
        String analyzed_action;
        String collect_keyword;
        String source_url;
        String source_board;
        String title;
        String content;
        List<Object> image_list;
        String writed_at;
        String collected_at;
        String analyzed_at;
        List<String> ocr_result;

        public DetailResponse(AnalysisDetailVO detailVO) {
            this.id = detailVO.getId();
            this.collect_id = detailVO.getCollect_id();
            this.reported = detailVO.getReported();
            this.status = detailVO.getStatus();
            this.analyzed_type_big = detailVO.getAnalyzed_type_big();
            this.analyzed_type_code = detailVO.getAnalyzed_type_code();
            this.analyzed_type2 = detailVO.getAnalyzed_type2();
            this.analyzed_type3 = detailVO.getAnalyzed_type3();
            this.analyzed_type_middle = detailVO.getAnalyzed_type_middle();
            this.analyzed_time = detailVO.getAnalyzed_time();
            this.analyzed_place_addr = detailVO.getAnalyzed_place_addr();
            this.analyzed_place_x = detailVO.getAnalyzed_place_x();
            this.analyzed_place_y = detailVO.getAnalyzed_place_y();
            this.analyzed_target = detailVO.getAnalyzed_target();
            this.analyzed_action = detailVO.getAnalyzed_action();
            this.collect_keyword = detailVO.getCollect_keyword();
            this.source_url = detailVO.getSource_url();
            this.source_board = detailVO.getSource_board();
            this.title = detailVO.getTitle();
            this.content = detailVO.getContent();
            this.writed_at = detailVO.getWrited_at();
            this.collected_at = detailVO.getCollected_at();
            this.analyzed_at = detailVO.getAnalyzed_at();
            this.ocr_result = detailVO.getOcr_result() != null && detailVO.getOcr_result() != "null" && StringUtils.isNotBlank(detailVO.getOcr_result())? Arrays.stream(detailVO.getOcr_result().split("@@@@@")).filter(i-> !Objects.equals(i, "null")).toList() : null;

            List<Object> image_list = new ArrayList<>();
            if (detailVO.getImage_file_names()!=null && StringUtils.isNotBlank(detailVO.getImage_file_names())) {
                String[] names = detailVO.getImage_file_names().split(",,,");
                String[] origins = detailVO.getImage_file_origins() != null ? detailVO.getImage_file_origins().split(",,,") : null;
                String[] urls = detailVO.getImage_urls() != null ? detailVO.getImage_urls().split(",,,") : null;

                for (int i = 0; i < names.length; i ++) {
                    Map<String,Object> image = new HashMap<>();
                    image.put("name", names[i]);
                    image.put("origin", origins != null && origins.length >= i ? origins[i] : null);
                    image.put("url", urls != null && urls.length >= i ? urls[i] : null);
                    image_list.add(image);
                }
            }

            this.image_list = image_list;

        }
    }
}
