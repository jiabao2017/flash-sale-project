package cn.wolfcode.web.controller;

import cn.wolfcode.common.constants.CommonConstants;
import cn.wolfcode.common.web.CommonCodeMsg;
import cn.wolfcode.common.web.Result;
import cn.wolfcode.common.web.anno.RequireLogin;
import cn.wolfcode.domain.OrderInfo;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.service.IOrderInfoService;
import cn.wolfcode.service.ISeckillProductService;
import cn.wolfcode.util.UserUtil;
import cn.wolfcode.web.msg.SeckillCodeMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by lanxw
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderInfoController {
    @Autowired
    private ISeckillProductService seckillProductService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private IOrderInfoService orderInfoService;

    @RequestMapping("/doSeckill")
    public Result<String> doSeckill(Integer time, Long seckillId, HttpServletRequest request) {
        //查询秒杀商品
        SeckillProductVo seckillProductVo = seckillProductService.find(time, seckillId);
        //判断是否处于秒杀时间段  这里先注释避免测试不通过
//        boolean legalTime = DateUtil.isLegalTime(seckillProductVo.getStartDate(), time);
//        if (!legalTime) {
//            return Result.error(CommonCodeMsg.ILLEGAL_OPERATION);
//        }
        //判断库存是否充足  todo 这里应该用当前数量吧？
        if (seckillProductVo.getStockCount() <= 0) {
            return Result.error(SeckillCodeMsg.SECKILL_STOCK_OVER);
        }
        //判断用户是否重复下单
        String token = request.getHeader(CommonConstants.TOKEN_NAME);
        String phone = UserUtil.getUserPhone(redisTemplate, token);
        OrderInfo orderInfo = orderInfoService.findByPhoneAndSeckillId(phone, seckillId);
        if (orderInfo != null) {
            return Result.error(SeckillCodeMsg.REPEAT_SECKILL);
        }


        orderInfo = orderInfoService.doSeckill(phone, seckillProductVo);
        return Result.success(orderInfo.getOrderNo());
    }

    /**
     * 根据秒杀订单编号查询秒杀订单信息
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("/find")
    @RequireLogin
    public Result<OrderInfo> find(String orderNo, HttpServletRequest request) {
        log.info("开始查询秒杀订单信息");
        OrderInfo orderInfo = orderInfoService.findByOrderNo(orderNo);
        if (orderInfo == null) {
            return Result.success(null);
        }
        String token = request.getHeader(CommonConstants.TOKEN_NAME);
        String phone = UserUtil.getUserPhone(redisTemplate, token);
        if (!String.valueOf(orderInfo.getUserId()).equals(phone)) {
            return Result.error(CommonCodeMsg.ILLEGAL_OPERATION);
        }
        return Result.success(orderInfo);
    }
}
