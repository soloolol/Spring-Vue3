package co.kr.mayfarm.seoulinstitutemanager.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class CollectDto {

    @Setter
    public static class AddRequest {
        @NonNull
        String url;
        @NonNull
        String board;
    }

    @Setter
    public static class SearchRequest {
        String source;
        @Getter
        String status;
        String query;
        String startDate;
        String endDate;
        @Getter
        int page=1;
        int pageSize=10;
        boolean isLimit = true;

    }

    @Setter
    @Getter
    public static class Response {
        boolean success;
        String message;
        int result_id;
        int total_count = -1;
    }

    @Setter
    @Getter
    public static class ListResponse extends Response{
        List<?> data;
    }

    @Getter
    @NoArgsConstructor
    public static class SearchResponse {
        int id;
        String source_board;
        String source_category;
        String title;
        String content;
        String status;
        String writed_at;
        String created_at;
    }

    @Getter
    @NoArgsConstructor
    public static class CollectionDetailVO{
        int id;
        String source_url;
        String source_board;
        String writer;
        String collect_keyword;
        String title;
        String status;
        String content;
        String image_files;
        String image_urls;
        String writed_at;
        String created_at;
    }

    @Getter
    @NoArgsConstructor
    public static class DetailResponse  extends Response {
        int id;
        String source_url;
        String source_board;
        String writer;
        String collect_keyword;
        String title;
        String status;
        String content;
        @Setter
        List<String> image_files;
        @Setter
        List<String> image_urls;
        String writed_at;
        String created_at;
        
        public DetailResponse(CollectionDetailVO detailVO) {
            this.id = detailVO.getId();
            this.source_url = detailVO.getSource_url();
            this.source_board = detailVO.getSource_board();
            this.writer = detailVO.getWriter();
            this.collect_keyword = detailVO.getCollect_keyword();
            this.title = detailVO.getTitle();
            this.status = detailVO.getStatus();
            this.content = detailVO.getContent();
            this.writed_at = detailVO.getWrited_at();
            this.created_at = detailVO.getCreated_at();
            this.image_files = detailVO.getImage_files() != null && !detailVO.getImage_files().equals("") ? Arrays.asList(detailVO.getImage_files().split(",,,")) : new ArrayList<>();
            this.image_urls = detailVO.getImage_urls() != null && !detailVO.getImage_urls().equals("") ? Arrays.asList(detailVO.getImage_urls().split(",,,")) : new ArrayList<>();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ImageResponseVO extends Response {
        String image_url;
        String image_file_id;
        String image_file_name;
        String ocr_result;
        String ocr_boundingbox_position;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageResponse extends Response {
        String ocr_image;
        List<String> ocr_text;
        List<Map<String, Float>> ocr_boundingbox_position;
    }
    
}
