package qed.bigdata.es.controller;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
    static Logger logger = Logger.getLogger(DicomSearchController.class);

    @RequestMapping(value = "dicomsearch", method = RequestMethod.GET)
    public ModelAndView dicomsearch(HttpServletRequest request, HttpServletResponse response) {

        logger.log(Level.INFO,"controller:dicomsearch 被调用");

        HttpSession session = request.getSession();
        String username = null;
        if(session != null){
            username = (String)session.getAttribute("username");
        }
        logger.log(Level.INFO,"跳转携带用户名:" + username);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("dicomsearch");
        mav.addObject("username",username);
        System.out.println("dicomsearch:"+username);

        return mav;
    }

    @RequestMapping(value = "patientsearch", method = RequestMethod.GET)
    public ModelAndView patientsearch(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:patientsearch 被调用");

        HttpSession session = request.getSession();
        String username = null;
        if(session != null){
            username = (String)session.getAttribute("username");
        }
        logger.log(Level.INFO,"跳转携带用户名:" + username);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("patientsearch");
        mav.addObject("username",username);
        return mav;
    }

    @RequestMapping(value = "signtag", method = RequestMethod.GET)
    public ModelAndView signtag(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:signtag 被调用");

        HttpSession session = request.getSession();
        String username = null;
        if(session != null){
            username = (String)session.getAttribute("username");
        }
        logger.log(Level.INFO,"跳转携带用户名:" + username);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("signtag");
        mav.addObject("username",username);
        return mav;
    }

    @RequestMapping(value = "tagmanage", method = RequestMethod.GET)
    public ModelAndView tagmanage(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:tagmanage 被调用");

        HttpSession session = request.getSession();
        String username = null;
        if(session != null){
            username = (String)session.getAttribute("username");
        }
        logger.log(Level.INFO,"跳转携带用户名:" + username);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("tagmanage");
        mav.addObject("username",username);
        return mav;
    }

    @RequestMapping(value = "downloaddesensitization", method = RequestMethod.GET)
    public ModelAndView downloaddesensitization(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:downloaddesensitization 被调用");

        HttpSession session = request.getSession();
        String username = null;
        if(session != null){
            username = (String)session.getAttribute("username");
        }
        logger.log(Level.INFO,"跳转携带用户名:" + username);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("downloaddesensitization");
        mav.addObject("username",username);
        return mav;
    }

    @RequestMapping(value = "navigation", method = RequestMethod.GET)
    public ModelAndView navigation(HttpServletRequest request, HttpServletResponse response) {
        logger.log(Level.INFO,"controller:navigation 被调用");
        String username=  request.getParameter("username");
        String password=  request.getParameter("password");
        if(username != null && password != null){
            HttpSession session = request.getSession();
            session.setAttribute("username",username);
            session.setAttribute("password",password);
        }
        logger.log(Level.INFO,"跳转携带用户名:" + username);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("navigation");
        mav.addObject("username",username);
        return mav;
    }
}
