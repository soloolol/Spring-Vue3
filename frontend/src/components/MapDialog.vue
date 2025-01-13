<template>
  <v-card prepend-icon="mdi-map" title="지도매핑">
    <div id="map" height="60vh"></div>
    <div class="hAddr">
      <span class="title">지도중심기준 행정동 주소정보</span>
      <span id="centerAddr"></span>
    </div>
    <template v-slot:actions>
      <v-btn class="ms-auto" color="green" variant="outlined" @click="updatePosition" text="적용"></v-btn>
      <v-btn text="닫기" variant="tonal" @click="emits('onClose')"></v-btn>
    </template>
  </v-card>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from "vue";
import { useCommonService } from "@/api/common";

const commonService = useCommonService();

onMounted(() => {
  if (window.kakao?.maps) {
    initMap();
  } else {
    loadScript();
  }
});

const props = defineProps<{
  details?: object;
}>();

const emits = defineEmits(["onClose", "update:position"]);

const kakaoMapKey = ref();

let map = null;
const initLng = props.details.analyzed_place_x ? props.details.analyzed_place_x : 127.12;
const initLat = props.details.analyzed_place_y ? props.details.analyzed_place_y : 37.4935;
const notices = ref([]);
const notice = reactive({ marker: null, overlay: null, details: null, address: null, road_address: null });

let geocoder = null;

function updatePosition() {
  let value = {
    address: notice.address,
    lat: notice.marker.getPosition().getLat(),
    lng: notice.marker.getPosition().getLng(),
  };
  emits("update:position", value);
  emits("onClose");
}

async function loadScript() {
  await commonService.getKakaoKey().then((res) => {
    kakaoMapKey.value = res;
  });
  const script = document.createElement("script");
  script.src = `//dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=${kakaoMapKey.value}&libraries=services,clusterer`;
  script.onload = () => kakao.maps.load(initMap);
  document.head.appendChild(script);
}

function initMap() {
  const container = document.getElementById("map");
  const options = {
    center: new kakao.maps.LatLng(initLat, initLng),
    level: 5,
  };

  // 지도 생성
  map = new kakao.maps.Map(container, options);

  // 주소-좌표 변환 객체를 생성
  geocoder = new kakao.maps.services.Geocoder();
  // 현재 지도 중심좌표로 주소를 검색해서 지도 좌측 상단에 표시
  searchAddrFromCoords(map.getCenter(), displayCenterInfo);

  // 중심 좌표나 확대 수준이 변경됐을 때 지도 중심 좌표에 대한 주소 정보를 표시하도록 이벤트를 등록
  kakao.maps.event.addListener(map, "idle", function () {
    searchAddrFromCoords(map.getCenter(), displayCenterInfo);
  });

  // 부모에서 전달 받은 디테일들 notice객체로 생성
  setNotices(props.details);

  // 생성한 notice들 지도 위에 표시
  notices.value.forEach((notice) => {
    notice.marker.setMap(map);
  });
}

function setNotices(details) {
  // 관리자 페이지 details는 객체 하나만 받음, 리스트 되면 for문으로 변경
  notice.details = details;
  notice.marker = getMarker(details);
  notice.overlay = initOverlay(details, notice.marker);
  searchDetailAddrFromCoords(notice.marker.getPosition(), updateAddress);

  // 마커를 클릭했을 때 커스텀 오버레이를 표시
  kakao.maps.event.addListener(notice.marker, "click", function () {
    searchDetailAddrFromCoords(notice.marker.getPosition(), updateAddress);
    notice.overlay.setPosition(notice.marker.getPosition());
    notice.overlay.setMap(map);
  });

  kakao.maps.event.addListener(notice.marker, "dragend", function () {
    searchDetailAddrFromCoords(notice.marker.getPosition(), updateAddress);
    notice.overlay.setPosition(notice.marker.getPosition());
    notice.overlay.setMap(map);
  });

  notices.value.push(notice);
}

function getMarker(details) {
  let marker = new kakao.maps.Marker({
    position: new kakao.maps.LatLng(initLat, initLng),
  });

  // 마커가 드래그 가능하도록 설정합니다
  marker.setDraggable(true);
  return marker;
}

function initOverlay(details, marker) {
  return new kakao.maps.CustomOverlay({
    content: createContent(
      notice.details.analyzed_type_middle,
      notice.details.analyzed_at,
      notice.details.analyzed_place_addr,
      "",
      notice.details.source_url
    ),
    map: map,
    position: marker.getPosition(),
  });
}

function closeOverlay(overlay) {
  overlay.setMap(null);
}

function searchAddrFromCoords(coords, callback) {
  // 좌표로 행정동 주소 정보를 요청합니다
  geocoder.coord2RegionCode(coords.getLng(), coords.getLat(), callback);
}

