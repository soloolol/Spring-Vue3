<template>
  <v-card class="px-9 pb-9 pt-6" color="grey-lighten-4" flat>
    <section v-if="!showDetails" class="section filter">
      <v-row no-gutters class="ma-2"><h4>검색 조건</h4></v-row>
      <v-card class="pa-3">
        <v-row class="justify-content-between">
          <v-col cols="6">
            <v-row no-gutters style="height: 60px">
              <v-col cols="3" class="align-content-center px-1">
                <v-sheet class="pa-2"> 출처</v-sheet>
              </v-col>
              <v-col cols="5" class="align-content-center px-1">
                <v-select
                  v-model="searchAction.source"
                  hide-details
                  density="compact"
                  :items="sourceList"
                  variant="outlined"
                ></v-select>
              </v-col>
            </v-row>
            <v-row no-gutters style="height: 60px">
              <v-col cols="3" class="align-content-center px-1">
                <v-sheet class="pa-2"> 상태 </v-sheet>
              </v-col>
              <v-col cols="5" class="align-content-center px-1">
                <v-select
                  v-model="searchAction.status"
                  hide-details
                  density="compact"
                  :items="collectStatusList"
                  variant="outlined"
                ></v-select>
              </v-col>
            </v-row>
            <v-row no-gutters style="height: 60px">
              <v-col cols="3" class="align-content-center px-1">
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
              <v-col cols="3" class="align-content-center px-1">
                <v-sheet class="pa-2"> 작성일|수집일 </v-sheet>
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
      <v-row no-gutters class="ma-2" justify="space-between"
        ><h4 style="line-height: 36px">수집 목록</h4>
        <v-col cols="auto" class="align-content-center">
          <v-row no-gutters class="ma-2" justify="space-between">
            <v-btn
              density="compact"
              class="ma-1 text-caption"
              append-icon="mdi-tray-arrow-down"
              :href="downloadExcelUrl"
              >엑셀다운로드
              <template v-slot:append>
                <v-icon color="success"></v-icon>
              </template>
            </v-btn>
          </v-row> </v-col
      ></v-row>
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
            <tr
              v-for="item in items"
              @click="
                getCollectionById(item.id);
                showDetails = true;
              "
            >
              <td>{{ item.id ? item.id : "" }}</td>
              <td class="board-ellipsis">{{ item.title ? item.title : "-" }}</td>
              <td class="board-ellipsis">{{ item.content ? item.content : "-" }}</td>
              <td>{{ item.source_board ? item.source_board : "-" }}</td>
              <td>{{ item.writed_at ? item.writed_at : "-" }}</td>
              <td>{{ item.created_at ? item.created_at : "-" }}</td>
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
      <v-row no-gutters class="ma-2"><h4>상세내용</h4></v-row>
      <v-card class="pa-3">
        <v-table>
          <tbody>
            <tr>
              <td style="width: 15%">제목</td>
              <td>
                <v-row no-gutters
                  ><v-col cols="auto" class="align-content-center px-1">{{ details.title ? details.title : "-" }}</v-col
                  ><v-col cols="2" class="align-content-center px-1 ml-2 xsm"
                    ><v-btn
                      variant="tonal"
                      size="small"
                      rounded
                      color="medium-emphasis"
                      v-if="details.source_url != null"
                      target="_blank"
                      :href="details.source_url"
                      >바로가기</v-btn
                    ></v-col
                  ></v-row
                >
              </td>
            </tr>
            <tr>
              <td>상태</td>
              <td>{{ details.status ? details.status : "-" }}</td>
            </tr>
            <tr>
              <td>작성자</td>
              <td>{{ details.writer ? details.writer : "-" }}</td>
            </tr>
            <tr>
              <td>수집출처</td>
              <td>{{ details.source_board ? details.source_board : "-" }}</td>
            </tr>
            <tr>
              <td>수집키워드</td>
              <td>{{ details.collect_keyword ? details.collect_keyword : "-" }}</td>
            </tr>
            <tr>
              <td>작성일</td>
              <td>{{ details.writed_at ? details.writed_at : "-" }}</td>
            </tr>
            <tr>
              <td>수집시간</td>
              <td>{{ details.created_at ? details.created_at : "-" }}</td>
            </tr>
            <tr>
              <td>내용</td>
              <td>
                <p>{{ details.content ? details.content : "-" }}</p>
              </td>
            </tr>
            <tr v-if="details.image_files?.length != 0">
              <td>첨부 이미지</td>
              <td>
                <v-row v-for="(file, idx) in details.image_files" no-gutters
                  ><v-col cols="3" class="align-content-center px-1">{{ file }}</v-col
                  ><v-col cols="2" class="align-content-center px-1 xsm"
                    ><v-btn icon size="x-small" :href="details.image_urls[idx]" target="_blank"
                      ><v-icon color="blue-lighten-1"> mdi-magnify </v-icon></v-btn
                    ></v-col
                  ></v-row
                >
              </td>
            </tr>
          </tbody>
        </v-table>
        <v-divider />
        <v-row no-gutters justify="end">
          <v-col cols="auto" class="ma-2">
            <v-btn class="pa-1" @click="router.go(0)">목록</v-btn>
          </v-col>
        </v-row>
      </v-card>
    </section>
    <v-dialog v-model="imgDialog" width="60%">
      <ImgDialog :image-url="imageUrl" @onClose="imgDialog = false" />
    </v-dialog>
  </v-card>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted, computed, toRaw } from "vue";
