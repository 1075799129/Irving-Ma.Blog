package cn.irving.dto;

import cn.irving.model.MetaDomain;

/**
 * 标签、分类列表
 * Created by Irving on 2018/4/30.
 */
public class MetaDto extends MetaDomain {

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
