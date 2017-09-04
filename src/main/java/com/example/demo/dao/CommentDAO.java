package com.example.demo.dao;

import com.example.demo.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Crys at 2017/9/2
 */
@Mapper
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = "user_id, content, entity_id, entity_type, status, created_date ";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId}, #{content },#{entityId},#{entityType},#{status},#{createdDate})"})
    int addComment(Comment comment);

    @Select({"select ",SELECT_FIELDS," from ", TABLE_NAME,
            " where entity_id =#{entityId} and entity_type = #{entityType} order by id desc"})
    List<Comment> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from ", TABLE_NAME,
            " where entity_id =#{entityId} and entity_type = #{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Update({"update ", TABLE_NAME, " set status = #{status} where entity_id =#{entityId} and entity_type = #{entityType}"})
    void updateStatus(@Param("entityId") int entityId, @Param("entityType") int entityType,
                     @Param("status") int status);
}
