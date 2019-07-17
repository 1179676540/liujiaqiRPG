package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.MarketItem;
import rpg.pojo.MarketItemExample;

public interface MarketItemMapper {
    int countByExample(MarketItemExample example);

    int deleteByExample(MarketItemExample example);

    int deleteByPrimaryKey(String id);

    int insert(MarketItem record);

    int insertSelective(MarketItem record);

    List<MarketItem> selectByExample(MarketItemExample example);

    MarketItem selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") MarketItem record, @Param("example") MarketItemExample example);

    int updateByExample(@Param("record") MarketItem record, @Param("example") MarketItemExample example);

    int updateByPrimaryKeySelective(MarketItem record);

    int updateByPrimaryKey(MarketItem record);
}