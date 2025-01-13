<template>
  <v-card class="px-9 pb-9 pt-6" color="grey-lighten-4" flat>
    <section v-if="!showDetails" class="section filter">
      <v-row no-gutters class="ma-2"><h4>검색 조건</h4></v-row>
      <v-card class="pa-3">
        <v-row class="justify-content-between">
          <v-col cols="6">
            <v-row no-gutters style="height: 60px">
              <v-col cols="2" class="align-content-center px-1">
                <v-sheet class="pa-2"> 분석상태 </v-sheet>
              </v-col>
              <v-col cols="5" class="align-content-center px-1">
                <v-select
                  hide-details
                  density="compact"
                  v-model="searchAction.status"
                  :items="collectStatusList"
                  variant="outlined"
                ></v-select>
              </v-col>
            </v-row>
            <v-row no-gutters style="height: 60px">
              <v-col cols="2" class="align-content-center">
                <v-sheet class="pa-2"> 검색 </v-sheet>
              </v-col>
              <v-col cols="5" class="align-content-center px-1">
                <v-text-field
                  v-model="searchAction.query"
                  @keyup.enter="setParams(1)"
                  append-inner-icon="mdi-magnify"
                  density="compact"
                  variant="outlined"
                  hide-details
                  single-line
                  @click:append-inner="setParams(1)"
                ></v-text-field>
              </v-col>
            </v-row>
            <v-row no-gutters style="height: 60px">
              <v-col cols="2" class="align-content-center px-1">
                <v-sheet class="pa-2"> 작성일 </v-sheet>
              </v-col>
              <v-col cols="8" class="align-content-center">
                <v-row no-gutters class="justify-content-between" justify="center" align="center">
                  <v-text-field
                    :value="formattedDate(rawDateRange.start)"
                    disabled
                    density="compact"
                    variant="outlined"
                    hide-details
                    single-line
                    class="align-content-center px-1"
                  ></v-text-field>
                  <span>~</span>
                  <v-text-field
                    :value="formattedDate(rawDateRange.end)"
                    disabled
                    density="compact"
                    variant="outlined"
                    hide-details
                    single-line
                    class="align-content-center px-1"
                  ></v-text-field>
                  <v-btn icon color="grey-lighten-2" size="x-small" @click="showDatePicker = !showDatePicker">
                    <v-icon color="grey"> mdi-calendar </v-icon>
                    <v-dialog v-model="showDatePicker" width="auto"
                      ><DatePicker v-model.range="rawDateRange" mode="date" />
                      <v-btn icon class="ms-auto" text="Ok" @click="showDatePicker = false"
                        ><v-icon color="grey-lighten-1"> mdi-check </v-icon></v-btn
                      ></v-dialog
                    >
                  </v-btn>
                </v-row>
              </v-col>
            </v-row>
          </v-col>
          <v-col cols="2" class="pa-3">
            <v-row no-gutters style="height: 100%" align="end"
              ><v-col class="align-content-center px-1" style="height: 60px"
                ><v-btn style="color: #000000de; margin-left: 10px" rounded variant="tonal" @click="setParams(1)"
                  >조회</v-btn
                ></v-col
              ></v-row
            >
          </v-col>
        </v-row>
      </v-card>
    </section>

    <section v-if="!showDetails" class="section board">
      <v-row no-gutters class="ma-2" justify="space-between"><h4 style="line-height: 36px">제보 목록</h4> </v-row>
      <v-card class="pa-3">
        <v-table hover>
          <thead>
            <tr>
              <th v-for="header in headers" :key="header.key" :style="`width: ` + header.width">
                {{ header.title }}
              </th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="item in items" @click="showRowInfo(item.id)">
              <td>{{ item.id ? item.id : "-" }}</td>
              <td class="board-ellipsis">{{ item.content ? item.content : "-" }}</td>
              <td class="board-ellipsis">{{ item.admin_comment ? item.admin_comment : "-" }}</td>
              <!-- <td>{{ item.source_url ? item.source_url : "-" }}</td> -->
              <td>{{ item.reported_at ? item.reported_at : "-" }}</td>
              <td>{{ item.updated_at ? item.updated_at : "-" }}</td>
              <td>{{ item.status ? item.status : "-" }}</td>
            </tr>
          </tbody>
        </v-table>
        <div class="text-xs-center pt-2">
          <v-pagination
            density="compact"
            v-model="searchAction.page"
            @update:modelValue="setParams(searchAction.page)"
            :length="pages"
            :total-visible="10"
            ><template v-slot:next="{ onClick }"
              ><v-btn
                @click="setParams(Math.ceil(searchAction.page / 10) * 10 + 1)"
                class="v-btn v-btn--icon v-theme--light v-btn--density-compact v-btn--size-default v-btn--variant-text"
                aria-label="Next page"
                :disabled="pages < Math.ceil(searchAction.page / 10) * 10 + 1"
                variant="text"
                ><v-icon flat>mdi-chevron-right</v-icon></v-btn
              ></template
            ></v-pagination
          >
        </div>
      </v-card>
    </section>

    <section v-if="showDetails" class="section detail">
      <v-row no-gutters class="ma-2"><h4>제보내용</h4></v-row>
      <v-card class="pa-3">
        <v-table>
          <tbody>
            <tr>
              <td>분석상태</td>
              <td>
                <v-row no-gutters
                  ><v-col cols="2" class="align-content-center px-1">
                    <v-select
                      v-model="details.status"
                      hide-details
                      density="compact"
                      :items="collectStatusList.filter((element) => element !== '전체')"
                      variant="outlined"
                    ></v-select></v-col
                ></v-row>
              </td>
            </tr>
            <tr>
              <td>범죄유형</td>
              <td>
                <v-row no-gutters
                  ><v-col cols="2" class="align-content-center px-1">
                    <v-select
                      v-model="details.category_big"
                      hide-details
                      density="compact"
                      :items="['-', ...bigCateList]"
                      variant="outlined"
                      @update:modelValue="
                        middleCate[details.category_big]
                          ? (details.category_code = middleCate[details.category_big][0].category_code)
                          : (details.category_code = '')
                      "
                    ></v-select
                  ></v-col>
                  <v-col cols="2" class="align-content-center px-1">
                    <v-select
                      v-model="details.category_code"
                      hide-details
                      density="compact"
                      :disabled="details.category_big == '-' || details.category_big == null"
                      :items="middleCate[details.category_big]"
                      item-title="category_middle"
                      item-value="category_code"
                      variant="outlined"
                    ></v-select></v-col
                ></v-row>
              </td>
            </tr>
            <tr>
              <td cols="2" style="width: 15%">주소</td>

              <td>
                <v-row no-gutters
                  ><v-col cols="auto" class="align-content-center px-1 text-button">
                    <input type="date" v-model="inputDate" /> <input type="time" v-model="inputTime" /> [ 주소 :
                    {{ details.analyzed_place_addr ? details.analyzed_place_addr : "-" }} ] [ 좌표 :
                    {{ details.analyzed_place_y }}, {{ details.analyzed_place_x }}]</v-col
                  ><v-col cols="2" class="align-content-center px-1 ml-2 xsm"
                    ><v-btn variant="tonal" size="small" rounded color="medium-emphasis" @click="mapDialog = true"
                      >지도매핑</v-btn
                    ></v-col
                  ></v-row
                >
              </td>
            </tr>
          </tbody>
        </v-table>
        <!-- <v-btn @click="refDetails">목록</v-btn> -->
      </v-card>
    </section>

    <section v-if="showDetails" class="section detail">
      <v-row no-gutters class="ma-2"><h4>상세내용</h4></v-row>
      <v-card class="pa-3">
        <v-table>
          <tbody>
            <tr>
              <td cols="2" style="width: 15%">게시글 주소</td>
              <td>
                <v-row no-gutters
                  ><v-col cols="auto" class="align-content-center px-1">{{
                    details.source_url ? details.source_url : "-"
                  }}</v-col
                  ><v-col cols="2" class="align-content-center px-1 ml-2 xsm"
                    ><v-btn
                      variant="tonal"
                      size="small"
                      rounded
                      color="medium-emphasis"
                      v-if="details.source_url != null"
                      :href="details.source_url"
                      >바로가기</v-btn
                    ></v-col
                  ></v-row
                >
              </td>
            </tr>
            <tr>
              <td>작성일</td>
              <td>{{ details.reported_at ? details.reported_at : "-" }}</td>
            </tr>
            <tr>
              <td>최종 관리일</td>
              <td>{{ details.updated_at ? details.updated_at : "-" }}</td>
            </tr>
            <tr>
              <td>제보 내용</td>
              <td>{{ details.content ? details.content : "-" }}</td>
            </tr>
            <tr>
              <td>관리자 확인 내용</td>
              <td>
                <v-text-field hide-details variant="solo-filled" v-model="details.admin_comment"></v-text-field>
              </td>
            </tr>
            <tr>
              <td>첨부 이미지</td>
              <td>
                <!-- <v-row v-for="(file, idx) in details.image_files" no-gutters
                  ><v-col cols="auto" class="align-content-center px-1">{{ file }}</v-col
                  ><v-col cols="2" class="align-content-center px-1 xsm"
                    ><v-btn icon size="x-small" @click="showImage(details.image_urls[idx])"
                      ><v-icon color="blue-lighten-1"> mdi-magnify </v-icon></v-btn
                    ></v-col
                  ></v-row
                > -->
                <v-row no-gutters>
                  <v-col cols="3" class="align-content-center px-0 xsm">
                    <v-chip
                      class="mr-2"
                      density="compact"
                      v-for="(image, idx) in image_attachments.exist"
                      color="medium-emphasis"
                      prepend-icon="mdi-magnify"
                      closable
                      @click:close.native="closeChip(idx, image.name)"
                      @click.native="viewImage(image.url)"
                    >
                      {{ image.origin }}
                    </v-chip>
                  </v-col>
                  <v-col cols="4" class="align-content-center px-1 ml-2 xsm">
                    <v-file-input
                      v-model="image_attachments.new"
                      accept="image/*"
                      label=""
                      density="compact"
                      variant="underlined"
                      color="primary"
                      hide-input
                      show-size
                      chips
                      multiple
                      hide-details
                      prepend-icon="mdi-plus-box-outline"
                    ></v-file-input>
                    <!-- <v-btn variant="tonal" size="small" rounded color="blue">첨부하기</v-btn> -->
                  </v-col></v-row
                >
              </td>
            </tr>
          </tbody>
        </v-table>
        <v-divider />
        <v-row no-gutters justify="end">
          <v-col cols="auto" class="ma-2">
            <v-btn
              class="pa-1"
              color="blue"
              @click="
                modifyDetails(details.id, {
                  collect_status: details.status,
                  category_code: details.category_code,
                  place_addr: details.analyzed_place_addr,
                  place_x: details.analyzed_place_x,
                  place_y: details.analyzed_place_y,
                  admin_comment: details.admin_comment,
                  image_attachments: image_attachments.new,
                  image_dels: image_attachments.del,
                })
              "
              >수정</v-btn
            >
          </v-col>
          <v-col cols="auto" class="ma-2">
            <v-btn class="pa-1" color="grey-darken-1" @click="deleteCheckDialog = true">삭제</v-btn>
          </v-col>
          <v-col cols="auto" class="ma-2">
            <v-btn class="pa-1" @click="router.go(0)">목록</v-btn>
          </v-col>
        </v-row>
        <!-- <v-btn @click="refDetails">목록</v-btn> -->
      </v-card>
    </section>

    <v-dialog v-model="mapDialog" width="60vw">
      <MapDialog @on-close="mapDialog = false" @update:position="updatePosition" :details="details" />
    </v-dialog>

    <v-dialog v-model="imgDialog" width="60%">
      <ImgDialog @on-close="imgDialog = false" :image-url="imageUrl" />
    </v-dialog>

    <v-dialog v-model="deleteCheckDialog" max-width="400">
      <v-card text="삭제하시겠습니까?">
        <template v-slot:actions>
          <v-spacer></v-spacer>
          <v-btn @click="deleteCheckDialog = false"> 아니요 </v-btn>
          <v-btn color="blue" @click="deleteReport(details.id)"> 예 </v-btn>
        </template>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, toRaw } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useReportService } from "@/api/report";
