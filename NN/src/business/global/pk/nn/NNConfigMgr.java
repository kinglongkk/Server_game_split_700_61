package business.global.pk.nn;

import com.ddm.server.common.utils.Txt2Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * 跑得快 配置文件
 * @author zaf
 * */
public class NNConfigMgr {
	public static final String fileName = "NNConfig.txt";
	public static final String filePath = "conf/";
	private Map<String, String> configMap = new HashMap<String, String>();
	protected int God_Card ;
	protected ArrayList<Integer> Private_Card1;
	protected ArrayList<Integer> Private_Card2;
	protected ArrayList<Integer> Private_Card3;
	protected ArrayList<Integer> Private_Card4;
	protected ArrayList<Integer> Private_Card5;
	protected ArrayList<Integer> Private_Card6;
	protected ArrayList<Integer> Private_Card7;
	protected ArrayList<Integer> Private_Card8;
	public NNConfigMgr(){
		this.configMap = Txt2Utils.txt2Map(filePath, fileName, "GBK");
		this.God_Card = Integer.valueOf(this.configMap.get("God_Card"));
		this.Private_Card1 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card1"));
		this.Private_Card2 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card2"));
		this.Private_Card3 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card3"));
		this.Private_Card4 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card4"));
		this.Private_Card5 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card5"));
		this.Private_Card6 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card6"));
		this.Private_Card7 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card7"));
		this.Private_Card8 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card8"));
	}

	/**
	 * @return god_Card
	 */
	public boolean isGodCard() {
		return God_Card == 1;
	}
	/**
	 * @return private_Card1
	 */
	public ArrayList<Integer> getPrivate_Card1() {
		return Private_Card1;
	}
	/**
	 * @return private_Card2
	 */
	public ArrayList<Integer> getPrivate_Card2() {
		return Private_Card2;
	}
	/**
	 * @return private_Card3
	 */
	public ArrayList<Integer> getPrivate_Card3() {
		return Private_Card3;
	}
	/**
	 * @return private_Card4
	 */
	public ArrayList<Integer> getPrivate_Card4() {
		return Private_Card4;
	}
	/**
	 * @return private_Card5
	 */
	public ArrayList<Integer> getPrivate_Card5() {
		return Private_Card5;
	}
	/**
	 * @return private_Card6
	 */
	public ArrayList<Integer> getPrivate_Card6() {
		return Private_Card6;
	}
	/**
	 * @return private_Card7
	 */
	public ArrayList<Integer> getPrivate_Card7() {
		return Private_Card7;
	}
	/**
	 * @return private_Card8
	 */
	public ArrayList<Integer> getPrivate_Card8() {
		return Private_Card8;
	}
}
