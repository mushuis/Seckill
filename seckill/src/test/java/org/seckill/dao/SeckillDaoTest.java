package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SecKill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //注入dao实现类
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() {
        long id = 1;
        SecKill secKill = seckillDao.queryById(id);
        System.out.println(secKill.getName());
    }

    @Test
    public void queryAll() {
        List<SecKill> secKills = seckillDao.queryAll(0,100);
        for(SecKill secKill:secKills){
            System.out.println(secKill.getName());
        }
    }

    @Test
    public void reduceNumber() {
        Date killTime = new Date();
        int num = seckillDao.reduceNumber(1L, killTime);
        System.out.println(num+"            ");
    }


}