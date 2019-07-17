package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Userkey;
import rpg.pojo.UserkeyExample;

public interface UserkeyMapper {
    int countByExample(UserkeyExample example);

    int deleteByExample(UserkeyExample example);

    int deleteByPrimaryKey(String username);

    int insert(Userkey record);

    int insertSelective(Userkey record);

    List<Userkey> selectByExample(UserkeyExample example);

    Userkey selectByPrimaryKey(String username);

    int updateByExampleSelective(@Param("record") Userkey record, @Param("example") UserkeyExample example);

    int updateByExample(@Param("record") Userkey record, @Param("example") UserkeyExample example);

    int updateByPrimaryKeySelective(Userkey record);

    int updateByPrimaryKey(Userkey record);
}