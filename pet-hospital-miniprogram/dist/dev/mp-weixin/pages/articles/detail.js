"use strict";
const common_vendor = require("../../common/vendor.js");
const api_article = require("../../api/article.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "detail",
  setup(__props) {
    const loading = common_vendor.ref(true);
    const article = common_vendor.ref(null);
    const articleId = common_vendor.ref(0);
    common_vendor.onMounted(async () => {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      articleId.value = parseInt(currentPage.options.id);
      console.log("文章ID:", articleId.value);
      await loadDetail();
    });
    async function loadDetail() {
      try {
        loading.value = true;
        console.log("正在加载文章详情，ID:", articleId.value);
        const res = await api_article.articleApi.getDetail(articleId.value);
        console.log("API响应:", res);
        if (res.code === 200) {
          article.value = res.data;
          console.log("文章数据:", article.value);
        } else {
          common_vendor.index.showToast({
            title: res.msg || "加载失败",
            icon: "none"
          });
        }
      } catch (error) {
        console.error("加载文章详情失败:", error);
        common_vendor.index.showToast({
          title: "加载失败，请重试",
          icon: "none"
        });
      } finally {
        loading.value = false;
      }
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: loading.value
      }, loading.value ? {} : article.value ? common_vendor.e({
        c: common_vendor.t(article.value.title),
        d: common_vendor.t(article.value.author || "管理员"),
        e: common_vendor.t(article.value.publishedAt || article.value.createdAt),
        f: common_vendor.t(article.value.viewCount),
        g: article.value.coverImage
      }, article.value.coverImage ? {
        h: article.value.coverImage
      } : {}, {
        i: article.value.content
      }) : {}, {
        b: article.value
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-85805058"]]);
wx.createPage(MiniProgramPage);