import { useCommonService } from "@/api/common";

const route = useRoute();
const router = useRouter();
const reportService = useReportService();
const commonService = useCommonService();

defineProps<{
  bigCateList?: string[];
  middleCateAll?: object;
  middleCate?: object;
  sourceList?: string[];
  collectStatusList?: string[];
}>();

onMounted(async () => {
  // console.log("query : " + route.query.toString());
  searchAction.query = route.query.query ? route.query.query.toString() : "";
  searchAction.page = route.query.page ? parseInt(route.query.page) : 1;
  searchAction.status = route.query.status
    ? route.query.status.toString() != "전체"
      ? route.query.status.toString()
      : ""
    : "";
  searchAction.startDate = route.query.startDate ? route.query.startDate.toString() : formattedDate(amonthAgo);
  searchAction.endDate = route.query.endDate ? route.query.endDate.toString() : formattedDate(today);

  await getReports(toRaw(searchAction));

  // after search  :: api 값 => 화면 값
  setNullToAll(searchAction, "status", "전체");
  rawDateRange.value.start = parseDate(searchAction.startDate);
  rawDateRange.value.end = parseDate(searchAction.endDate);
});

const searchAction = reactive({
  query: "",
  status: "",
  startDate: "",
  endDate: "",
  page: 1,
});

