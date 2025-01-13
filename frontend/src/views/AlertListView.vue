<template>
  <v-card class="px-9 pb-9 pt-6" color="grey-lighten-4" flat>
    <section v-if="!showDetails" class="section board">
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
            <tr v-for="item in items" @click="showRowInfo($event, item.id)" :class="item.read == 0 ? 'yet' : ''">
              <td>{{ item.id != null ? item.id : "" }}</td>
              <td class="board-ellipsis">{{ item.title ? item.title : "-" }}</td>
              <td class="board-ellipsis">{{ item.content ? item.content : "-" }}</td>
              <td>{{ item.source_board ? item.source_board : item.reported ? "사용자 제보" : "-" }}</td>
              <td>{{ item.collected_at ? item.collected_at : "-" }}</td>
              <td>
                {{ item.analyzed_type_big ? item.analyzed_type_big : "-" }}-{{
                  item.analyzed_type_middle ? item.analyzed_type_middle : "-"
                }}
              </td>
              <td>{{ item.analyzed_at ? item.analyzed_at : "-" }}</td>
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

    <section v-if="showDetails" class="section result">
      <v-row no-gutters class="ma-2"><h4>분석결과</h4></v-row>
      <v-card class="pa-3">
        <v-table>
          <tbody>
            <tr>
              <td>상태</td>
              <td>
                <v-row no-gutters
                  ><v-col cols="2" class="align-content-center px-1">
                    <v-select
                      v-model="details.status"
                      hide-details
                      density="compact"
                      :items="analysisStatusList.filter((el) => el != '전체')"
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
                      v-model="details.analyzed_type_big"
                      hide-details
                      density="compact"
                      :items="['-', ...bigCateList]"
                      variant="outlined"
                      @update:modelValue="
                        middleCate[details.analyzed_type_big]
                          ? (details.analyzed_type_code = middleCate[details.analyzed_type_big][0].category_code)
                          : (details.analyzed_type_code = '')
                      "
                    ></v-select
                  ></v-col>
                  <v-col cols="2" class="align-content-center px-1">
                    <v-select
                      v-model="details.analyzed_type_code"
                      hide-details
                      density="compact"
                      :disabled="details.analyzed_type_big == '-' || details.analyzed_type_big == null"
                      :items="middleCate[details.analyzed_type_big]"
                      item-title="category_middle"
                      item-value="category_code"
                      @update:modelValue="console.log('code', details.analyzed_type_code)"
                      variant="outlined"
                    ></v-select
                  ></v-col>
                  <v-col cols="auto" class="align-content-center px-1 text-button">
                    <v-chip v-if="details.analyzed_type2" class="mx-1" density="compact" color="secondary">
                      2순위 : {{ details.analyzed_type2 }}
                    </v-chip>
                    <v-chip v-if="details.analyzed_type3" class="mx-1" density="compact" color="secondary">
                      3순위 : {{ details.analyzed_type3 }}</v-chip
                    >
                  </v-col>
                </v-row>
              </td>
            </tr>
            <tr>
              <td cols="2" style="width: 15%">추출내역</td>

              <td class="text-button" style="font-weight: 900">
                <v-row no-gutters class="align-content-center"
                  ><v-col cols="auto" class="px-2" style="color: rgba(216, 19, 22)">
                    [ 시간 :
                    <input type="date" class="mx-1" v-model="inputDate" />
                    <input type="time" class="mx-1" v-model="inputTime" /> ]
                  </v-col>
                </v-row>
                <v-row no-gutters class="align-content-center">
                  <v-col cols="auto" class="px-2" style="color: rgba(243, 142, 28)">
                    [ 주소 : {{ details.analyzed_place_addr ? details.analyzed_place_addr : "-" }} 좌표 :
                    {{ details.analyzed_place_y ? details.analyzed_place_y : "-" }},
                    {{ details.analyzed_place_x ? details.analyzed_place_x : "-" }} ]
                  </v-col>
                  <v-col cols="2" class="align-content-center ml-2 xsm">
                    <v-btn
                      size="small"
                      rounded
                      color="grey"
                      variant="outlined"
                      @click="mapDialog = true"
                      prepend-icon="mdi-map"
                    >
                      지도매핑
                    </v-btn>
                  </v-col>
                </v-row>
                <v-row no-gutters class="align-content-center">
                  <v-col cols="auto" class="px-2" style="color: rgba(58, 146, 67)">
                    <p>[ 대상 : {{ details.analyzed_target ? details.analyzed_target : "-" }} ]</p>
                  </v-col>
                </v-row>
                <v-row no-gutters class="align-content-center">
                  <v-col cols="auto" class="px-2" style="color: rgba(32, 127, 185)"
                    ><p>[ 행위 : {{ details.analyzed_action ? details.analyzed_action : "-" }} ]</p>
                  </v-col>
                </v-row>
                <v-row v-if="!details.reported" no-gutters class="align-content-center">
                  <v-col cols="auto" class="px-2" style="color: rgba(32, 127, 185)"
                    ><p>[ 수집키워드 : {{ details.collect_keyword ? details.collect_keyword : "-" }} ]</p>
                  </v-col>
                </v-row>
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
              <td cols="2" style="width: 15%">제목</td>
              <td>
                <v-row no-gutters
                  ><v-col cols="auto" class="align-content-center">{{ details.title ? details.title : "-" }}</v-col
                  ><v-col v-if="details.source_url" cols="2" class="align-content-center ml-2 xsm"
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
              <td>{{ details.writed_at ? details.writed_at : "-" }}</td>
            </tr>
            <tr>
              <td>수집일</td>
              <td>{{ details.collected_at ? details.collected_at : "-" }}</td>
            </tr>
            <tr>
              <td>분석일</td>
              <td>{{ details.analyzed_at ? details.analyzed_at : "-" }}</td>
            </tr>
            <tr>
              <td>내용</td>
              <td>{{ details.content ? details.content : "-" }}</td>
            </tr>
            <tr v-if="details.image_list?.length != 0">
              <td>첨부 이미지</td>
              <td>
                <v-row v-for="image in details.image_list" no-gutters
                  ><v-col cols="auto" class="align-content-center px-1">{{ image.origin }}</v-col
                  ><v-col cols="2" class="align-content-center px-1 xsm"
                    ><v-btn icon size="x-small" @click="goSource(image.url)"
                      ><v-icon color="blue-lighten-1"> mdi-magnify </v-icon></v-btn
                    ></v-col
                  ></v-row
                >
              </td>
            </tr>
            <tr v-if="details.ocr_result?.length != 0 && details.ocr_result != null">
              <td>OCR 추출 내용</td>
              <td>
                <v-row v-for="image in details.image_list" no-gutters>
                  <v-col cols="auto" class="align-content-center px-1">
                    <p v-for="result of details.ocr_result" class="text-button">{{ result }}</p>
                  </v-col>
                </v-row>
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
                  status: details.status,
                  category_code: details.analyzed_type_code,
                  place_addr: details.analyzed_place_addr,
                  place_x: details.analyzed_place_x,
                  place_y: details.analyzed_place_y,
                })
              "
              >수정</v-btn
            >
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
  </v-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, toRaw } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAnalyseService } from "@/api/analyse";
