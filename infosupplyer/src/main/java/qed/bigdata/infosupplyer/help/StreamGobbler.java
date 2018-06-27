package qed.bigdata.infosupplyer.help;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author WeiGuangWu
 * @version V1.0
 * @Package yasen.datauploader.help
 * @Description: ${todo}
 * @date 2018/6/7 17:14
 */
public class StreamGobbler extends Thread {

    InputStream is;
    String type;

    public StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (type.equals("Error")) {
                    System.out.println("Error   :" + line);
                } else {
//                    System.out.println("Debug:" + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                isr.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}