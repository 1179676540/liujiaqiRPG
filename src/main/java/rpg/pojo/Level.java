package rpg.pojo;

/**等级类
 * @author ljq
 *
 */
public class Level {
	private int id;
	private int expl;
	private int expr;
	private int hp;
	private int ack;
	private int def;
	/**
	 * @return the hp
	 */
	public int getHp() {
		return hp;
	}
	/**
	 * @param hp the hp to set
	 */
	public void setHp(int hp) {
		this.hp = hp;
	}
	/**
	 * @return the ack
	 */
	public int getAck() {
		return ack;
	}
	/**
	 * @param ack the ack to set
	 */
	public void setAck(int ack) {
		this.ack = ack;
	}
	/**
	 * @return the def
	 */
	public int getDef() {
		return def;
	}
	/**
	 * @param def the def to set
	 */
	public void setDef(int def) {
		this.def = def;
	}
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
	 * @return the expl
	 */
	public int getExpl() {
		return expl;
	}
	/**
	 * @param expl the expl to set
	 */
	public void setExpl(int expl) {
		this.expl = expl;
	}
	/**
	 * @return the expr
	 */
	public int getExpr() {
		return expr;
	}
	/**
	 * @param expr the expr to set
	 */
	public void setExpr(int expr) {
		this.expr = expr;
	}
}
