package business.global.pk.sss.utlis;

import com.ddm.server.common.utils.Txt2Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * 配置文件
 * @author zaf
 * */
public class SSSConfigMgr {
	public static final String fileName = "SSSConfig.txt";
	public static final String filePath = "conf/";
	private Map<String, String> configMap = new HashMap<String, String>();
	private ArrayList<Integer> huase; //底分
	private int  gui  = 0;
	private int  setCount  = 4;
	private int  special = 0;
	private double loseRadix;
	private int basisRadix;
	private ArrayList<Integer> specialList;
	public SSSConfigMgr(){
		this.configMap = Txt2Utils.txt2Map(filePath, fileName, "GBK");
		this.huase = Txt2Utils.String2ListInteger(this.configMap.get("huase"));
		this.gui = Integer.valueOf(this.configMap.get("gui"));
		this.setCount = Integer.valueOf(this.configMap.get("setCount"));
		this.special = Integer.valueOf(this.configMap.get("special"));
		this.specialList = Txt2Utils.String2ListInteger(this.configMap.get("specialList"));

		

	}
	public ArrayList<Integer> getHuase() {
		return huase;
	}
	public boolean getGui() {
		return gui == 0 ? false: true;
	}

	public int getSetCount() {
		return setCount;
	}

	public int getSpecial() {
		return special;
	}
	public ArrayList<Integer> getSpecialList() {
		return specialList;
	}
	public double getLoseRadix() {
		return loseRadix;
	}
	public int getBasisRadix() {
		return basisRadix;
	}
	
	
}
