package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Ghstore;
import rpg.pojo.GhstoreExample;
/**
 * Mybatis的工会仓库表配置接口
 * @author ljq
 *
 */
public interface GhstoreMapper {
    int countByExample(GhstoreExample example);

    int deleteByExample(GhstoreExample example);

    int insert(Ghstore record);

    int insertSelective(Ghstore record);

    List<Ghstore> selectByExample(GhstoreExample example);

    int updateByExampleSelective(@Param("record") Ghstore record, @Param("example") GhstoreExample example);

    int updateByExample(@Param("record") Ghstore record, @Param("example") GhstoreExample example);
}