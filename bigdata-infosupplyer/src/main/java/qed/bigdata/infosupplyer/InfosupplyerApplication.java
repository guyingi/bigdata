package qed.bigdata.infosupplyer;

import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import qed.bigdata.infosupplyer.scheduler.TaskScheduler;
import qed.bigdata.infosupplyer.scheduler.task.CreateRawMhdDataTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@EnableAutoConfiguration
@SpringBootApplication
public class InfosupplyerApplication {
    public static void main(String[] args) {
        SpringApplication.run(InfosupplyerApplication.class, args);

//        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
//        // 从现在开始2秒钟之后，每隔2秒钟执行一次job2
//        service.scheduleWithFixedDelay(new CreateRawMhdDataTask(), 2, 2, TimeUnit.SECONDS);
    }

}

