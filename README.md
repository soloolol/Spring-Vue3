# 통합 API 및 관리자 프론트 화면
 관리자 화면 페이지 입니다.

### 프론트

- Vue
- Vite
- TypeScript
- Vuetify

### 백엔드

- Java 17
- Spring Boot 3.2.4
- Mybatis 3.0.3
- Maven

## 프로젝트 구조

┬─ frontend/  
│ 　 ├─ public/  
│ 　 ├─ src/  
│ 　 ├─ .env.prod  
│ 　 ├─ index.html  
│ 　 ├─ package.json  
│ 　 ├─ tsconfig.json  
│ 　 ├─ tsconfig.node.json  
│ 　 └─ vite.config.ts  
│─ src/  
│ 　 ├─ main/  
│ 　 │ 　 ├─ kr/co/mayfarm/seoulinstitutemanager/  
│ 　 │ 　 │ 　 ├─ config/  
│ 　 │ 　 │ 　 ├─ controller/  
│ 　 │ 　 │ 　 ├─ dto/  
│ 　 │ 　 │ 　 ├─ repository/  
│ 　 │ 　 │ 　 ├─ service/  
│ 　 │ 　 │ 　 └─ SeoulInstituteManagerApplication.java  
│ 　 │ 　 └─ resources/  
│ 　 │ 　　　 ├─mapper/  
│ 　 │ 　　　 ├─static/  
│ 　 │ 　　　 └─application.properties  
│ 　 └─ test/  
│ 　　　 └─ kr/co/mayfarm/seoulinstitutemanager/  
│ 　　　　　 └─ SeounInstituteManagerApplicationTests.java  
├─ pom.xml  
└─ README.md

## 실행 방법

#### 배포

- 관리자 페이지 ('./frontend')
  `yarn install`
  `yarn build`

- API
  `maven package`

#### 실행

`java -jar spring-vue3-admin.jar`


