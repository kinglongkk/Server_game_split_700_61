package business.sss.c2s.cclass.newsss;

/**
 * Enum {@code RankingEnum} 牌型的规则排列大小类型  ,牌型改动，需要通知客户端修改
 */
public enum RankingEnum {
	HIGH_CARD(0,"乌龙",1 ), 
	ONE_PAIR(1, "一对",1), 
	TWO_PAIR(2, "两对",1), 
	THREE_OF_THE_KIND(3, "三条",1), 
	STRAIGHT(4, "顺子",1), 
	FLUSH(5, "同花",1), 
	FLUSH_ONE_PAIR(6, "一对同花",1), //新增
	FLUSH_TWO_PAIR(7, "两对同花",1), //新增
	FULL_HOUSE(8, "葫芦",1), 
	FOUR_OF_THE_KIND(9, "铁支",4), 
	STRAIGHT_FLUSH(10, "同花顺",5), 
	FIVE_OF_THE_KIND(11, "五同",10), //新增

	SShunZi(84, "三顺子",3), 
	STongHua(85, "三同花",3), 
	LDuiBan(86, "六对半",4), 
	WDuiSanChong(87, "五对三冲",5), 
	STaoSanTiao(88, "四套三条",8), 
	ZhongYYDian(89,"中原一点色",9),
	CYiSe(90, "凑一色",10), 
	QXiao(91, "全小",12), 
	LIULIUDASHUAN(92, "六六大顺",13), //新增
	QDa(93, "全大",15), 
	SFenTianXia(94, "三分天下",16), 
//	LIULIUDASHUAN(93, "六六大顺",20), 
	STongHuaShun(95, "三同花顺",18), 
	SErHuangzu(96, "十二皇族",24), 
//	SANHUANWUDI(96, "三皇五帝",26), 
	YTiaoLong(97, "一条龙",26), 
	QIXINGLIANZHU(98, "七星连珠",38), 
	BAXIANGGUOHAI(99, "八仙过海",48), 
	ZZunQinLong(100,"至尊清龙",52);

	
	private String type;
	private int priority;
	private int value;

	RankingEnum(int priority,String type,int value) {
		this.type = type;
		this.priority = priority;
		this.value = value ;
	}
	
	 public static RankingEnum valueOf(int value) {  
		 for (RankingEnum flow : RankingEnum.values()) {
				if (flow.priority == value) {
					return flow;
				}
			}
		 return null;
	 }  

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int value() {
		return value;
	}
}
