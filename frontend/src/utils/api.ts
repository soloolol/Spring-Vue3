import axios, { type AxiosInstance } from "axios";

interface ApiRequest {
  url: string;
  method: string;
  data?: any;
  headers?: object;
  responseType?: string;
}

export default class TerrorlessApi {
  private client: AxiosInstance = axios.create();
  private config: {} = {};
  // private apiBaseUrl = import.meta.env.VITE_API_URL;
  private apiBaseUrl = import.meta.env.BASE_URL;
  private headers: object = { "Content-Type": "application/json" };
  private responseType: string = "json";

  private nomalizeRequestConfig(req: ApiRequest) {
    this.headers = req.headers ? req.headers : this.headers;
    this.responseType = req.responseType ? req.responseType : this.responseType;
    if (req.method === "GET" && req.data) {
      const tmp: any[] = [];
      const keys = Object.keys(req.data);
      keys.forEach((key: string) => {
        tmp.push(key + "=" + req.data[key]);
      });
      const query: string = "?" + tmp.join("&");
      return {
        url: req.url + query,
        method: req.method,
        baseURL: this.apiBaseUrl,
        headers: this.headers,
        responseType: this.responseType,
      };
    }
    return {
      url: req.url,
      method: req.method,
      baseURL: this.apiBaseUrl,
      data: req.data,
      headers: this.headers,
      responseType: this.responseType,
    };
  }

  async call(req: ApiRequest) {
    this.config = this.nomalizeRequestConfig(req);
    this.client.interceptors.response.use(undefined, () => {});

    // @ts-ignore
    return await this.client.request(this.config);
  }
}
