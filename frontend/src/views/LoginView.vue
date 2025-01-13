<template>
  <v-card height="100%" style="background-color: rgba(0, 0, 0, 0)">
    <v-col style="height: 87vh" class="ma-auto pa-12 pb-8 align-content-center" cols="4">
      <div class="text-subtitle-1 text-medium-emphasis">Account</div>

      <v-text-field
        v-model="username"
        density="compact"
        variant="outlined"
        placeholder="Enter your username"
      ></v-text-field>

      <div class="text-subtitle-1 text-medium-emphasis d-flex align-center justify-space-between">Password</div>

      <v-text-field
        v-model="password"
        :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
        :type="visible ? 'text' : 'password'"
        density="compact"
        placeholder="Enter your password"
        prepend-inner-icon="mdi-lock-outline"
        variant="outlined"
        @click:append-inner="visible = !visible"
      ></v-text-field>

      <v-btn @click="handleLogin" class="mb-8" color="blue" size="large" variant="tonal" block> 로그인 </v-btn>

      <p v-if="error" class="error-message">{{ error }}</p>
    </v-col>
  </v-card>
</template>

<script>
import Cookies from "js-cookie";

export default {
  data() {
    return {
      username: "",
      password: "",
      visible: false,
      error: "",
    };
  },
  methods: {
    handleLogin() {
      // 로그인 기능 추가
      this.error = "";
      if (this.username === "admin" && this.password === "1234") {
        Cookies.set("isAuthenticated", "true");
        location.href = "/";
      } else {
        this.error = "잘못된 사용자명 또는 비밀번호입니다.";
      }
    },
  },
};
</script>

<style scope>
.error-message {
  color: red;
}
</style>
