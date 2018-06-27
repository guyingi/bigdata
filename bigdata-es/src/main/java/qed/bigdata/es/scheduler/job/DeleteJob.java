package qed.bigdata.es.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.bigdata.es.scheduler.job
 * @Description: ${todo}
 * @date 2018/5/25 15:07
 */
public class DeleteJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //删除临时图片
        String projectDir = System.getProperty("user.dir");
        System.out.println(projectDir);
    }
}
