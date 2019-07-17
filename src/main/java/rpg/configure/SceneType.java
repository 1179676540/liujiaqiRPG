package rpg.configure;

/**
 * 场景类型
 * 
 * @author ljq
 *
 */
public enum SceneType {
	/**
	 * 起始之地
	 */
	QI_SHI_ZHI_DI("起始之地"),
	/**
	 * 森林
	 */
	SEN_LIN("森林"),
	/**
	 * 城堡
	 */
	CHEN_BAO("城堡"),
	/**
	 * 村子
	 */
	CUN_ZI("村子");

	private String value;

	private SceneType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
