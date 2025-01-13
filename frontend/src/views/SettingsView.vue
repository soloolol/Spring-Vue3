<template>
  <v-card class="px-9 pb-9 pt-6" color="grey-lighten-4" flat>
    <section class="section filter">
      <v-row no-gutters class="ma-2"><h4>검색 조건</h4></v-row>
      <v-card class="pa-3">
        <v-row class="justify-content-between">
          <v-col cols="6">
            <v-row no-gutters style="height: 60px">
              <v-col cols="2" class="align-content-center">
                <v-sheet class="pa-2"> 용어 </v-sheet>
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

    <section class="section board">
      <v-row no-gutters justify="space-between" class="ma-2"
        ><h4 style="line-height: 36px">용어 목록</h4>
        <v-col cols="auto" class="align-content-center">
          <v-row no-gutters class="ma-2" justify="space-between">
            <input type="file" ref="fileInput" accept=".xlsx" style="display: none" @change="handleFileUpload" />
            <v-chip v-if="file">{{ file?.name }}</v-chip>
            <v-btn
              v-if="file"
              density="compact"
              class="ma-1 text-caption"
              color="green"
              @click="uploadCheckDialog = true"
              >적용</v-btn
            >
            <v-btn density="compact" class="ma-1 text-caption" append-icon="mdi-tray-arrow-up" @click="triggerFileInput"
              >엑셀업로드
              <template v-slot:append>
                <v-icon color="success"></v-icon>
              </template>
            </v-btn>
            <form :action="downloadExcelUrl" method="get">
              <v-btn density="compact" class="ma-1 text-caption" append-icon="mdi-tray-arrow-down" type="submit"
                >엑셀다운로드
                <template v-slot:append>
                  <v-icon color="success"></v-icon> </template
              ></v-btn>
            </form>
          </v-row> </v-col
      ></v-row>
      <v-card class="pa-3">
        <v-table hover>
          <thead>
            <tr>
              <th>
                <v-checkbox hide-details v-model="isCheckAll"></v-checkbox>
              </th>
              <th v-for="header in headers" :key="header.key" :style="`width: ` + header.width">
                {{ header.title }}
              </th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="item in items">
              <td style="width: 7%">
                <v-checkbox hide-details v-model="selected" :value="item"></v-checkbox>
              </td>
              <td>{{ item.category_code ? item.category_code : "-" }}</td>
              <td>{{ item.category_big ? item.category_big : "-" }}</td>
              <td>{{ item.category_middle ? item.category_middle : "-" }}</td>
              <td>{{ item.term ? item.term : "-" }}</td>
              <td>
                <v-text-field hide-details density="compact" variant="outlined" v-model="item.synonym"></v-text-field>
              </td>
              <td>{{ item.updated_at ? item.updated_at : "-" }}</td>
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
        <v-divider />
        <v-row no-gutters justify="end">
          <v-col cols="auto">
            <v-btn class="pa-1 ma-2" color="error" @click="deleteTerm(selected)">삭제</v-btn>
            <v-btn class="pa-1 ma-2" color="blue-darken-1" @click="updateTerm(selected)">수정</v-btn>
          </v-col>
        </v-row>
      </v-card>
    </section>
  </v-card>
  <v-dialog v-model="uploadCheckDialog" max-width="400">
    <v-card text="선택한 엑셀 파일을 사전에 적용하시겠습니까? 기존 사전 내용을 덮어 씌웁니다.">
      <template v-slot:actions>
        <v-spacer></v-spacer>
        <v-btn @click="uploadCheckDialog = false"> 아니요 </v-btn>
        <v-btn color="blue" @click="uploadExcel(file)"> 예 </v-btn>
      </template>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, toRaw, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useSettingService } from "@/api/setting";
import { useCommonService } from "@/api/common";

const route = useRoute();
const router = useRouter();
const settingService = useSettingService();
const commonService = useCommonService();

defineProps<{
  bigCateList?: string[];
  middleCateAll?: object;
  middleCate?: object;
  sourceList?: string[];
  collectStatusList?: string[];
  analysisStatusList?: string[];
}>();

onMounted(async () => {
  searchAction.query = route.query.query ? route.query.query.toString() : "";
  searchAction.page = route.query.page ? parseInt(route.query.page) : 1;
  await getDictionary(toRaw(searchAction));
});

const downloadExcelUrl = import.meta.env.VITE_API_URL + "/crime-dictionary/excel";

const searchAction = reactive({
  query: "",
  page: 1,
});

const details = reactive({
  category_code: "",
  category_big: "",
  category_middle: "",
  term: "",
  synonym: "",
  created_at: "",
  updated_at: "",
});

const items = ref([]);

const isCheckAll = ref(false);
const selected = ref([]);

watch(
  () => isCheckAll.value,
  (newVal) => {
    if (newVal == true) {
      selected.value = [...items.value];
      console.log("selected", selected.value);
    } else {
      selected.value = [];
    }
  }
);

const pagination = reactive({ rowsPerPage: 10, totalItems: 0 });

const pages = computed(() => {
  if (pagination.rowsPerPage == null || pagination.totalItems == null) return 0;
  return Math.ceil(pagination.totalItems / pagination.rowsPerPage);
});

const headers = ref([
  { title: "코드", key: "category_code", width: "" },
  { title: "대분류", key: "category_big", width: "" },
  { title: "중분류", key: "category_middle", width: "" },
  { title: "용어 (대표어)", key: "term", width: "" },
  { title: "동의어", key: "synonym", width: "50%" },
  { title: "수정일", key: "updated_at", width: "" },
]);

const file = ref(null);
const fileInput = ref(null);
const uploadCheckDialog = ref(false);

const triggerFileInput = () => {
  fileInput.value.click();
};

const handleFileUpload = (event) => {
  file.value = event.target.files[0];
  if (file.value) {
    console.log("업로드된 파일:", file.value);
  }
};

function setParams(page) {
  searchAction.page = page;
  let url = route.path;
  let params = toRaw(searchAction);
  Object.keys(params).forEach((key, index) => {
    url = url + (index === 0 ? "?" : "&") + key + "=" + (params[key] === "전체" ? "" : params[key]);
  });

  router.push(url);
}

async function getDictionary(request) {
  await settingService
    .getDictionary(request)
    .then((res) => {
      items.value = res.data ? res.data : [];
      pagination.totalItems = res.total_count;
    })
    .catch((err) => console.log(err));
}

async function deleteTerm(request) {
  if (request.length == 0) {
    alert("용어를 선택해주세요");
    return;
  }
  await settingService.bulkDeleteTerm(request).then(async (res) => {
    if (res.success == true) {
      alert("삭제 되었습니다");
      router.go(0);
    } else {
      alert("삭제 실패했습니다");
    }
  });
}

async function updateTerm(request) {
  if (request.length == 0) {
    alert("용어를 선택해주세요");
    return;
  }
  await settingService.bulkUpdateTerm(request).then(async (res) => {
    if (res.success == true) {
      alert("수정 되었습니다");
      router.go(0);
    } else {
      alert("수정 실패했습니다");
    }
  });
}

async function uploadExcel(file) {
  let formData = new FormData();
  formData.append("file", file);
  await settingService.uploadExcel(formData).then((res) => {
    if (res.success == true) {
      alert("적용 되었습니다");
      router.go(0);
    } else {
      alert("적용 실패했습니다");
    }
  });
  uploadCheckDialog.value = false;
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