const details = reactive({
  id: "",
  status: "",
  category_big: "",
  category_middle: "",
  category_code: "",
  analyzed_place_addr: "",
  source_url: "",
  content: "",
  admin_comment: "",
  image_files: "",
  image_urls: "",
  created_at: "",
  updated_at: "",
  image_file_origins: [],
  image_file_names: [],
});

const items = ref([]);
const image_attachments = reactive({ exit: [], new: [], del: [] });

const today = new Date();
const amonthAgo = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
const showDatePicker = ref(false);
const rawDateRange = ref({
  start: amonthAgo,
  end: today,
});

const mapDialog = ref(false);

const imageUrl = ref("");
const imgDialog = ref(false);

const deleteCheckDialog = ref(false);

const pagination = reactive({ rowsPerPage: 10, totalItems: 0 });

const pages = computed(() => {
  if (pagination.rowsPerPage == null || pagination.totalItems == null) return 0;
  return Math.ceil(pagination.totalItems / pagination.rowsPerPage);
});

const headers = ref([
  { title: "ID", key: "id", width: "2%" },
  { title: "내용", key: "content", width: "40%" },
  { title: "관리자 확인 내용", key: "admin_comment", width: "20%" },
  { title: "작성일", key: "report_at", width: "" },
  { title: "관리일", key: "updated_at", width: "" },
  { title: "분석상태", key: "status", width: "" },
]);

