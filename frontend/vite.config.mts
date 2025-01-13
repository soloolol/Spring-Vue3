/* eslint-disable @typescript-eslint/no-unused-vars */
import { fileURLToPath, URL } from "node:url";

import { defineConfig, loadEnv } from "vite";
import Components from "unplugin-vue-components/vite";
import Vue from "@vitejs/plugin-vue";
import Vuetify, { transformAssetUrls } from "vite-plugin-vuetify";
import ViteFonts from "unplugin-fonts/vite";
// import vueJsx from "@vitejs/plugin-vue-jsx";

// https://github.com/vuetifyjs/vuetify-loader/tree/next/packages/vite-plugin

// @ts-ignore
export default defineConfig(({ command, mode }) => {
  // 현재 작업 디렉터리의 `mode`를 기반으로 env 파일을 불러옴
  // 세 번째 매개변수를 ''로 설정하면 `VITE_` 접두사에 관계없이 모든 환경 변수를 불러옴
  const env = loadEnv(mode, process.cwd(), "");

  return {
    plugins: [
      Vue({
        template: { transformAssetUrls },
      }),
      // https://github.com/vuetifyjs/vuetify-loader/tree/master/packages/vite-plugin#readme
      Vuetify(),
      Components(),
      ViteFonts({
        google: {
          families: [
            {
              name: "Roboto",
              styles: "wght@100;300;400;500;700;900",
            },
          ],
        },
      }),
    ],
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url)),
      },
    },
    server: {
      proxy: {
        [`${env.VITE_BASE_URL}/api`]: {
          target: `${env.VITE_API_URL}`,
          changeOrigin: true,
          rewrite: (path) => path.replace(/\S+\/api/, ""),
        },
      },
    },
    transpileDependencies: true,
    filenameHashing: false, // build 파일 해싱 제거
    productionSourceMap: true, // js source map 생성
    css: { sourceMap: true },
    build: {
      outDir: "../src/main/resources/static",
      // assetsDir: "./assets",
      emptyOutDir: true,
    },
    define: {
      __APP_ENV__: JSON.stringify(env.APP_ENV),
    },
  };
});
// https://vitejs.dev/config/
