package yasen.bigdata.milk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class GlobalController {

    @RequestMapping(value = "dicomsearch", method = RequestMethod.GET)
    public ModelAndView dicomsearch(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String username = null;
        if(session != null){
            username = (String)session.getAttribute("username");
        }
        ModelAndView mav = new ModelAndView();
        mav.setViewName("dicomsearch");
        mav.addObject("username",username);
        return mav;
    }

    @RequestMapping(value = "patientsearch", method = RequestMethod.GET)
    public ModelAndView patientsearch(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String username = null;
        if(session != null){
            username = (String)session.getAttribute("username");
        }
        System.out.println("patientsearch:"+username);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("patientsearch");
        mav.addObject("username",username);
        return mav;
    }

    @RequestMapping(value = "navigation", method = RequestMethod.GET)
    public ModelAndView navigation(HttpServletRequest request, HttpServletResponse response) {
        String username=  request.getParameter("username");
        String password=  request.getParameter("password");
        if(username != null && password != null){
            HttpSession session = request.getSession();
            session.setAttribute("username",username);
            session.setAttribute("password",password);
        }
        System.out.println("navigation:"+username+","+password);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("navigation");
        mav.addObject("username",username);
        return mav;
    }
}
