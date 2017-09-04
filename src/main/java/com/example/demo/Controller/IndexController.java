package com.example.demo.Controller;

import com.example.demo.Service.DemoService;
import com.example.demo.aspect.LogAspect;
import com.example.demo.model.User;
import com.sun.deploy.net.HttpResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
//@Controller
public class IndexController {
    private static final Logger logger =  LoggerFactory.getLogger(IndexController.class.toString());
    //IoC控制反转，注入一个类，而实现在工程的另外一个地方；SpringBoot的核心
    //可以通过配置文件或注解来实现各种连接
    @Autowired
    private DemoService demoService;

    @RequestMapping(path = {"/", "/index"})
    @ResponseBody
    public String index(HttpSession session) {
        logger.info("This is log from index.");
        return "Hello,this is a new web." + session.getAttribute("msg") +
                "<br> newsService say:"+ demoService.say();
    }

    @RequestMapping(value = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(
            @PathVariable("groupId") String group,
            @PathVariable("userId") int user,
            @RequestParam(value = "type", defaultValue = "1") int type,
            @RequestParam(value = "key", defaultValue = "everyone") String key
    ) {
        return String.format("{%s}{%d}{%d}{%s}", group, user, type, key);
    }

//向vm传递参数，如集合和自定义类
    @RequestMapping(value = {"/vm"})
    public String news(Model model) {
        model.addAttribute("value1", "vv1");
        List<String> colors = Arrays.asList(new String[]{"Red", "Green", "Blue"});

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            map.put(String.valueOf(i), String.valueOf(i * i));
        }

        model.addAttribute("colors", colors);
        model.addAttribute("map", map);
        model.addAttribute("user", new User("Jim"));
        return "news";
    }

    @RequestMapping(value = {"/request"})
    @ResponseBody
    public String reques(HttpServletRequest request,
                         HttpServletResponse response,
                         HttpSession session) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        //读取参数
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }
        //Cookie读取
        for (Cookie cookie : request.getCookies()) {
            sb.append("Cookie:");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");
        }

        sb.append("getMethod:" + request.getMethod() + "<br>");
        sb.append("getPathInfo:" + request.getPathInfo() + "<br>");
        sb.append("getQueryString:" + request.getQueryString() + "<br>");
        sb.append("getRequestURI:" + request.getRequestURI() + "<br>");

        return sb.toString();
    }

    @RequestMapping(value = {"/response"})
    @ResponseBody
    public String response(@CookieValue(value = "cookieId", defaultValue = "idiot") String cook,
                           @RequestParam(value = "key", defaultValue = "another") String key,
                           @RequestParam(value = "value", defaultValue = "idiot2") String value,
                           HttpServletResponse response) {
        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);
        return "cookieId from Cookie:" + cook;
    }

    //重定向，返回重定向的页面；更改session信息
    @RequestMapping(value = {"/redirect/{code}"})
    public String redirect(@PathVariable("code") int code, HttpSession session) {
        /*RedirectView red = new RedirectView("/",true);
        if(code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red; */
        session.setAttribute("msg","Jump from Redirect.");
        return "redirect:/";
    }

    @RequestMapping(value = {"/admin"})
    @ResponseBody
    public String admin(@RequestParam(value="key", required = false) String key) {
        if("admin".equals(key)) {
            return "hello admin";
        }
        throw new IllegalArgumentException("Key 错误");
    }

    //错误页面处理函数，统一错误页面，提高用户体验
    @ExceptionHandler
    @ResponseBody
    public String exceptionHandler(Exception e) {
        return "error:" + e.getMessage();
    }
}
