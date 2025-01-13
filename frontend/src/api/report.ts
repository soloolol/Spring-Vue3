import TerrorlessApi from "@/utils/api";

export interface Request {
  analyzed_place_addr?: string;
  category_code?: number;
  status?: string;
  collect_status?: string;
  query?: string;
  startDate?: string;
  endDate?: string;
  page?: string;
  time?: string;
  place_addr?: string;
  place_x?: number;
  place_y?: number;
  image_attachment?: File[];
  admin_comment?: string;
}
const api = new TerrorlessApi();

export const useReportService = () => {
  async function getReports(request: Request) {
    return (
      await api.call({
        url: "/reports",
        method: "GET",
        data: request,
      })
    )?.data;
  }
  async function getReportById(id: String) {
    return (
      await api.call({
        url: "/reports/" + id,
        method: "GET",
      })
    )?.data;
  }
  async function patchReports(id: String, request: Request) {
    return (
      await api.call({
        url: "/reports/" + id,
        method: "POST",
        data: request,
        headers: { "Content-Type": "multipart/form-data" },
      })
    )?.data;
  }
  async function deleteReportById(id: String) {
    return (
      await api.call({
        url: "/reports/" + id,
        method: "DELETE",
      })
    )?.data;
  }
  return { getReports, getReportById, patchReports, deleteReportById };
};
