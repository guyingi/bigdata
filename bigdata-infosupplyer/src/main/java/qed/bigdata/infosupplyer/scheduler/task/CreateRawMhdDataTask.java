package qed.bigdata.infosupplyer.scheduler.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import qed.bigdata.infosupplyer.dao.CreateRawTaskInfoDao;
import qed.bigdata.infosupplyer.dao.TaskHistoryDao;
import qed.bigdata.infosupplyer.pojo.bigdata.CreateRawTaskInfo;
import qed.bigdata.infosupplyer.pojo.bigdata.TaskHistory;
import qed.bigdata.infosupplyer.service.DesensitizationService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.infosupplyer.scheduler.task
 * @Description: ${todo}
 * @date 2018/7/9 14:43
 */

@Component
public class CreateRawMhdDataTask{

    @Autowired
    CreateRawTaskInfoDao createRawTaskInfoDao;

    @Autowired
    TaskHistoryDao taskHistoryDao;

    @Autowired
    DesensitizationService desensitizationService;


    @Scheduled(cron = "* 0/1 * * * ? ") // 间隔5秒执行,[秒] [分] [小时] [日] [月] [周] [年]
    public void taskCycle() {
        Long starttime = new Date().getTime();

        List<String> seccessList = new ArrayList<String>();
        List<String> failedList = new ArrayList<String>();

        //扫描数据库，得到待脱敏的tag，并同时更新开始脱敏时间
        List<CreateRawTaskInfo> createRawTaskInfos = createRawTaskInfoDao.listNew();
        if(createRawTaskInfos == null || createRawTaskInfos.size() == 0)
            return;


        //循环list，挨个做脱敏处理，将成功的添加到list
        for(CreateRawTaskInfo info : createRawTaskInfos){
            String tag = info.getTag();
            Long desensitizedicom = desensitizationService.desensitizedicom(tag);
            if(desensitizedicom > 0) {
                createRawTaskInfoDao.updateStatus(info.getId(),1);
                seccessList.add(tag);
            }else {
                createRawTaskInfoDao.updateStatus(info.getId(),2);
                failedList.add(tag);
            }
        }

        //新增任务记录到数据库
        Long endtime = new Date().getTime();
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setStarttime(new Timestamp(starttime));
        taskHistory.setEndtime(new Timestamp(endtime));
        JSONArray seccessJson = JSONArray.parseArray(JSON.toJSONString(seccessList));
        JSONArray failedJson = JSONArray.parseArray(JSON.toJSONString(failedList));
        String description = "成功:"+seccessJson.toJSONString()+",失败："+failedJson.toJSONString();
        taskHistory.setDescription(description);
        taskHistoryDao.insert(taskHistory);
    }
}
