<template>
  <v-navigation-drawer id="sidebar" location="left" permanent>
    <v-list-item lines="two">
      <a href="/" rel="home" title="서울연구원 범죄예고 신고지원">
        <img style="height: 50px" src="@/assets/si_logo.png" alt="서울연구원로고" />
      </a>
    </v-list-item>

    <v-divider></v-divider>

    <v-list nav>
      <v-list-item prepend-icon="mdi-layers-search" title="수집내용조회" @click="index('collect')"></v-list-item>
      <v-list-item prepend-icon="mdi-view-dashboard-edit" title="분석내용관리" @click="index('analysis')"></v-list-item>
      <v-list-item prepend-icon="mdi-account-group-outline" title="제보내용관리" @click="index('report')"></v-list-item>
      <v-list-item prepend-icon="mdi-cog" title="범죄용어관리" @click="index('settings')"></v-list-item>
      <v-list-item prepend-icon="mdi-bell-outline" title="신규예고알림" @click="index('alert')">
        <template v-slot:append>
          <v-badge v-if="unread" color="error" :content="unread" inline></v-badge>
        </template>
      </v-list-item>
    </v-list>
    <button class="login-btn" v-if="isLogin" @click="handleLogout">로그아웃</button>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import { defineProps } from "vue";
import { useRouter } from "vue-router";
import Cookies from "js-cookie";

defineProps<{
  unread?: number;
}>();

const router = useRouter();
// const store = useStore();
let isLogin = Cookies.get("isAuthenticated") === "true";

const handleLogout = () => {
  // 로그아웃 기능 추가
  alert("로그아웃 완료");
  Cookies.remove("isAuthenticated");
  isLogin = false;
  location.href = "login";
};
const index = (v) => {
  let url = location.origin + import.meta.env.VITE_BASE_URL + "/" + v;
  location.href = url;
};
</script>

<style>
.login-btn {
  margin: 20px;
  font-size: 12px;
  position: absolute;
  bottom: 0;
}
</style>
