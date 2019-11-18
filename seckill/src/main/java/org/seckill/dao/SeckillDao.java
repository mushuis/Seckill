package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SecKill;

import java.util.Date;
import java.util.List;

public interface SeckillDao {

    /*减库存的方法*/
    int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);
    /*通过id查询*/
    SecKill queryById(long seckillId);
    /*通过条件清单，限制条件查询链表*/
    List<SecKill> queryAll(@Param("offset") int offset,@Param("limit") int limit);
}
