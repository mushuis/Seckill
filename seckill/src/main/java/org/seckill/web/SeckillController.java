package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.SecKill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatkillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/seckill")//url:/模块/资源/{id}/细分/seckill/list
public class SeckillController {

    private Logger logger =  LoggerFactory.getLogger(this.getClass());


    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list", method= RequestMethod.GET)
    public String list(Model model){
        //获取列表页
        List<SecKill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        //list.jsp + model = ModelAndView
        return "/list";
    }

    //@PathVariable注解用来从url中获取数据，不加也可以，开发规范
    @RequestMapping(value ="/{seckillId}/detail" ,method = RequestMethod.GET)
    public String detail(Model model,@PathVariable("seckillId") Long seckillId){
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        SecKill seckill = seckillService.getById(seckillId);
        if(seckill == null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "/detail";
    }

    //ajax json
    @RequestMapping(value = "/{seckillId}/exposer",produces = {"application/json;charset=UTF-8"},
            method = RequestMethod.POST)
    @ResponseBody
    public SeckillResult<Exposer> /*TODO*/ exposer(@PathVariable("seckillId") Long seckillId ){
        SeckillResult<Exposer> result;
        //Exposer用来存放秒杀的详细信息
        try{
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            logger.info(e.getMessage(), e);
            result =  new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution",produces = {"application/json;charset=UTF-8"},
            method = RequestMethod.POST

    )
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone",required = false) Long Phone){
        if(Phone == null){
            return new SeckillResult<>(false,"未注册");
        }
        SeckillResult<SeckillExecution> result;
        try{
            SeckillExecution execution = seckillService.executeSeckill(seckillId,Phone,md5 );
            result = new SeckillResult<SeckillExecution>(true,execution);
        }catch (RepeatkillException e){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(false,execution);
        }catch (SeckillCloseException e){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(false,execution);
        }
        catch (Exception e){
            logger.info(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
            result =  new SeckillResult<SeckillExecution>(false,execution);
        }
        return result;
    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    public SeckillResult<Long> time(){
        Date date = new Date();
        return new SeckillResult(true,date.getTime());
    }
}
