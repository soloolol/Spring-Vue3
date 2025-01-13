import TerrorlessApi from "@/utils/api";

export interface Request {
  source?: string;
  status?: string;
  query?: string;
  startDate?: string;
  endDate?: string;
  page?: string;
}
const api = new TerrorlessApi();

export const useCollectService = () => {
  async function getCollections(request: Request) {
    return (
      await api.call({
        url: "/collections",
        method: "GET",
        data: request,
      })
    )?.data;
  }
  async function getCollectionById(id: String) {
    return (
      await api.call({
        url: "/collections/" + id,
        method: "GET",
      })
    )?.data;
  }
  return { getCollections, getCollectionById };
};
