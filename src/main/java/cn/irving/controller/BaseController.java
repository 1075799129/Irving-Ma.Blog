package cn.irving.controller;

import cn.irving.constant.Types;
import cn.irving.constant.WebConst;
import cn.irving.dto.MetaDto;
import cn.irving.dto.cond.ContentCond;
import cn.irving.model.UserDomain;
import cn.irving.service.content.ContentService;
import cn.irving.service.meta.MetaService;
import cn.irving.service.site.SiteService;
import cn.irving.utils.MapCache;
import cn.irving.utils.TaleUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * Created by Irving on 2018/4/30.
 */
public abstract class BaseController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private MetaService metaService;

    @Autowired
    private SiteService siteService;


    protected MapCache cache = MapCache.single();

    public BaseController title(HttpServletRequest request, String title) {
        request.setAttribute("title", title);
        return this;
    }

    /**
     * 获取blog页面需要的公共数据
     * @param request
     * @return
     */
    public BaseController blogBaseData(HttpServletRequest request, ContentCond contentCond){
//        List<MetaDto> categories = metaService.getMetaList(Types.CATEGORY.getType(), null, WebConst.MAX_POSTS);
//        List<MetaDto> tags = metaService.getMetaList(Types.TAG.getType(), null, WebConst.MAX_POSTS);
        List<MetaDto> links = metaService.getMetaList(Types.LINK.getType(), null,WebConst.MAX_POSTS);
//        request.setAttribute("categories", categories);//分类
//        request.setAttribute("tags", tags);//标签
        request.setAttribute("links", links);
//        PageInfo<ContentDomain> recentlyArticles = contentService.getRecentlyArticle(1, 10);
//        ContentCond cond = new ContentCond();
//        cond.setType(contentCond.getType());
//        request.setAttribute("recentlyArticles", recentlyArticles);
//        List<ArchiveDto> archives = siteService.getArchivesSimple(cond);
//        request.setAttribute("archives", archives);//归档数据

        return this;
    }
    /**
     * 获取请求绑定的登录对象
     * @param request
     * @return
     */
    public UserDomain user(HttpServletRequest request) {
        return TaleUtils.getLoginUser(request);
    }

    public Integer getUid(HttpServletRequest request){
        return this.user(request).getUid();
    }

    /**
     * 数组转字符串
     *
     * @param arr
     * @return
     */
    public String join(String[] arr) {
        StringBuilder ret = new StringBuilder();
        String[] var3 = arr;
        int var4 = arr.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String item = var3[var5];
            ret.append(',').append(item);
        }

        return ret.length() > 0 ? ret.substring(1) : ret.toString();
    }
}
