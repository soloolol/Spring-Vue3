package co.kr.mayfarm.seoulinstitutemanager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ManagerViewController {

    @Value("${kakao.map.key}")
    String KAKAO_MAP_KEY;

    @GetMapping("/manager**")
    public String index() {
        return "index.html";
    }

    @GetMapping({"/kakao"})
    public ResponseEntity<?> getKakaoKey() {

        return new ResponseEntity<>(KAKAO_MAP_KEY, HttpStatus.OK);

    }
}