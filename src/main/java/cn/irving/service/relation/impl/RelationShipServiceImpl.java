package cn.irving.service.relation.impl;

import cn.irving.dao.RelationShipDao;
import cn.irving.service.relation.RelationShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Irving on 2018/4/30.
 */
@Service
public class RelationShipServiceImpl implements RelationShipService {

    @Autowired
    private RelationShipDao relationShipDao;


}
