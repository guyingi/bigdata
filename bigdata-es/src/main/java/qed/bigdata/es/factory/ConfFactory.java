package qed.bigdata.es.factory;

import qed.bigdata.es.conf.MilkConfiguration;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package qed.bigdata.es.factory
 * @Description: ${todo}
 * @date 2018/6/19 17:22
 */
public class ConfFactory {
    public static MilkConfiguration milkConfiguration = null;
    public static MilkConfiguration getMilkConfiguration(){
        if(milkConfiguration == null){
            synchronized (ConfFactory.class){
                if(milkConfiguration == null){
                    milkConfiguration = new MilkConfiguration();
                }
            }
        }
        return milkConfiguration;
    }
}
