package com.example.demo.Service;

import com.example.demo.dao.NewsDAO;
import com.example.demo.model.News;
import com.example.demo.util.MD5util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit) {
        List<News> newsList = newsDAO.selectNewsByUserIdAndOffset(userId, offset, limit);
        return newsList;
    }

    //保存图片
    /*
    public String saveImage(MultipartFile file) throws IOException {
        //获取文件类型
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        String fileEx = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();

        //判断文件类型是否为可接受的图片
        if (!MD5util.isFileAllowed(fileEx)) {
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "")
                + "." + fileEx;
        //复制图片到服务器指定位置
        Files.copy(file.getInputStream(), new File(MD5util.FILE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        return MD5util.WEB_DOMAIN + "image?name=" + fileName;
    }  */

    //插入资讯
    public int addNews(News news) {
        newsDAO.addNews(news);
        return news.getId();
    }

    //获取资讯
    public News getById(int newsId) {
        News news = newsDAO.selectById(newsId);
        return news;
    }

    public int updateCommentCount(int entityId, int count) {
        return newsDAO.updateCommentCount(entityId, count);
    }

    public int updateLikeCount( int entiytId, int count ) {
        return newsDAO.updateLikeCount(entiytId, count);
    }
}
