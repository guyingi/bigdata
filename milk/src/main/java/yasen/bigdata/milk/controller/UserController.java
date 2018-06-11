package yasen.bigdata.milk.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("")
public class UserController {

    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public JSONObject login(HttpServletRequest request, HttpServletResponse response) {
        String username=  request.getParameter("username");
        String password=  request.getParameter("password");
        JSONObject result = new JSONObject();
        if(username.equals("test") && password.equals("test"))
            result.put("result",true);
        else
            result.put("result",false);
        System.out.println("login:"+username+","+password);
        return result;
    }

}
