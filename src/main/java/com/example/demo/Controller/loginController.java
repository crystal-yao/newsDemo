package com.example.demo.Controller;

import com.example.demo.Service.NewsService;
import com.example.demo.Service.UserService;
import com.example.demo.model.News;
import com.example.demo.model.ViewObject;
import com.example.demo.util.MD5util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Crys at 2017/8/19
 */
@Controller
public class loginController {
    private static final Logger logger =  LoggerFactory.getLogger(loginController.class.toString());
    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String register(Model model, @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value="rember", defaultValue = "0") int rember,
                           HttpServletResponse response) {
        try{
            Map<String, Object> map = userService.register(username,password);
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(rember > 0 ) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                return MD5util.getJSONString(0,"注册成功");
            } else {
                return MD5util.getJSONString(1, map);
            }
        }catch (Exception e) {
            logger.error("注册异常"+ e.getMessage());
            return MD5util.getJSONString(1, "注册异常");
        }
    }

    @RequestMapping(path = {"/login/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(Model model,@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value="rember", defaultValue = "0") int rember,
                        HttpServletResponse response) {
        try{
            Map<String, Object> map = userService.login(username,password);
            if(map.containsKey("ticket")) {
                //有ticket，说明登录正常，将该用户的ticket信息加入到cookie中
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(rember > 0 ) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                return MD5util.getJSONString(0,"登录成功");
            } else {
                return MD5util.getJSONString(1, map);
            }
        }catch (Exception e) {
            logger.error("登录异常"+ e.getMessage());
            return MD5util.getJSONString(1, "登录异常");
        }
    }

    @RequestMapping(path = {"/logout/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }
}
