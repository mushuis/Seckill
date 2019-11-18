package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.SecKill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatkillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger =  LoggerFactory.getLogger(this.getClass());

    @Autowired//注入service依赖
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;

    private final String slat = "1234567890";

    @Override
    public List<SecKill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public SecKill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        SecKill secKill = seckillDao.queryById(seckillId);
        if(secKill == null){
            return new Exposer(false, seckillId);
        }
        Date startTime = secKill.getStartTime();
        Date endTime = secKill.getEndTime();
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false, seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }
        //转换特定字符串
        String md5 = this.getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    private String getMD5(long seckillId){
        String base = seckillId + "" +slat;
//        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
//        return md5;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }




    /*@Transactional*/
    /*
    * 使用注解控制事务方法的优点
    * 1：开发团队达成一直约定，明确标注事务方法的编程风格
    * 2:保证事务方法的执行时间尽可能短，不要穿插其他网络操作
    * */
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatkillException, SeckillCloseException {
        if(md5 == null || !md5.equals(this.getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存 + 记录购买记录
        try{
            Date nowTime = new Date();
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if(updateCount <= 0){
                //没有更新到记录，秒杀结束
                throw new SeckillCloseException("seckill is closed");
            }else{
                //记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if(insertCount <= 0){
                    throw new RepeatkillException("repeated seckill");
                }else{
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
                }
            }
        }catch (RepeatkillException e2){
            throw e2;
        }catch (SeckillCloseException e1){
            throw e1;
        } catch (Exception e){
            logger.error(e.getMessage(),e);
            //编译期异常，转换为运行期异常
            throw  new SeckillException("seckill inner error  "+e.getMessage());
        }
    }
}
