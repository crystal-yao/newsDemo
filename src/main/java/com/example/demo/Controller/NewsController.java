package com.example.demo.Controller;

import com.example.demo.Service.*;
import com.example.demo.model.*;
import com.example.demo.util.MD5util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.UserCredentialsDataSourceAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Crys at 2017/8/26
 */
@Controller
public class NewsController {
    private static final Logger logger =  LoggerFactory.getLogger(NewsController.class.toString());

    @Autowired
    NewsService newsService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    //显示资讯具体内容
    @RequestMapping(path = {"/news/{newsId}"}, method = { RequestMethod.GET })
    public String newsDetail(@PathVariable("newsId") int newsId, Model model) {
        News news = newsService.getById(newsId);
        if(news != null) {
            int localUserId = hostHolder.getUser().getId();
            if(localUserId != 0) {
                model.addAttribute( "like" , likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId())) ;
            } else {
                model.addAttribute("like", 0);
            }
            //评论
            List<Comment> comments = commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
            List<ViewObject> commentVOs = new ArrayList<>();
            for(Comment co : comments) {
                ViewObject vo = new ViewObject();
                vo.set("comment", co);
                vo.set("user", userService.getUser(co.getUserId()));
                commentVOs.add(vo);
            }
            model.addAttribute("comments", commentVOs);
        }
        model.addAttribute("news",news);
        model.addAttribute("owner", userService.getUser(news.getUserId()));
        return "detail";
    }

    //添加评论
    @RequestMapping(path = {"/addComment"}, method = { RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try{
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setStatus(0);
            comment.setUserId(hostHolder.getUser().getId());
            comment.setCreatedDate(new Date());
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);

            commentService.addComment(comment);
            //更新news里的评论数
            int count = commentService.getCommentCount(comment.getEntityId(), EntityType.ENTITY_NEWS);
            newsService.updateCommentCount(comment.getEntityId(), count);
        }catch( Exception e ) {
            logger.error("增加评论失败！" + e.getMessage());
        }
        return "redirect:/news/" + String.valueOf(newsId);
    }

    //图片下载
    @RequestMapping(path = {"/image"}, method = { RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName,
                         HttpServletResponse response) {
        response.setContentType("image/jpeg");
        try{
            //将图片下载，输出到response的输出流中
            StreamUtils.copy(new FileInputStream(
                    new File(MD5util.FILE_DIR + imageName)),
                    response.getOutputStream() );
        }catch(Exception e) {
            logger.error("读取图片失败"+ e.getMessage());
        }
    }

    //图片上传入口
    @RequestMapping(path = {"/uploadImage/"}, method = { RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try{
            //String fileUrl = newsService.saveImage(file);
            String fileUrl = qiniuService.saveImage(file,"image/news/");
            if(fileUrl == null) {
                return MD5util.getJSONString(1,"上传图片失败！");
            }
            return MD5util.getJSONString(0, fileUrl);
        }catch (Exception e) {
            logger.error("上传失败"+e.getMessage());
            return MD5util.getJSONString(1,"上传失败！");
        }
    }

    //添加资讯
    @RequestMapping(path = {"/user/addNews/"}, method = { RequestMethod.POST})
    @ResponseBody
    public String addNews( @RequestParam("image") String image,
                           @RequestParam("title") String title,
                           @RequestParam("link") String link) {
        try{
            News news = new News();
            if(hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                news.setUserId(1);
            }
            news.setCreatedDate(new Date());
            news.setImage(image);
            news.setTitle(title);
            news.setLink(link);
            newsService.addNews(news);
            return MD5util.getJSONString(0);
        }catch (Exception e) {
            logger.error("添加资讯失败！" + e.getMessage());
            return MD5util.getJSONString(1,"添加资讯失败");
        }
    }

}
