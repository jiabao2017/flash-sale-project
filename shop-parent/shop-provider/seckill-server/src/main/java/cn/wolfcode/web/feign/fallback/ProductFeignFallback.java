package cn.wolfcode.web.feign.fallback;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.Product;
import cn.wolfcode.web.feign.ProductFeignApi;
import feign.hystrix.FallbackFactory;

import java.util.List;

/**
 * @Author 李家宝
 * @Date 2023/12/18-11:02
 * @Description //TODO
 **/
public class ProductFeignFallback implements FallbackFactory<ProductFeignApi> {
    @Override
    public ProductFeignApi create(Throwable throwable) {
        return new ProductFeignApi(){
            @Override
            public Result<List<Product>> queryByIds(List<Long> ids) {
                return null;
            }
        };
    }
}
