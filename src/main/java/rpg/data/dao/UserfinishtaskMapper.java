package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Userfinishtask;
import rpg.pojo.UserfinishtaskExample;

public interface UserfinishtaskMapper {
    int countByExample(UserfinishtaskExample example);

    int deleteByExample(UserfinishtaskExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Userfinishtask record);

    int insertSelective(Userfinishtask record);

    List<Userfinishtask> selectByExample(UserfinishtaskExample example);

    Userfinishtask selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Userfinishtask record, @Param("example") UserfinishtaskExample example);

    int updateByExample(@Param("record") Userfinishtask record, @Param("example") UserfinishtaskExample example);

    int updateByPrimaryKeySelective(Userfinishtask record);

    int updateByPrimaryKey(Userfinishtask record);
}