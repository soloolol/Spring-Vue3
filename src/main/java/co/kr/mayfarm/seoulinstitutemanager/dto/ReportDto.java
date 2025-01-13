package co.kr.mayfarm.seoulinstitutemanager.dto;

import java.util.*;


import io.micrometer.common.util.StringUtils;
import lombok.*;
import org.springframework.lang.NonNull;

import org.springframework.web.multipart.MultipartFile;

public class ReportDto {

    @Setter
    public static class AddRequest {
        @NonNull
        String url;
        @NonNull
        String content;
    }

    @Setter
    public static class SearchRequest {
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
    @Getter
    public static class UpdateRequest {
        private String collect_status;
        private Integer category_code;
        private String time;
        private String place_addr;
        private Float place_x;
        private Float place_y;
        private String admin_comment;
        private List<MultipartFile> image_attachments;
        private List<String> image_dels;
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
    public static class ListResponse extends Response {
        List<?> data;
    }

    @Getter
    @NoArgsConstructor
    public static class SearchResponse {
        int id;
        String source_url;
        String content;
        String admin_comment;
        String status;
        String analyzed_status;
        String reported_at;
        String updated_at;
    }

    @Getter
    @NoArgsConstructor
    public static class ReportDetailVO{
        int id;
        int analyzed_id;
        String status;
        String category_big;
        String category_middle;
        Integer category_code;
        String analyzed_time;
        String analyzed_place_addr;
        Float analyzed_place_x;
        Float analyzed_place_y;
        String source_url;
        String content;
        String admin_comment;
        String image_file_origins;
        String image_file_names;
        String image_urls;
        String reported_at;
        String analyzed_at;
        String updated_at;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DetailResponse extends Response {
        int id;
        int analyzed_id;
        String status;
        String category_big;
        String category_middle;
        Integer category_code;
        String analyzed_time;
        String analyzed_place_addr;
        Float analyzed_place_x;
        Float analyzed_place_y;
        String source_url;
        String content;
        String admin_comment;
        List<Object> image_list;
        String reported_at;
        String analyzed_at;
        String updated_at;


        public DetailResponse(ReportDetailVO detailVO) {
            this.id = detailVO.getId();
            this.analyzed_id = detailVO.getAnalyzed_id();
            this.status = detailVO.getStatus();
            this.category_big = detailVO.getCategory_big();
            this.category_middle = detailVO.getCategory_middle();
            this.category_code = detailVO.getCategory_code();
            this.analyzed_time = detailVO.getAnalyzed_time();
            this.analyzed_place_addr = detailVO.getAnalyzed_place_addr();
            this.analyzed_place_x = detailVO.getAnalyzed_place_x();
            this.analyzed_place_y = detailVO.getAnalyzed_place_y();
            this.source_url = detailVO.getSource_url();
            this.content = detailVO.getContent();
            this.admin_comment = detailVO.getAdmin_comment();
            this.reported_at = detailVO.getReported_at();
            this.analyzed_at = detailVO.getAnalyzed_at();
            this.updated_at = detailVO.getUpdated_at();

            List<Object> image_list = new ArrayList<>();
            if (detailVO.getImage_file_names()!=null && StringUtils.isNotBlank(detailVO.getImage_file_names())) {
                String[] names = detailVO.getImage_file_names().split(",,,");
                String[] origins = detailVO.getImage_file_origins() != null ? detailVO.getImage_file_origins().split(",,,") : null;
                String[] urls = detailVO.getImage_urls() != null ? detailVO.getImage_urls().split(",,,") : null;

                for (int i = 0; i < names.length; i ++) {
                    Map<String,Object> image = new HashMap<>();
                    image.put("name", names[i]);
                    image.put("origin", origins.length >= i ? origins[i] : null);
                    image.put("url", urls.length >= i ? urls[i] : null);
                    image_list.add(image);
                }
            }

            this.image_list = image_list;
        }
    }

}
