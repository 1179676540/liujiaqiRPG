package rpg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;
import rpg.configure.MsgResp;
import rpg.configure.MsgSize;
import rpg.pojo.User;
import rpg.pojo.Zb;
import rpg.service.task.TaskManage;
import rpg.util.ProbabilityUtil;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;
import rpg.xmlparse.DrawXmlParse;
import rpg.xmlparse.ZbXmlParse;

/**
 * 抽奖处理器
 * 
 * @author ljq
 *
 */
@Slf4j
@Component
public class DrawDispatch {

	@Autowired
	private DrawXmlParse drawXmlParse;
	@Autowired
	private ZbXmlParse zbXmlParse;
	@Autowired
	private RpgUtil rpgUtil;
	@Autowired
	private TaskManage taskManage;

	/**
	 * 展示抽奖信息
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void showDraw(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length != MsgSize.MAX_MSG_SIZE_1.getValue()) {
			SendMsg.send(MsgResp.ORDER_ERR, ch);
			return;
		}

		rpg.pojo.Draw draw = drawXmlParse.getDraw();
		String mString = "";
		mString += draw.getName() + "\n";
		HashMap<Integer, Integer> idToMoney = draw.getIdToMoney();
		int i = 1;
		for (; i <= idToMoney.size(); i++) {
			Integer id = idToMoney.get(i);
			mString += "金币------" + id + "\n";
		}
		HashMap<Integer, Integer> idToZb = draw.getIdToZb();
		for (; i <= idToZb.size() + idToMoney.size(); i++) {
			Integer id = idToZb.get(i);
			Zb zb = zbXmlParse.getZbById(id);
			String level = "";
			for (int j = 0; j < zb.getLevel(); j++) {
				level += "★";
			}
			mString += "装备------" + zb.getName() + "等级" + level + "攻击力" + zb.getAck() + "\n";
		}
		SendMsg.send(mString, ch);
	}

	/**
	 * 抽奖
	 * 
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void Draw(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		rpg.pojo.Draw draw = drawXmlParse.getDraw();

		if (msg.length != MsgSize.MAX_MSG_SIZE_1.getValue()) {
			SendMsg.send(MsgResp.ORDER_ERR, ch);
			return;
		}

		if (user.getMoney() < draw.getNum()) {
			SendMsg.send(MsgResp.MONEY_ERR, ch);
			return;
		}

		List<Double> prob = new ArrayList<Double>();
		HashMap<Integer, Integer> idToMoney = draw.getIdToMoney();
		HashMap<Integer, Double> moneyMap = draw.getMoneyMap();
		int i = 1;
		for (; i <= idToMoney.size(); i++) {
			Integer id = idToMoney.get(i);
			Double moneyNum = moneyMap.get(id);
			prob.add(moneyNum);
		}
		HashMap<Integer, Integer> idToZb = draw.getIdToZb();
		HashMap<Integer, Double> zbMap = draw.getZbMap();
		for (; i <= idToZb.size() + idToMoney.size(); i++) {
			Integer id = idToZb.get(i);
			Double zbId = zbMap.get(id);
			prob.add(zbId);
		}
		ProbabilityUtil probability = new ProbabilityUtil(prob);
		int id = probability.next() + 1;
		log.info(id+"---------");
		user.setMoney(user.getMoney() - draw.getNum());
		// 获取次数
		int num = (int) (draw.getMin() + Math.random() * (draw.getMax() - draw.getMin() + 1));

		if (id <= idToMoney.size()) {
			user.setDrawNum(user.getDrawNum() + 1);
			Integer moneyNum = idToMoney.get(id);
			user.setMoney(user.getMoney() + moneyNum);
			SendMsg.send("恭喜你抽中-----金币-----" + moneyNum, ch);
			return;
		}

		if ((id == idToMoney.size() + idToZb.size()) || (user.getDrawNum() >= num + user.getWinNum() * 10)) {
			id = idToMoney.size() + idToZb.size();
			// 重置次数，并统计
			user.setDrawNum(0);
			user.setWinNum(user.getWinNum() + 1);
			Integer zbId = idToZb.get(id);
			Zb zb = zbXmlParse.getZbById(zbId);
			rpgUtil.putZb(user, zb);
			String level = "";
			for (int j = 0; j < zb.getLevel(); j++) {
				level += "★";
			}
			SendMsg.send("恭喜你抽中终极大奖-----" + zb.getName() + "等级" + level, ch);
			for (Channel channel : group) {
				if (channel != ch) {
					SendMsg.send("恭喜" + user.getNickname() + "抽中终极大奖-----" + zb.getName() + "等级" + level + "攻击力"
							+ zb.getAck(), channel);
				}
			}
			taskManage.checkTaskCompleteBytaskidWithzb(user, 4, zb);
			return;
		}

		user.setDrawNum(user.getDrawNum() + 1);
		Integer zbId = idToZb.get(id);
		Zb zb = zbXmlParse.getZbById(zbId);
		rpgUtil.putZb(user, zb);
		String level = "";
		for (int j = 0; j < zb.getLevel(); j++) {
			level += "★";
		}
		SendMsg.send("恭喜你抽中-----装备-----" + zb.getName() + "等级" + level + "攻击力" + zb.getAck(), ch);
	}
}
