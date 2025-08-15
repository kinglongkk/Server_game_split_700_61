package business.pdk.c2s.cclass;

/*
 * 跑得快宏定义
 * @author zaf
 * **/
public class PDK_define {

	//跑得快的关门状态
	public static enum PDK_ROBCLOSE_STATUS{
		PDK_ROBCLOSE_STATUS_NOMAL(-1),
		PDK_ROBCLOSE_STATUS_FAIL(0),
		PDK_ROBCLOSE_STATUS_SUCCESS(1),
		PDK_ROBCLOSE_STATUS_REVERES(2),//反关门
		;

		private int value;
		private PDK_ROBCLOSE_STATUS(int value) {this.value = value;}
		public int value() {return this.value;}

		public static PDK_ROBCLOSE_STATUS getRobCloseStatus(String value) {
			String gameTypyName = value.toUpperCase();
			for (PDK_ROBCLOSE_STATUS flow : PDK_ROBCLOSE_STATUS.values()) {
				if (flow.toString().equals(gameTypyName)) {
					return flow;
				}
			}
			return PDK_ROBCLOSE_STATUS.PDK_ROBCLOSE_STATUS_NOMAL;
		}

		public static PDK_ROBCLOSE_STATUS valueOf(int value) {
			for (PDK_ROBCLOSE_STATUS flow : PDK_ROBCLOSE_STATUS.values()) {
				if (flow.value == value) {
					return flow;
				}
			}
			return PDK_ROBCLOSE_STATUS.PDK_ROBCLOSE_STATUS_NOMAL;
		}
	};

	//跑得快类型
	public static enum PDK_GameType{
		//		PDK_PDK(1), //跑得快
		PDK_FJ(2), //龙岩伏击
//		PDK_ZSY(3), //争上游

		;

		private int value;
		private PDK_GameType(int value) {this.value = value;}
		public int value() {return this.value;}

		public static PDK_GameType getGameType(String value) {
			String gameTypyName = value.toUpperCase();
			for (PDK_GameType flow : PDK_GameType.values()) {
				if (flow.toString().equals(gameTypyName)) {
					return flow;
				}
			}
			return PDK_GameType.PDK_FJ;
		}

		public static PDK_GameType valueOf(int value) {
			for (PDK_GameType flow : PDK_GameType.values()) {
				if (flow.value == value) {
					return flow;
				}
			}
			return PDK_GameType.PDK_FJ;
		}
	};


	//跑得快游戏状态
	public static enum PDK_GameStatus{
		PDK_GAME_STATUS_SENDCARD(0), //发牌
		//		PDK_GAME_STATUS_COMPAER_ONE(1), //比牌
		PDK_GAME_STATUS_COMPAER_SECOND(1), //比牌
		PDK_GAME_STATUS_RESULT(2), //结算
		;

		private int value;
		private PDK_GameStatus(int value) {this.value = value;}
		public int value() {return this.value;}

		public static PDK_GameStatus getGameStatus(String value) {
			String gameTypyName = value.toUpperCase();
			for (PDK_GameStatus flow : PDK_GameStatus.values()) {
				if (flow.toString().equals(gameTypyName)) {
					return flow;
				}
			}
			return PDK_GameStatus.PDK_GAME_STATUS_SENDCARD;
		}

		public static PDK_GameStatus valueOf(int value) {
			for (PDK_GameStatus flow : PDK_GameStatus.values()) {
				if (flow.value == value) {
					return flow;
				}
			}
			return PDK_GameStatus.PDK_GAME_STATUS_SENDCARD;
		}
	};

	//玩法
	public static enum PDK_WANFA{
		PDK_WANFA_SHOUJUHEITAO3(0),  	//首局黑桃3先出
		PDK_WANFA_XIANCHUHEITAO3(1),  	//先出黑桃三 每局
		PDK_WANFA_FEIBICHU(2),  		//非比出
		PDK_WANFA_MAXZHADAN(3),  		//3炸封顶
		PDK_WANFA_3AZHA(4),  			//3A炸
		PDK_WANFA_XUEZHANDAODI(5),  	//血战到底
		PDK_WANFA_ZHADANBUKECHAI(6),  	//炸弹不可拆
		PDK_WANFA_HONGTAO10FANBEI(7),  //红桃10翻倍
		PDK_WANFA_QUSANZHANG(8),  		//去3张
		PDK_WANFA_KEMINGPAI(9),  		//可明牌
		PDK_WANFA_4DAIFAN(10),  		//4带翻倍
		PDK_WANFA_XIANSHISHOUPAI(11),  	//显示手牌
		PDK_WANFA_QIANGGUANMEN(12),  	//抢关门
		PDK_WANFA_3BUDAI(13),  			//3不带
		PDK_WANFA_3DAI1(14),  			//3带1
		PDK_WANFA_3DAI2(15),  			//3带2
		PDK_WANFA_4DAI1(16),  			//4带1
		PDK_WANFA_4DAI2(17),  			//4带2
		PDK_WANFA_4DAI3(18),  			//4带3
		PDK_WANFA_LAIZI(19),  			//赖子
		PDK_WANFA_KEJIABEI(20),  		//可加倍
		PDK_WANFA_ZHANDANFANBEI(21),  	//炸弹翻倍
		PDK_WANFA_ZHADANKECHAI(22),  	//炸弹可拆
		PDK_WANFA_3AZHAMAX(23),  	//3A炸最大
		PDK_WANFA_HONGTAO10ZHANIAO(24),  	//红桃十扎鸟
		PDK_WANFA_HEITAO3BUBI(25),  	//黑桃三不必先出
		PDK_WANFA_SHOUDONGGUO(26),  	//手动过
		PDK_WANFA_SHOUDONGZHUNBEI(27),  	//手动准备
		PDK_WANFA_WEIZHANGSUANFEN(28),  	//尾张算分
		PDK_WANFA_WUZHA(29),  	//无炸弹
		PDK_WANFA_NOTBELOWZERO(31),  	//不能低于0
		PDK_WANFA_OnlyWinRightNowPoint(32),     //每局带多少
		;
		private int value;
		private PDK_WANFA(int value) {this.value = value;}
		public int value() {return this.value;}