import { useCommonService } from "@/api/common";

const route = useRoute();
const router = useRouter();
const analyseService = useAnalyseService();
const commonService = useCommonService();

defineProps<{
  bigCateList?: string[];
  middleCateAll?: object;
  middleCate?: object;
  sourceList?: string[];
  analysisStatusList?: string[];
}>();

onMounted(async () => {
  searchAction.page = route.query.page ? parseInt(route.query.page) : 1;

  await analyseService
    .getNewAnalyses(toRaw(searchAction))
    .then((res) => {
      items.value = res.analyses ? res.analyses : [];
      pagination.totalItems = res.total_count;
      console.log(items.value);
    })
    .catch((err) => console.log(err));
});

const searchAction = reactive({
  page: 1,
});

const items = ref([]);

const showDetails = ref(false);

const mapDialog = ref(false);

const imageUrl = ref("");
const imgDialog = ref(false);

const pagination = reactive({ rowsPerPage: 10, totalItems: 0 });

const pages = computed(() => {
  if (pagination.rowsPerPage == null || pagination.totalItems == null) return 0;
  return Math.ceil(pagination.totalItems / pagination.rowsPerPage);
});

const headers = ref([
  { title: "ID", key: "id", width: "2%" },
  { title: "제목", key: "title", width: "12%" },
  { title: "내용", key: "content", width: "35%" },
  { title: "출처", key: "source_board", width: "" },
  { title: "작성일", key: "created_at", width: "" },
  { title: "범죄유형", key: "analyzed_crime_type", width: "" },
  { title: "분석일", key: "analyzed_at", width: "" },
  { title: "상태", key: "status", width: "" },
]);