const showDetails = ref(false);

const inputDate = ref();
const inputTime = ref();

function init() {
  inputDate.value = "";
  inputTime.value = "";
  image_attachments.new = [];
  image_attachments.del = [];
}

function setParams(page) {
  searchAction.page = page;
  searchAction.startDate = formattedDate(rawDateRange.value.start);
  searchAction.endDate = formattedDate(rawDateRange.value.end);
  let url = route.path;
  let params = toRaw(searchAction);
  Object.keys(params).forEach((key, index) => {
    url = url + (index === 0 ? "?" : "&") + key + "=" + (params[key] === "전체" ? "" : params[key]);
  });

  router.push(url);
}

function updatePosition(val) {
  details.analyzed_place_addr = val.address;
  details.analyzed_place_x = val.lng;
  details.analyzed_place_y = val.lat;
}

async function getReports(query) {
  await reportService
    .getReports(query)
    .then((res) => {
      items.value = res.data ? res.data : [];
      pagination.totalItems = res.total_count;
      // console.log(items.value);
    })
    .catch((err) => console.log(err));
}

async function modifyDetails(id, request: object) {
  console.log("수정요청", request);

  let formData = new FormData();
  Object.keys(request).forEach((key) => {
    if (key == "image_attachments") {
      request[key].forEach((val) => {
        // console.log(file);
        formData.append(key, val);
      });
    } else {
      formData.append(key, request[key] ? request[key] : "");
    }
  });

  if (inputDate.value || inputTime.value) {
    if (!inputDate.value || !inputTime.value) {
      alert("수정할 날짜와 시간을 확인해 주세요");
      return;
    } else {
      Object.assign(request, { time: inputDate.value + " " + inputTime.value });
    }
  }

  await reportService
    .patchReports(id, formData)
    .then((res) => {
      if (res.success == true) {
        alert("수정 완료");
      } else {
        alert("수정 실패 다시 시도해주세요");
      }
    })
    .catch((err) => {
      alert("수정 실패 다시 시도해주세요");
    });

  init();
  await getReportById(id);
}

async function getReportById(id) {
  await reportService.getReportById(id).then((res) => {
    Object.assign(details, res);
    if (details.category_big == null) details.category_big = "-";
    if (res.image_list != []) {
      image_attachments.exist = res.image_list;
    }
    inputDate.value = res.analyzed_time?.substring(0, 10);
    inputTime.value = res.analyzed_time?.substring(11);
    return;
  });
}

async function deleteReport(id) {
  deleteCheckDialog.value = false;
  await reportService
    .deleteReportById(id)
    .then(async (res) => {
      if (res.success == true) {
        alert("삭제 완료");
        showDetails.value = false;
        router.go(0);
      } else {
        alert("삭제 실패 다시 시도해주세요");
      }
    })
    .catch((err) => {
      alert("삭제 실패 다시 시도해주세요");
    });
}

function closeChip(idx, name) {
  if (!image_attachments.del.includes(name)) {
    image_attachments.del.push(name);
  }
  console.log("image_attachments.del", image_attachments.del);
}

function viewImage(url) {
  imageUrl.value = url;
  imgDialog.value = true;
}

async function loadMiddleCate(big) {
  await commonService.getMiddleCateByBig(big);
}

function formattedDate(dateNum) {
  let date = new Date(dateNum);
  return `${date.getFullYear()}-${("0" + (date.getMonth() + 1)).slice(-2)}-${("0" + date.getDate()).slice(-2)}`;
}

async function showRowInfo(id) {
  init();
  await getReportById(id);
  showDetails.value = true;
}

function parseDate(str: string) {
  // YYYY-MM-DD
  return new Date(str.split("-")[0], str.split("-")[1] - 1, str.split("-")[2]);
}

function setNullToAll(a: object, key: string, title: string) {
  a[key] = a[key] ? a[key] : title;
}
</script>

<style scope>
.section {
  padding: 15px;
}

.board-ellipsis {
  max-width: 1px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
