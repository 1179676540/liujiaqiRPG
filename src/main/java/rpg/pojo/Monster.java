package rpg.pojo;

import java.util.HashMap;
import java.util.List;

/**
 * 怪物
 * 
 * @author ljq
 *
 */
public class Monster implements Cloneable {
	private int id;
	private String name;
	private boolean aliveFlag;
	private int hp;
	private int ack;
	private int countAcker;
	private List<User> userList;
	private List<Integer> awardList;
	private int money;
	private List<Integer> skillList;
	private int exp;
	private HashMap<Integer, Long> monsterBuffStartTime;

	public HashMap<Integer, Long> getMonsterBuffStartTime() {
		return monsterBuffStartTime;
	}

	public void setMonsterBuffStartTime(HashMap<Integer, Long> monsterBuffStartTime) {
		this.monsterBuffStartTime = monsterBuffStartTime;
	}

	/**
	 * 0被玩家杀 1被buff杀
	 */
	private int deadType;

	public int getDeadType() {
		return deadType;
	}

	public void setDeadType(int deadType) {
		this.deadType = deadType;
	}

	public List<Integer> getSkillList() {
		return skillList;
	}

	public void setSkillList(List<Integer> skillList) {
		this.skillList = skillList;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public List<Integer> getAwardList() {
		return awardList;
	}

	public void setAwardList(List<Integer> awardList) {
		this.awardList = awardList;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public int getCountAcker() {
		return countAcker;
	}

	public void setCountAcker(int countAcker) {
		this.countAcker = countAcker;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAliveFlag() {
		return aliveFlag;
	}

	public void setAliveFlag(boolean aliveFlag) {
		this.aliveFlag = aliveFlag;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	@Override
	public String toString() {
		return "Monster [" + name + "-存活状态=" + aliveFlag + "-血量=" + hp + "-攻击力=" + ack + "]";
	}

	@Override
	public Object clone() {
		Monster monster = null;
		try {
			monster = (Monster) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return monster;
	}
}