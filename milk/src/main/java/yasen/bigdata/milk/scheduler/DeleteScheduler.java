package yasen.bigdata.milk.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import yasen.bigdata.milk.scheduler.job.DeleteJob;

import java.util.Date;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.milk.scheduler
 * @Description: ${todo}
 * @date 2018/5/25 15:06
 */
public class DeleteScheduler {
    public void startScheduler() throws SchedulerException {
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        // 开始
        scheduler.start();
        // job 唯一标识 test.test-1
        JobKey jobKey = new JobKey("test" , "test-1");
        JobDetail jobDetail = JobBuilder.newJob(DeleteJob.class).withIdentity(jobKey).build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("test" , "test")
                // 延迟一秒执行
                .startAt(new Date(System.currentTimeMillis() + 1000))
                // 每隔一秒执行 并一直重复
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(300).repeatForever())
                .build();
        scheduler.scheduleJob(jobDetail , trigger);
    }
}
