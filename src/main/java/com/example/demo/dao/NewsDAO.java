package com.example.demo.dao;

import com.example.demo.model.News;
import com.example.demo.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NewsDAO {
    String TABLE_NAME = "news";
    String INSERT_FIELDS = "title, link, image, like_count,comment_count,created_date, user_id";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;
    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title}, #{link },#{image},#{likeCount},#{commentCount},#{createdDate},#{userId})"})
    int addNews(News news);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    News selectById(int id);

    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    @Update({"update ", TABLE_NAME, " set like_count = #{likeCount} where id=#{id}"})
    int updateLikeCount(@Param("id") int id, @Param("likeCount") int likeCount);
    /**
     * 该函数通过xml配置的方式完成数据库操作，内容在resources下的相同目录下
     * 通过Param注解将参数传给了xml文件
     */
    List<News> selectNewsByUserIdAndOffset(@Param("userId") int userId, @Param("offset") int offset,
                                           @Param("limit") int limit);
}
