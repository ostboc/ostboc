package cn.itcast.web.task;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时执行的任务类
 */
public class MyTask {

   public void execute() {
      System.out.println("当前时间为："+
            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
   }
}