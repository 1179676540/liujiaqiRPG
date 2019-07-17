package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Userskill;
import rpg.pojo.UserskillExample;


/**
 * Mybatis的用户技能表配置接口
 * @author ljq
 *
 */
public interface UserskillMapper {
    int countByExample(UserskillExample example);

    int deleteByExample(UserskillExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Userskill record);

    int insertSelective(Userskill record);

    List<Userskill> selectByExample(UserskillExample example);

    Userskill selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Userskill record, @Param("example") UserskillExample example);

    int updateByExample(@Param("record") Userskill record, @Param("example") UserskillExample example);

    int updateByPrimaryKeySelective(Userskill record);

    int updateByPrimaryKey(Userskill record);
}