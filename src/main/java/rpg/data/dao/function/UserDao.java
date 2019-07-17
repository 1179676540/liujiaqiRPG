package rpg.data.dao.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import rpg.data.dao.UserMapper;
import rpg.pojo.User;

/**
 * 用户数据库dao
 * @author ljq
 *
 */
@Component
@Slf4j
public class UserDao {

	@Autowired
	private UserMapper userMapper;

	@Async
	public void updateByPrimaryKey(User user) {
		userMapper.updateByPrimaryKey(user);
		log.info("UserDao更新数据库成功.................");
	}
}
