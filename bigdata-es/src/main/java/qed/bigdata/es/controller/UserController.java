package qed.bigdata.es.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("")
public class UserController {
    static Logger logger = Logger.getLogger(PatientSearchController.class);

    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public JSONObject login(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:login 被调用");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        logger.log(Level.INFO,"接收的参数:"+username);

        JSONObject result = new JSONObject();
        if(username.equals("test") && password.equals("test"))
            result.put("result",true);
        else
            result.put("result",false);
        logger.log(Level.INFO,"登陆用户名与密码:username："+username+"\t password:"+password);
        logger.log(Level.INFO,"控制流程结束:downloadbypatient");
        return result;
    }

}
