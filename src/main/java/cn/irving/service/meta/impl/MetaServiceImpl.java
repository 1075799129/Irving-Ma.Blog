package cn.irving.service.meta.impl;

import cn.irving.constant.ErrorConstant;
import cn.irving.constant.Types;
import cn.irving.constant.WebConst;
import cn.irving.dao.MetaDao;
import cn.irving.dao.RelationShipDao;
import cn.irving.dto.MetaDto;
import cn.irving.dto.cond.MetaCond;
import cn.irving.exception.BusinessException;
import cn.irving.model.ContentDomain;
import cn.irving.model.MetaDomain;
import cn.irving.model.RelationShipDomain;
import cn.irving.service.content.ContentService;
import cn.irving.service.meta.MetaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Irving on 2018/4/29.
 */
@Service
public class MetaServiceImpl implements MetaService {

    @Autowired
    private MetaDao metaDao;

    @Autowired
    private RelationShipDao relationShipDao;


    @Autowired
    private ContentService contentService;

    @Override
    @Transactional
    public void addMeta(MetaDomain meta) {
        if (null == meta)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        metaDao.addMeta(meta);

    }

    @Override
    public void saveMeta(String type, String name, Integer mid) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)){
            MetaCond metaCond = new MetaCond();
            metaCond.setName(name);
            metaCond.setType(type);
            List<MetaDomain> metas = metaDao.getMetasByCond(metaCond);
            if (null == metas || metas.size() == 0){
                MetaDomain metaDomain = new MetaDomain();
                metaDomain.setName(name);
                if (null != mid){
                    MetaDomain meta = metaDao.getMetaById(mid);
                    if (null != meta)
                        metaDomain.setMid(mid);

                    metaDao.updateMeta(metaDomain);
                    //更新原有的文章分类
                    contentService.updateCategory(meta.getName(), name);
                } else {
                    metaDomain.setType(type);
                    metaDao.addMeta(metaDomain);
                }
            } else {
                throw BusinessException.withErrorCode(ErrorConstant.Meta.META_IS_EXIST);

            }

        }
    }

    @Override
    @Transactional
    public void addMetas(Integer cid, String names, String type) {
        if (null == cid)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);

        if (StringUtils.isNotBlank(names) && StringUtils.isNotBlank(type)) {
            String[] nameArr = StringUtils.split(names, ",");
            for (String name : nameArr) {
                this.saveOrUpdate(cid, name, type);
            }
        }
    }

    @Override
    public void saveOrUpdate(Integer cid, String name, String type) {
        MetaCond metaCond = new MetaCond();
        metaCond.setName(name);
        metaCond.setType(type);
        List<MetaDomain> metas = this.getMetas(metaCond);

        int mid;
        MetaDomain metaDomain;
        if (metas.size() == 1){
            MetaDomain meta = metas.get(0);
            mid = meta.getMid();
        }else if (metas.size() > 1){
            throw BusinessException.withErrorCode(ErrorConstant.Meta.NOT_ONE_RESULT);
        } else {
            metaDomain = new MetaDomain();
            metaDomain.setSlug(name);
            metaDomain.setName(name);
            metaDomain.setType(type);
            this.addMeta(metaDomain);
            mid = metaDomain.getMid();
        }
        if (mid != 0){
            Long count = relationShipDao.getCountById(cid, mid);
            if (count == 0){
                RelationShipDomain relationShip = new RelationShipDomain();
                relationShip.setCid(cid);
                relationShip.setMid(mid);
                relationShipDao.addRelationShip(relationShip);
            }

        }
    }

    @Override
    @Transactional
    public void deleteMetaById(Integer mid) {
        if (null == mid)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);

        MetaDomain meta = metaDao.getMetaById(mid);
        if (null != meta){
            String type = meta.getType();
            String name = meta.getName();
            metaDao.deleteMetaById(mid);
            //需要把相关的数据删除
            List<RelationShipDomain> relationShips = relationShipDao.getRelationShipByMid(mid);
            if (null != relationShips && relationShips.size() > 0){
                for (RelationShipDomain relationShip : relationShips) {
                    ContentDomain article = contentService.getAtricleById(relationShip.getCid());
                    if (null != article){
                        ContentDomain temp = new ContentDomain();
                        temp.setCid(relationShip.getCid());
                        if (type.equals(Types.CATEGORY.getType())) {
                            temp.setCategories(reMeta(name, article.getCategories()));
                        }
                        if (type.equals(Types.TAG.getType())) {
                            temp.setTags(reMeta(name, article.getTags()));
                        }
                        //将删除的资源去除
                        contentService.updateArticleById(temp);
                    }
                }
                relationShipDao.deleteRelationShipByMid(mid);
            }
        }



    }

    @Override
    @Transactional
    public void updateMeta(MetaDomain meta) {
        if (null == meta || null == meta.getMid())
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        metaDao.updateMeta(meta);

    }

    @Override
    public MetaDomain getMetaById(Integer mid) {
        if (null == mid)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        return metaDao.getMetaById(mid);
    }

    @Override
    public List<MetaDomain> getMetas(MetaCond metaCond) {
        return metaDao.getMetasByCond(metaCond);
    }


    @Override
    public List<MetaDto> getMetaList(String type, String orderby, int limit) {
        if (StringUtils.isNotBlank(type)){
            if (StringUtils.isBlank(orderby)) {
                orderby = "count desc, a.mid desc";
            }
            if (limit < 1 || limit > WebConst.MAX_POSTS) {
                limit = 10;
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("type", type);
            paraMap.put("order", orderby);
            paraMap.put("limit", limit);
            return metaDao.selectFromSql(paraMap);
        }
        return null;
    }

    private String reMeta(String name, String metas) {
        String[] ms = StringUtils.split(metas, ",");
        StringBuilder sbuf = new StringBuilder();
        for (String m : ms) {
            if (!name.equals(m)) {
                sbuf.append(",").append(m);
            }
        }
        if (sbuf.length() > 0) {
            return sbuf.substring(1);
        }
        return "";
    }
}
