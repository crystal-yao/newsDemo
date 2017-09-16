package com.example.demo.Controller;

import com.example.demo.Service.LikeService;
import com.example.demo.Service.NewsService;
import com.example.demo.Service.UserService;
import com.example.demo.model.EntityType;
import com.example.demo.model.HostHolder;
import com.example.demo.model.News;
import com.example.demo.model.ViewObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger =  LoggerFactory.getLogger(HomeController.class.toString());


    @Autowired
    UserService userService;
    @Autowired
    NewsService newsService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(@RequestParam(value = "userId", defaultValue = "0") int userId,
                        Model model,@RequestParam(value="pop", defaultValue = "0")int pop) {
        model.addAttribute("vos", getNews(0, 0, 10));
        model.addAttribute("pop", pop);
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(@PathVariable("userId") int userId, Model model) {

        model.addAttribute("vos", getNews(userId, 0, 10));
        return "home";
    }

    private List<ViewObject> getNews(int userId, int offset, int limit) {
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        int localUserId = hostHolder.getUser() == null ? 0: hostHolder.getUser().getId();
        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));

            if(localUserId != 0) {
               vo.set( "like" , likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId())) ;
            } else {
                vo.set("like", 0);
            }

            vos.add(vo);
        }
        return vos;
    }
}
