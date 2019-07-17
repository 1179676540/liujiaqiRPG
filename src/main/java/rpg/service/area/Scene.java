package rpg.service.area;

import java.util.LinkedList;

import rpg.pojo.Monster;
import rpg.pojo.Npc;

/**
 *场景类 
 * @author ljq
 *
 */
public class Scene {
	public int id;
	public String name;
	private LinkedList<Npc> npcList;
	private LinkedList<Monster> monsterList;

	public LinkedList<Monster> getMonsterList() {
		return monsterList;
	}

	public void setMonsterList(LinkedList<Monster> monsterList) {
		this.monsterList = monsterList;
	}

	public LinkedList<Npc> getNpcList() {
		return npcList;
	}

	public void setNpcList(LinkedList<Npc> npcList) {
		this.npcList = npcList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		String npcStr = "";
		String monsterStr = "";
		for (Npc npc : npcList) {
			npcStr += npc.toString();
		}
		for (Monster monster : monsterList) {
			monsterStr += monster.toString();
		}
		return "Scene [name=" + name + ", npc=" + npcStr + ", monster=" + monsterStr + "]";
	}

}
