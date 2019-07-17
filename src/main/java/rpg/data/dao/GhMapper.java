package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Gh;
import rpg.pojo.GhExample;
/**
 * Mybatis的工会表配置接口
 * @author ljq
 *
 */
public interface GhMapper {
    int countByExample(GhExample example);

    int deleteByExample(GhExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Gh record);

    int insertSelective(Gh record);

    List<Gh> selectByExample(GhExample example);

    Gh selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Gh record, @Param("example") GhExample example);

    int updateByExample(@Param("record") Gh record, @Param("example") GhExample example);

    int updateByPrimaryKeySelective(Gh record);

    int updateByPrimaryKey(Gh record);
}