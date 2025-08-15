package business.global.mj.qzmj;

/**
 * 宁德麻将配置
 * 
 * @author Administrator
 *
 */
public class QZMJRoomEnum {
	public enum QZMJCfg {
		ShuangJinNotPingHu, // 双金不平胡
		DanJinNotPingHu, // 单金不平胡
		LianZhuang,// 连庄
		;
	}

	public enum QZMJJieSuan {
		FHDJP, // 	放胡单家赔
		FHSJP, // 	放胡三家赔
		;
	}
	public enum QZMJFangGang {
		Fen_1(1), Fen_2(2), Fen_3(3),;
		private int value;

		private QZMJFangGang(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static QZMJFangGang valueOf(int value) {
			for (QZMJFangGang flow : QZMJFangGang.values()) {
				if (flow.ordinal() == value) {
					return flow;
				}
			}
			return QZMJFangGang.Fen_1;
		}
	}

	public enum QZMJDiFen {
		diFen(0),;
		private int value;

		private QZMJDiFen(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

	}

	public enum QZMJYouJin {
		beiShu_3(3), beiShu_4(4),;
		private int value;

		private QZMJYouJin(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static QZMJYouJin valueOf(int value) {
			for (QZMJYouJin flow : QZMJYouJin.values()) {
				if (flow.ordinal() == value) {
					return flow;
				}
			}
			return QZMJYouJin.beiShu_3;
		}
	}
	/**
	 * 动作分数
	 * @author Administrator
	 *
	 */
	public enum QZMJOpPoint {
		Not(0),
		Hu(1),// 	屁胡：（底分+盘数）
		ZiMo(2),// 	自摸：（底分+盘数）
		QGH(2),// 	抢杠胡：（底分+盘数）
		SanJinDao(3),	// 	三金倒：（底分+盘数）×3
		TianHu(4),// 天胡（特殊牌型）：庄家起手胡牌；
		DanYou(4),// 	单游：（底分+盘数）×2
		SanYou(16),//	三游：（底分+盘数）×16
		ShuangYou(8),//	双游：（底分+盘数）×8
		TianTing(4),//	天听：（底分+盘数）×4
		QiangJin(2),//	抢金：（底分+盘数）
		PengFan(0),//刻番
		GangFan(0),//杠番
		HuaFan(0),//花番
		JinFan(0),//金番
		LianZhuang(0),//连庄
		DiFen(0),//底分

		;
		private int value;
		private QZMJOpPoint(int value) {this.value = value;}
		public int value(){return this.value;}

		public void setValue(int value) {
			this.value = value;
		}};


}



