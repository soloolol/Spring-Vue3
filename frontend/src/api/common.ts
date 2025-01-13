// http://localhost:8080/manager/crime-dictionary/category
// http://localhost:8080/manager/crime-dictionary/category/{대분류}
import TerrorlessApi from "@/utils/api";

const api = new TerrorlessApi();

export const useCommonService = () => {
  async function getKakaoKey() {
    return (
      await api.call({
        url: "/kakao",
        method: "GET",
      })
    )?.data;
  }
  async function getSourceBoardList() {
    return (
      await api.call({
        url: "/collections/sourceList",
        method: "GET",
      })
    )?.data;
  }
  async function getCrimeBigCategories() {
    return (
      await api.call({
        url: "/crime-dictionary/category_big",
        method: "GET",
      })
    )?.data;
  }
  async function getCrimeMiddleCateByBig() {
    return (
      await api.call({
        url: "/crime-dictionary/category_middle",
        method: "GET",
      })
    )?.data;
  }

  return { getKakaoKey, getSourceBoardList, getCrimeBigCategories, getCrimeMiddleCateByBig };
};
