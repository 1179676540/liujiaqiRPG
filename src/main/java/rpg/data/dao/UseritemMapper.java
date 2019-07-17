package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Useritem;
import rpg.pojo.UseritemExample;

public interface UseritemMapper {
    int countByExample(UseritemExample example);

    int deleteByExample(UseritemExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Useritem record);

    int insertSelective(Useritem record);

    List<Useritem> selectByExample(UseritemExample example);

    Useritem selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Useritem record, @Param("example") UseritemExample example);

    int updateByExample(@Param("record") Useritem record, @Param("example") UseritemExample example);

    int updateByPrimaryKeySelective(Useritem record);

    int updateByPrimaryKey(Useritem record);
}