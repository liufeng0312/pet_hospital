"use strict";
const common_vendor = require("../../common/vendor.js");
const api_article = require("../../api/article.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "list",
  setup(__props) {
    const loading = common_vendor.ref(true);
    const articles = common_vendor.ref([]);
    const currentCategory = common_vendor.ref("");
    const categories = [
      { label: "全部", value: "" },
      { label: "疾病预防", value: "疾病预防" },
      { label: "日常护理", value: "日常护理" },
      { label: "饮食营养", value: "饮食营养" },
      { label: "行为训练", value: "行为训练" }
    ];
    common_vendor.onMounted(async () => {
      await loadArticles();
    });
    async function loadArticles() {
      try {
        loading.value = true;
        const params = { page: 1, size: 20 };
        if (currentCategory.value) {
          params.category = currentCategory.value;
        }
        const res = await api_article.articleApi.getList(params);
        if (res.code === 200) {
          articles.value = res.data.records;
        }
      } catch (error) {
        console.error("加载文章列表失败:", error);
      } finally {
        loading.value = false;
      }
    }
    function changeCategory(category) {
      currentCategory.value = category;
      loadArticles();
    }
    function viewDetail(id) {
      common_vendor.index.navigateTo({
        url: `/pages/articles/detail?id=${id}`
      });
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.f(categories, (cat, k0, i0) => {
          return {
            a: common_vendor.t(cat.label),
            b: cat.value,
            c: currentCategory.value === cat.value ? 1 : "",
            d: common_vendor.o(($event) => changeCategory(cat.value), cat.value)
          };
        }),
        b: loading.value
      }, loading.value ? {} : articles.value.length === 0 ? {} : {
        d: common_vendor.f(articles.value, (article, k0, i0) => {
          return common_vendor.e({
            a: article.coverImage
          }, article.coverImage ? {
            b: article.coverImage
          } : {}, {
            c: common_vendor.t(article.title),
            d: article.summary
          }, article.summary ? {
            e: common_vendor.t(article.summary)
          } : {}, {
            f: common_vendor.t(article.author || "管理员"),
            g: common_vendor.t(article.viewCount || 0),
            h: article.id,
            i: common_vendor.o(($event) => viewDetail(article.id), article.id)
          });
        })
      }, {
        c: articles.value.length === 0
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-c61deb17"]]);
wx.createPage(MiniProgramPage);
