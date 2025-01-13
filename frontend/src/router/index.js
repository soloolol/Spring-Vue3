import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/LoginView.vue";
import Cookies from "js-cookie";

const router = createRouter({
  history: createWebHistory(import.meta.env.VITE_BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      // component: () => import("../views/CollectView.vue"),
      meta: { requiresAuth: true },
      redirect: "/collect",
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
    },
    {
      path: "/collect",
      name: "수집내용조회",
      meta: { requiresAuth: true },
      component: () => import("../views/CollectView.vue"),
    },
    {
      path: "/analysis",
      name: "분석내용관리",
      meta: { requiresAuth: true },
      component: () => import("../views/AnalysisView.vue"),
    },
    {
      path: "/report",
      name: "제보내용관리",
      meta: { requiresAuth: true },
      component: () => import("../views/ReportView.vue"),
    },
    {
      path: "/settings",
      name: "범죄용어관리",
      meta: { requiresAuth: true },
      component: () => import("../views/SettingsView.vue"),
    },
    {
      path: "/alert",
      name: "신규예고관리",
      meta: { requiresAuth: true },
      component: () => import("../views/AlertListView.vue"),
    },
  ],
});

// 로그인 인증 부분 추가
router.beforeEach((to, from, next) => {
  const storedAuth = Cookies.get("isAuthenticated");

  if (!storedAuth && to.matched.some((record) => record.meta.requiresAuth)) {
    next({ name: "login" });
  } else {
    next();
  }
});

export default router;
