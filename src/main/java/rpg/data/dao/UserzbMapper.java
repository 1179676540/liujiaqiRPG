package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Userzb;
import rpg.pojo.UserzbExample;
/**
 * 
 * @author ljq
 *
 */
public interface UserzbMapper {
    int countByExample(UserzbExample example);

    int deleteByExample(UserzbExample example);

    int deleteByPrimaryKey(String id);

    int insert(Userzb record);

    int insertSelective(Userzb record);

    List<Userzb> selectByExample(UserzbExample example);

    Userzb selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Userzb record, @Param("example") UserzbExample example);

    int updateByExample(@Param("record") Userzb record, @Param("example") UserzbExample example);

    int updateByPrimaryKeySelective(Userzb record);

    int updateByPrimaryKey(Userzb record);
}