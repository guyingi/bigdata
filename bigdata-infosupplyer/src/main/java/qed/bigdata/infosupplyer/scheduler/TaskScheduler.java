package qed.bigdata.infosupplyer.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import qed.bigdata.infosupplyer.scheduler.task.CreateRawMhdDataTask;

import java.util.Date;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.scheduler
 * @Description: ${todo}
 * @date 2018/7/9 14:43
 */
public class TaskScheduler {
//    public void startScheduler() throws SchedulerException {
//        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
//        // 开始
//        scheduler.start();
//        // job 唯一标识 test.test-1
//        JobKey jobKey = new JobKey("test" , "test");
//        JobDetail jobDetail = JobBuilder.newJob(CreateRawMhdDataTask.class).withIdentity(jobKey).build();
//        Trigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity("test" , "test")
//                // 延迟一秒执行
//                .startAt(new Date(System.currentTimeMillis() + 1000))
//                // 每隔一秒执行 并一直重复
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).repeatForever())
//                .build();
//        scheduler.scheduleJob(jobDetail , trigger);
//        scheduler.start();
//    }
}
