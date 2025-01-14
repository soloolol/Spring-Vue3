/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_MODE: string;
  readonly VITE_APP_TITLE: string;
  readonly VITE_BASE_URL: string;
  readonly VITE_API_URL: string;
  readonly VITE_PORT: number;
  // more env variables...
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