		public static PDK_WANFA getGameType(String value) {
			String gameTypyName = value.toUpperCase();
			for (PDK_WANFA flow : PDK_WANFA.values()) {
				if (flow.toString().equals(gameTypyName)) {
					return flow;
				}
			}
			return PDK_WANFA.PDK_WANFA_XIANCHUHEITAO3;
		}

		public static PDK_WANFA valueOf(int value) {
			for (PDK_WANFA flow : PDK_WANFA.values()) {
				if (flow.value == value) {
					return flow;
				}
			}
			return PDK_WANFA.PDK_WANFA_XIANCHUHEITAO3;
		}
	};


	//牌类型
	public static enum PDK_CARD_TYPE{
		PDK_CARD_TYPE_NOMARL(0),			//默认状态
		PDK_CARD_TYPE_BUCHU(1),				//不出
		PDK_WANFA_SINGLECARD(2), 			//单牌
		PDK_CARD_TYPE_DUIZI(3),  			//对子
		PDK_CARD_TYPE_SHUNZI(4),  			//顺子
		PDK_CARD_TYPE_3BUDAI(5),  			//3不带
		PDK_CARD_TYPE_3DAI1(6),  			//3带1
		PDK_CARD_TYPE_3DAI2(7),  			//3带2
		PDK_CARD_TYPE_4DAI1(8),  			//4带1
		PDK_CARD_TYPE_4DAI2(9),  			//4带2
		PDK_CARD_TYPE_4DAI3(10),  			//4带3
		PDK_CARD_TYPE_ZHADAN(11),  			//炸弹
		PDK_CARD_TYPE_FEIJI3(12),  			//飞机3
		PDK_CARD_TYPE_FEIJI4(13),  			//飞机4
		PDK_WANFA_LIANDUI(14), 				//联队

		;
		private int value;
		private PDK_CARD_TYPE(int value) {this.value = value;}
		public int value() {return this.value;}

		public static PDK_CARD_TYPE getGameType(String value) {
			String gameTypyName = value.toUpperCase();
			for (PDK_CARD_TYPE flow : PDK_CARD_TYPE.values()) {
				if (flow.toString().equals(gameTypyName)) {
					return flow;
				}
			}
			return PDK_CARD_TYPE.PDK_CARD_TYPE_DUIZI;
		}

		public static PDK_CARD_TYPE valueOf(int value) {
			for (PDK_CARD_TYPE flow : PDK_CARD_TYPE.values()) {
				if (flow.value == value) {
					return flow;
				}
			}
			return PDK_CARD_TYPE.PDK_CARD_TYPE_DUIZI;
		}
	}

	/**
	 * 炸弹算法
	 */
	public enum BombAlgorithm {
		GETROUNDALLBOMB(0),			// 炸弹归每轮最大玩家
		WINNER(1),			// 只算赢家
		PASS(2),			// 炸弹不算分
		ALWAYS(3);			// 有炸就算

		private int type;
		BombAlgorithm(int type) {
			this.type = type;
		}

		/**
		 * 类型转枚举
		 * @param type
		 * @return
		 */
		public static BombAlgorithm valueOf(int type) {
			for (BombAlgorithm bombAlgorithm : BombAlgorithm.values()) {
				if (bombAlgorithm.type == type) {
					return bombAlgorithm;
				}
			}
			throw new IllegalArgumentException("错误的炸弹算法");
		}

		/**
		 * 是否相同类型
		 * @param type
		 * @return
		 */
		public boolean has(int type) {
			return this.type == type;
		}

		public int getType() {
			return type;
		}
	}

	/**
	 * 炸弹分数计算
	 */
	public enum BombScore {
		DOUBLE_(0),			// 翻倍
		ADD_TEN(1);			// 加10分

		private int type;
		BombScore(int type) {
			this.type = type;
		}

		/**
		 * 类型转枚举
		 * @param type
		 * @return
		 */
		public static BombScore valueOf(int type) {
			for (BombScore bombScore : BombScore.values()) {
				if (bombScore.type == type) {
					return bombScore;
				}
			}
			throw new IllegalArgumentException("炸弹分数计算");
		}

		/**
		 * 是否相同炸弹计算方式
		 * @param type
		 * @return
		 */
		public boolean has(int type) {
			return this.type == type;
		}

		public int getType() {
			return type;
		}
	}


	public enum PDKGameRoomConfigEnum {
		/**
		 * 房间内切换人数
		 */
		FangJianQieHuanRenShu,
		/**
		 * 小局托管解散
		 */
		SetAutoJieSan,
		/**
		 * 小局托管2解散
		 */
		SetAutoJieSan2,
		;
	}

}
