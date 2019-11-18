package org.seckill.service;


import org.apache.ibatis.annotations.Param;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.SecKill;
import org.seckill.exception.RepeatkillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/*
* 业务接口：站在“使用者”的角度
* 三个方面：方法定义粒度，参数，返回类型（return 类型/异常）
*
* */
public interface SeckillService {

    List<SecKill> getSeckillList();

    SecKill getById(long seckillId);


    /*
    * 秒杀开始输出秒杀地址和秒杀接口
    * 秒杀未开始输出当前时间和秒杀开始时间
    * 返回值只能写void否则返回两个类型没法判断。
    * service层关注的是业务
    * */
    Exposer exportSeckillUrl(long seckillId);
    //md5唯一标识，如果md5发生改变证明用户跳出秒杀界面
    SeckillExecution executeSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone,@Param("md5") String md5)
        throws SeckillException, RepeatkillException, SeckillCloseException;

}
