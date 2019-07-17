package rpg.service.skill;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import rpg.pojo.Skill;
import rpg.pojo.User;

/**
 * 技能相关常用容器
 * 
 * @author ljq
 *
 */
@Component
public class SkillList {

	private static SkillList self;

	/**
	 * 技能列表
	 */
	private HashMap<String, Skill> skillMp = new HashMap<String, Skill>();
	/**
	 * 技能cd时间
	 */
	private HashMap<User, HashMap<String, Long>> cdMpCache = new HashMap<User, HashMap<String, Long>>();

	public static SkillList getInstance() {
		return self;
	}

	@PostConstruct
	private void init() {
		self = this;
	}

	public HashMap<User, HashMap<String, Long>> getCdMpCache() {
		return cdMpCache;
	}

	public HashMap<String, Skill> getSkillMp() {
		return skillMp;
	}
}
