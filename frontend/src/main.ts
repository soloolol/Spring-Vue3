import { createApp } from "vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";

import App from "./App.vue";
import router from "./router";

// Vuetify
import "@mdi/font/css/materialdesignicons.css";
import "vuetify/styles";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

const vuetify = createVuetify({
  components,
  directives,
});

import { setupCalendar, Calendar, DatePicker } from "v-calendar";
import "v-calendar/style.css";

// Use plugin with optional defaults

// import "./assets/scss/main.scss";

const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

app.use(pinia);
app.use(router);
app.use(vuetify);
// Use plugin defaults (optional)
app.use(setupCalendar, {});

// Use the components
app.component("Calendar", Calendar);
app.component("DatePicker", DatePicker);

app.mount("#app");