const details = reactive({
  id: "",
  source_board: "",
  source_category: "",
  title: "",
  content: "",
  status: "",
  analyzed_category_big: "",
  analyzed_category_middle: "",
  analyzed_time: "",
  analyzed_place_addr: "",
  analyzed_place_x: "",
  analyzed_place_y: "",
  collected_at: "",
  analyzed_at: "",
  ocr_result: "",
  reported: 0,
});

const inputDate = ref();
const inputTime = ref();
///^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/

function initData() {
  inputDate.value = "";
  inputTime.value = "";
}

function setParams(page) {
  searchAction.page = page;

  let url = route.path;
  url = url + "?page=" + page;

  router.push(url);
}

async function modifyDetails(id, request) {
  if (request.status === "승인") {
    if (!request.category_code) {
      alert("승인을 위해 범죄유형을 선택해 주세요");
      return;
    }
  }

  if (inputDate.value || inputTime.value) {
    if (!inputDate.value || !inputTime.value) {
      alert("수정할 날짜와 시간을 확인해 주세요");
      return;
    } else {
      Object.assign(request, { time: inputDate.value + " " + inputTime.value });
    }
  }
  await analyseService.patchAnalyses(id, request).then(async (res) => {
    alert("수정성공");
    initData();
    await getAnalysesById(id);
  });
}

async function getAnalysesById(id) {
  await analyseService
    .getAnalysesById(id)
    .then((res) => {
      if (res.success) {
        Object.assign(details, res);
        inputDate.value = res.analyzed_time?.substring(0, 10);
        inputTime.value = res.analyzed_time?.substring(11);
        analyseService.updateReadById(id);
      } else {
        alert("게시물을 읽는 도중 문제가 발생했습니다 다시 시도해주세요");
        router.go(0);
      }
    })
    .catch((err) => {
      alert("서버와의 연결을 확인해주세요");
      router.go(0);
    });
}

function updatePosition(val) {
  details.analyzed_place_addr = val.address;
  details.analyzed_place_x = val.lng;
  details.analyzed_place_y = val.lat;
}

async function showRowInfo(event, id) {
  initData();
  await getAnalysesById(id);
  showDetails.value = true;
}

function goSource(url) {
  imgDialog.value = true;
  imageUrl.value = url;
}
</script>
<style scope>
.section {
  padding: 15px;
}
.board > .v-card > .v-table > .v-table__wrapper > table > thead > tr > th,
.board > .v-card > .v-table > .v-table__wrapper > table > tbody > tr > td {
  padding: 0 5px;
}
.board-ellipsis {
  max-width: 1px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.yet {
  background: beige;
}
.yet td {
  font-weight: bold;
}
</style>
