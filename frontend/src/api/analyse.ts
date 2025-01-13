import TerrorlessApi from "@/utils/api";

export interface Request {
  crime_category_big?: string;
  crime_category_code?: number;
  category_code?: number;
  status?: string;
  query?: string;
  startDate?: string;
  endDate?: string;
  page?: string;
  time?: string;
  place_addr?: string;
  place_x?: number;
  place_y?: number;
}
const api = new TerrorlessApi();

export const useAnalyseService = () => {
  async function getAnalyses(request: Request) {
    return (
      await api.call({
        url: "/analyses",
        method: "GET",
        data: request,
      })
    )?.data;
  }
  async function getNewAnalyses(request: Request) {
    return (
      await api.call({
        url: "/analyses/new",
        method: "GET",
        data: request,
      })
    )?.data;
  }
  async function checkNewAnalyses() {
    return (
      await api.call({
        url: "/analyses/check",
        method: "GET",
      })
    )?.data;
  }
  async function updateReadById(id: number) {
    return (
      await api.call({
        url: "/analyses/read/" + id,
        method: "PATCH",
      })
    )?.data;
  }
  async function getAnalysesById(id: number) {
    return (
      await api.call({
        url: "/analyses/" + id,
        method: "GET",
      })
    )?.data;
  }
  async function patchAnalyses(id: number, request: Request) {
    return (
      await api.call({
        url: "/analyses/" + id,
        method: "PATCH",
        data: request,
      })
    )?.data;
  }

  return { getAnalyses, getAnalysesById, patchAnalyses, getNewAnalyses, checkNewAnalyses, updateReadById };
};
