package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Ghuser;
import rpg.pojo.GhuserExample;
/**
 * Mybatis的工会用户表配置接口
 * @author ljq
 *
 */
public interface GhuserMapper {
    int countByExample(GhuserExample example);

    int deleteByExample(GhuserExample example);

    int insert(Ghuser record);

    int insertSelective(Ghuser record);

    List<Ghuser> selectByExample(GhuserExample example);

    int updateByExampleSelective(@Param("record") Ghuser record, @Param("example") GhuserExample example);

    int updateByExample(@Param("record") Ghuser record, @Param("example") GhuserExample example);
}