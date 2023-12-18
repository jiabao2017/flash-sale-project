package cn.wolfcode.service;

import cn.wolfcode.domain.Product;
import cn.wolfcode.domain.SeckillProduct;
import cn.wolfcode.domain.SeckillProductVo;

import java.util.List;

/**
 * Created by lanxw
 */
public interface ISeckillProductService {
    List<SeckillProductVo> queryByTime(Integer time);

    SeckillProductVo find(Integer time, Long seckillId);

}