function searchDetailAddrFromCoords(coords, callback) {
  // 좌표로 법정동 상세 주소 정보를 요청합니다
  return geocoder.coord2Address(coords.getLng(), coords.getLat(), callback);
}

function updateAddress(result, status) {
  if (status === kakao.maps.services.Status.OK) {
    notice.road_address = result[0].road_address ? result[0].road_address.address_name : "도로명 주소 없음";
    notice.address = result[0].address ? result[0].address.address_name : "지번 없음";
    notice.overlay.setContent(
      createContent(
        notice.details.analyzed_category_middle,
        notice.details.analyzed_at,
        notice.road_address,
        notice.address,
        notice.details.source_url
      )
    );
  }
}

// 지도 좌측상단에 지도 중심좌표에 대한 주소정보를 표출
function displayCenterInfo(result, status) {
  if (status === kakao.maps.services.Status.OK) {
    var infoDiv = document.getElementById("centerAddr");

    for (var i = 0; i < result.length; i++) {
      // 행정동의 region_type 값은 'H'
      if (result[i].region_type === "H") {
        infoDiv.innerHTML = result[i].address_name;
        break;
      }
    }
  }
}

function createContent(type, date, adress, jibun, url) {
  const wrapDiv = document.createElement("div");
  wrapDiv.classList.add("wrap");

  const infoDiv = document.createElement("div");
  infoDiv.classList.add("info");

  const titleDiv = document.createElement("div");
  titleDiv.classList.add("title");
  titleDiv.textContent = type;

  const closeDiv = document.createElement("div");
  closeDiv.classList.add("close");
  closeDiv.setAttribute("title", "닫기");
  closeDiv.addEventListener("click", closeOverlay);

  titleDiv.appendChild(closeDiv);

  const bodyDiv = document.createElement("div");
  bodyDiv.classList.add("body");

  const descDiv = document.createElement("div");
  descDiv.classList.add("desc");

  const ellipsisDiv = document.createElement("div");
  ellipsisDiv.classList.add("adress");
  ellipsisDiv.textContent = adress;

  const jibunDiv = document.createElement("div");
  jibunDiv.classList.add("jibun", "adress");
  jibunDiv.textContent = jibun;

  const dateDiv = document.createElement("div");
  dateDiv.classList.add("date");
  dateDiv.textContent = date;

  const linkAnchor = document.createElement("a");
  linkAnchor.href = url;
  linkAnchor.target = "_blank";
  linkAnchor.classList.add("link");
  linkAnchor.textContent = "게시글 바로가기";

  descDiv.appendChild(ellipsisDiv);
  descDiv.appendChild(dateDiv);
  descDiv.appendChild(linkAnchor);

  infoDiv.appendChild(titleDiv);
  infoDiv.appendChild(bodyDiv);
  bodyDiv.appendChild(descDiv);

  wrapDiv.appendChild(infoDiv);

  return wrapDiv;
}
</script>
<style>
#map {
  height: 60vh;
}
.title {
  font-weight: bold;
  display: block;
}
.hAddr {
  position: absolute;
  left: 10px;
  top: 10px;
  border-radius: 2px;
  background: #fff;
  background: rgba(255, 255, 255, 0.8);
  z-index: 1;
  padding: 5px;
}
#centerAddr {
  display: block;
  margin-top: 2px;
  font-weight: normal;
}
.bAddr {
  padding: 5px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
.wrap {
  position: absolute;
  left: 0;
  bottom: 40px;
  width: 200px;
  height: 132px;
  margin-left: -100px;
  text-align: left;
  overflow: hidden;
  font-size: 12px;
  font-family: "Malgun Gothic", dotum, "돋움", sans-serif;
  line-height: 1.5;
}
.wrap * {
  padding: 0;
  margin: 0;
}
.wrap .info {
  width: 200px;
  height: 120px;
  border-radius: 5px;
  border-bottom: 2px solid #ccc;
  border-right: 1px solid #ccc;
  overflow: hidden;
  background: #fff;
}
.wrap .info:nth-child(1) {
  border: 0;
  box-shadow: 0px 1px 2px #888;
}
.info .title {
  padding: 5px 0 0 10px;
  height: 30px;
  background: #eee;
  border-bottom: 1px solid #ddd;
  font-size: 16px;
  font-weight: bold;
  color: #888;
}
.info .close {
  position: absolute;
  top: 10px;
  right: 10px;
  color: #888;
  width: 17px;
  height: 17px;
  background: url("https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/overlay_close.png");
}
.info .close:hover {
  cursor: pointer;
}
.info .body {
  position: relative;
  overflow: hidden;
  height: 90px;
  display: flex;
  flex-direction: row;
  justify-content: center;
}
.info .desc {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.desc .adress {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 11px;
  color: #888;
}
.desc .date {
  font-size: 11px;
  color: black;
  margin-top: -2px;
}

.info .link {
  color: #5085bb;
}
</style>
