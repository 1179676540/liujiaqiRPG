package rpg.pojo;

/**技能类
 * @author ljq
 *
 */
public class Skill {
	private int id;
	private String name;
	private int cd;
	private int mp;
	private int hurt;
	private String effect;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the cd
	 */
	public int getCd() {
		return cd;
	}
	/**
	 * @param cd the cd to set
	 */
	public void setCd(int cd) {
		this.cd = cd;
	}
	/**
	 * @return the mp
	 */
	public int getMp() {
		return mp;
	}
	/**
	 * @param mp the mp to set
	 */
	public void setMp(int mp) {
		this.mp = mp;
	}
	/**
	 * @return the hurt
	 */
	public int getHurt() {
		return hurt;
	}
	/**
	 * @param hurt the hurt to set
	 */
	public void setHurt(int hurt) {
		this.hurt = hurt;
	}
	/**
	 * @return the effect
	 */
	public String getEffect() {
		return effect;
	}
	/**
	 * @param effect the effect to set
	 */
	public void setEffect(String effect) {
		this.effect = effect;
	}
}