import { useCollectService } from "@/api/collect";
import { useRoute, useRouter } from "vue-router";

const collectService = useCollectService();
const route = useRoute();
const router = useRouter();

defineProps<{
  sourceList?: string[];
  collectStatusList?: string[];
}>();

onMounted(async () => {
  searchAction.query = route.query.query ? route.query.query.toString() : "";
  searchAction.source = route.query.source ? route.query.source.toString() : "";
  searchAction.page = route.query.page ? parseInt(route.query.page) : 1;
  searchAction.status = route.query.status ? route.query.status.toString() : "";
  searchAction.source = route.query.source
    ? route.query.source.toString() != "전체"
      ? route.query.source.toString()
      : ""
    : "";
  searchAction.status = route.query.status
    ? route.query.status.toString() != "전체"
      ? route.query.status.toString()
      : ""
    : "";
  searchAction.startDate = route.query.startDate ? route.query.startDate.toString() : formattedDate(amonthAgo);
  searchAction.endDate = route.query.endDate ? route.query.endDate.toString() : formattedDate(today);

  // search
  await getCollections(toRaw(searchAction));

  // after search  :: api 값 => 화면 값
  setNullToAll(searchAction, "source", "전체");
  setNullToAll(searchAction, "status", "전체");
  rawDateRange.value.start = parseDate(searchAction.startDate);
  rawDateRange.value.end = parseDate(searchAction.endDate);

  downloadExcelUrl.value = import.meta.env.VITE_API_URL + "/collections/excel" + window.location.search;
});

const searchAction = reactive({ query: "", source: "", status: "", startDate: "", endDate: "", page: 1 });

const today = new Date();
const amonthAgo = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());

const showDatePicker = ref(false);

const rawDateRange = ref({
  start: amonthAgo,
  end: today,
});

const imageUrl = ref("");
const imgDialog = ref(false);

const headers = ref([
  { title: "ID", key: "id", width: "2%" },
  { title: "제목", key: "title", width: "15%" },
  { title: "내용", key: "content", width: "40%" },
  { title: "출처", key: "source_board", width: "" },
  { title: "작성일", key: "writed_at", width: "" },
  { title: "수집일", key: "created_at", width: "" },
  { title: "상태", key: "status", width: "" },
]);

const showDetails = ref(false);
const details = ref({});

const items = ref([]);

const pagination = reactive({ rowsPerPage: 10, totalItems: 0 });

const pages = computed(() => {
  if (pagination.rowsPerPage == null || pagination.totalItems == null) return 0;
  return Math.ceil(pagination.totalItems / pagination.rowsPerPage);
});

const downloadExcelUrl = ref("");

function parseDate(str: string) {
  // YYYY-MM-DD
  return new Date(str.split("-")[0], str.split("-")[1] - 1, str.split("-")[2]);
}

function setNullToAll(a: object, key: string, title: string) {
  a[key] = a[key] ? a[key] : title;
}

function formattedDate(dateNum) {
  let date = new Date(dateNum);
  return `${date.getFullYear()}-${("0" + (date.getMonth() + 1)).slice(-2)}-${("0" + date.getDate()).slice(-2)}`;
}

function setParams(page) {
  searchAction.page = page;
  searchAction.startDate = formattedDate(rawDateRange.value.start);
  searchAction.endDate = formattedDate(rawDateRange.value.end);
  let url = route.path;
  let params = toRaw(searchAction);
  Object.keys(params).forEach((key, index) => {
    url = url + (index === 0 ? "?" : "&") + key + "=" + params[key];
  });
  router.push(url);
}

async function getCollectionById(id) {
  await collectService.getCollectionById(id).then((res) => {
    details.value = Object.assign({}, res);
  });
}

function goSource(url) {
  imgDialog.value = true;
  imageUrl.value = url;
}

async function getCollections(request) {
  await collectService
    .getCollections(request)
    .then((res) => {
      items.value = res.data ? res.data : [];
      pagination.totalItems = res.total_count;
    })
    .catch((err) => console.log(err));
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
</style>
