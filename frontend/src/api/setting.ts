import TerrorlessApi from "@/utils/api";

export interface Request {
  query?: string;
  startDate?: string;
  endDate?: string;
  page?: number;
  source?: string;
  crimeType?: string;
  location?: string;
  status?: string;
  upload_file?: File;
  category_code?: number;
  term?: string;
  synonym?: string;
}

const api = new TerrorlessApi();

export const useSettingService = () => {
  async function getDictionary(request: Request) {
    return (
      await api.call({
        url: "/crime-dictionary",
        method: "GET",
        data: request,
      })
    )?.data;
  }
  async function deleteTerm(request: Request) {
    return (
      await api.call({
        url: `/crime-dictionary/${request.category_code}/${request.term}`,
        method: "DELETE",
      })
    )?.data;
  }
  async function updateTerm(request: Request) {
    return (
      await api.call({
        url: `/crime-dictionary`,
        method: "PUT",
        data: { category_code: request.category_code, term: request.term, synonym: request.synonym },
      })
    )?.data;
  }
  async function bulkDeleteTerm(request: Request[]) {
    return (
      await api.call({
        url: "/crime-dictionary/bulk",
        method: "DELETE",
        data: { request: request },
      })
    )?.data;
  }
  async function bulkUpdateTerm(request: Request[]) {
    return (
      await api.call({
        url: "/crime-dictionary/bulk",
        method: "PUT",
        data: { request: request },
      })
    )?.data;
  }
  async function downloadExcel() {
    return (
      await api.call({
        url: "/crime-dictionary/excel",
        method: "GET",
        responseType: "blob",
      })
    )?.data;
  }
  async function uploadExcel(request: Request) {
    return (
      await api.call({
        url: "/crime-dictionary/excel",
        method: "POST",
        data: request,
        headers: { "Content-Type": "multipart/form-data" },
      })
    )?.data;
  }

  return { getDictionary, deleteTerm, updateTerm, bulkDeleteTerm, bulkUpdateTerm, downloadExcel, uploadExcel };
};
