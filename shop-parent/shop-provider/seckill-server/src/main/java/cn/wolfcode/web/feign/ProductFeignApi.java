package cn.wolfcode.web.feign;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.Product;
import cn.wolfcode.web.feign.fallback.ProductFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author 李家宝
 * @Date 2023/12/18-10:56
 * @Description //TODO
 **/
@FeignClient(name = "product-service",fallbackFactory = ProductFeignFallback.class)
public interface ProductFeignApi {
    @RequestMapping("/product/queryByIds")
    Result<List<Product>> queryByIds(@RequestParam List<Long> productIds);
}
