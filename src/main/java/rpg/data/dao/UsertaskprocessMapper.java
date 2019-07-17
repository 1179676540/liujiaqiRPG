package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Usertaskprocess;
import rpg.pojo.UsertaskprocessExample;

public interface UsertaskprocessMapper {
    int countByExample(UsertaskprocessExample example);

    int deleteByExample(UsertaskprocessExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Usertaskprocess record);

    int insertSelective(Usertaskprocess record);

    List<Usertaskprocess> selectByExample(UsertaskprocessExample example);

    Usertaskprocess selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Usertaskprocess record, @Param("example") UsertaskprocessExample example);

    int updateByExample(@Param("record") Usertaskprocess record, @Param("example") UsertaskprocessExample example);

    int updateByPrimaryKeySelective(Usertaskprocess record);

    int updateByPrimaryKey(Usertaskprocess record);
}