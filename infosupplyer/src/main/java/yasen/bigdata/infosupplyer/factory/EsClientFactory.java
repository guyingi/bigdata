package yasen.bigdata.infosupplyer.factory;

/**
 * @Title: EsClientFactory.java
 * @Package yasen.bigdata.infosupplyer.util
 * @Description: 获取ES原生java api Client工厂
 * @author weiguangwu
 * @date  2018/4/23 14:13
 * @version V1.0
 */

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import yasen.bigdata.infosupplyer.conf.InfosupplyerConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;

//import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class EsClientFactory {

    /**
     * @Author:weiguangwu
     * @Description: 获取ES原生java api TransportClient
     * @params:[]
     * @return: org.elasticsearch.client.transport.TransportClient
     * @Date: 2018/4/24 15:45
     */
    public static TransportClient getTransportClient(){
        InfosupplyerConfiguration conf = new InfosupplyerConfiguration();
        Settings setting = Settings.builder()
                .put("cluster.name", conf.getEscluster())//指定集群名称
                .put("client.transport.ignore_cluster_name", false)
                .put("client.transport.sniff", true)//启动嗅探功能
                .build();
        InetAddress address = null;
        if(conf.getEsip()!=null){
            String[] ip = conf.getEsip().split("\\.");
            byte[] bip = new byte[4];
            bip[0] = (byte)Integer.parseInt(ip[0]);
            bip[1] = (byte)Integer.parseInt(ip[1]);
            bip[2] = (byte)Integer.parseInt(ip[2]);
            bip[3] = (byte)Integer.parseInt(ip[3]);
            try {
                address = InetAddress.getByAddress(bip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }else if(conf.getEshost()!=null){
            try {
                address = InetAddress.getByName(conf.getEshost());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }else{

        }
        return new PreBuiltTransportClient(setting).addTransportAddress(new TransportAddress(address,Integer.parseInt(conf.getEstcpport())));
    }

    //    public static void main(String[] args) throws UnknownHostException {
//        TransportClient transportClient = getTransportClient();
//        List<DiscoveryNode> nodeList = transportClient.connectedNodes();
//        System.out.println(nodeList.size());
//        for(DiscoveryNode node : nodeList){
//            System.out.println(node.getHostName());
//        }
//        String ip[] = "192.168.237.131".split("\\.");
//    }

}
