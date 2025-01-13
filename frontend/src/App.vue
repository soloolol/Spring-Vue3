<template>
  <v-app style="background-color: #f5f5f5">
    <SideBar :unread="unread" />
    <v-app-bar
      class="px-9"
      density="compact"
      scroll-behavior="elevate"
      color="#292c45"
      image="/src/assets/milad-fakurian-zcyxRCsKAx4-unsplash.jpg"
    >
      <v-app-bar-title>{{ title }}</v-app-bar-title>
      <v-spacer></v-spacer>
      <v-breadcrumbs :items="breadcrumbs">
        <template v-slot:prepend>
          <v-icon icon="mdi-home" size="small"></v-icon>
        </template>
        <template v-slot:divider>
          <v-icon icon="mdi-chevron-right"></v-icon>
        </template>
      </v-breadcrumbs>
    </v-app-bar>
    <v-main>
      <RouterView
        :key="$route.fullPath"
        :big-cate-list="bigCateList"
        :middle-cate-all="middleCateAll"
        :middle-cate="middleCate"
        :collect-status-list="collectStatusList"
        :analysis-status-list="analysisStatusList"
        :source-list="sourceList"
      />
      <!-- <footer class="pt-9">
        <v-row class="align-center justify-center">
          <div class="text-overline">Mayfarmsoft</div>
          ©
          <span class="text-caption">2024</span>
        </v-row>
      </footer> -->
    </v-main>
    <v-snackbar
      v-model="snackbar"
      vertical
      location="left bottom"
      timeout="-1"
      @click="
        snackbar = false;
        $router.push('/alert');
      "
    >
      <div class="text-subtitle-1 pb-2">신규 예고 알림 <v-icon>mdi-bell</v-icon></div>

      <p>새로운 예고가 {{ diffCount }}건 있습니다</p>

      <template v-slot:actions>
        <v-btn color="red" variant="text" @click.stop="snackbar = false"> Close </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script setup lang="ts">
import { useRoute, useRouter, RouterView } from "vue-router";
import { ref, onMounted, watch, computed } from "vue";
import { useCommonService } from "@/api/common";
import { useAnalyseService } from "@/api/analyse";

const route = useRoute();
const router = useRouter();
const commonService = useCommonService();
const analyseService = useAnalyseService();

const bigCateList = ref([]);
const middleCateAll = ref({});
const middleCate = ref([]);

const sourceList = ref(["전체", "디시인사이드", "루리웹", "와이고수", "트위터"]);
const analysisStatusList = ref(["전체", "보류", "무시", "승인", "대기"]);
const collectStatusList = ref(["전체", "분석결과있음", "분석결과없음", "대기", "실패"]);

onMounted(async () => {
  // console.log(route.name);
  title.value = route.name;

  await commonService
    .getSourceBoardList()
    .then((res) => {
      sourceList.value = ["전체", ...res.data];
    })
    .catch((err) => {
      console.log(err);
    });

  await commonService
    .getCrimeBigCategories()
    .then((res) => {
      bigCateList.value = res.data;
    })
    .catch((err) => {
      console.log(err);
    });

  await commonService.getCrimeMiddleCateByBig().then((res) => {
    middleCateAll.value = arrangeMiddle(bigCateList.value, res.data, true);
    middleCate.value = arrangeMiddle(bigCateList.value, res.data, false);
  });

  await checkNew();
  setInterval(checkNew, 10000);
});

const title = ref("");
const ids = ref(null);
const unread = ref(null);
const diffCount = ref();
const snackbar = ref(false);

//breadcrumb title handle
watch(
  () => route.name,
  (newName) => {
    title.value = newName;
  }
);

//새로운 아이디 있으면 알림
watch(
  () => ids.value,
  (newV, oldV) => {
    // console.log("newV", newV, "oldV", oldV);
    if (oldV != null && !equalsIgnoreOrder(newV, oldV)) {
      let dif = newV.filter((x) => !oldV.includes(x));
      snackbar.value = false;
      if (dif.length > 0) {
        diffCount.value = dif.length;
        snackbar.value = true;
      }
    }
  }
);

const breadcrumbs = computed(() => {
  return [
    {
      title: "",
      disabled: false,
      href: "/",
    },
    {
      title: route.name,
      disabled: true,
      href: route.path,
    },
  ];
});

async function checkNew() {
  await analyseService.checkNewAnalyses().then((res) => {
    if (res.success) {
      ids.value = res.data[0] ? res.data[0].ids.split(",") : [];
      unread.value = res.data[0] ? res.data[0].unread : 0;
    }
  });
}

const equalsIgnoreOrder = (a, b) => {
  if (a.length !== b.length) return false;
  const uniqueValues = new Set([...a, ...b]);
  for (const v of uniqueValues) {
    const aCount = a.filter((e) => e === v).length;
    const bCount = b.filter((e) => e === v).length;
    if (aCount !== bCount) return false;
  }
  return true;
};

function arrangeMiddle(keyList, middleList, isSearch: boolean) {
  let all = [];
  if (isSearch) {
    all = [{ category_middle: "전체", category_code: "" }];
  }
  let temp = keyList.reduce((acc, key) => {
    const foundObjects = middleList.filter((obj) => obj.category_big === key).map(({ key, ...rest }) => rest);
    if (foundObjects.length > 0) {
      acc[key] = foundObjects;
      if (isSearch) {
        acc[key] = [{ category_middle: "전체", category_code: "" }, ...foundObjects];
      }
      all = [...all, ...foundObjects];
    }
    return acc;
  }, {});
  if (isSearch) {
    temp["전체"] = all;
  }
  return temp;
}
</script>

<style>
.main-content::-webkit-scrollbar {
  display: none;
}
</style>
