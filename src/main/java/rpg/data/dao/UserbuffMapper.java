package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Userbuff;
import rpg.pojo.UserbuffExample;
/**
 * Mybatis的用户Buff表配置接口
 * @author ljq
 *
 */
public interface UserbuffMapper {
    int countByExample(UserbuffExample example);

    int deleteByExample(UserbuffExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Userbuff record);

    int insertSelective(Userbuff record);

    List<Userbuff> selectByExample(UserbuffExample example);

    Userbuff selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Userbuff record, @Param("example") UserbuffExample example);

    int updateByExample(@Param("record") Userbuff record, @Param("example") UserbuffExample example);

    int updateByPrimaryKeySelective(Userbuff record);

    int updateByPrimaryKey(Userbuff record);
}