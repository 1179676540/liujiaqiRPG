package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Userlevel;
import rpg.pojo.UserlevelExample;

public interface UserlevelMapper {
    int countByExample(UserlevelExample example);

    int deleteByExample(UserlevelExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Userlevel record);

    int insertSelective(Userlevel record);

    List<Userlevel> selectByExample(UserlevelExample example);

    Userlevel selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Userlevel record, @Param("example") UserlevelExample example);

    int updateByExample(@Param("record") Userlevel record, @Param("example") UserlevelExample example);

    int updateByPrimaryKeySelective(Userlevel record);

    int updateByPrimaryKey(Userlevel record);
}