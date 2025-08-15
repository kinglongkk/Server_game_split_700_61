package business.global.pk.dzpk;

/**
 * 长汀510K配置
 *
 * @author Administrator
 */
public class DZPKRoomEnum {


    public enum DZPKGameRoomConfigEnum {
        /**
         * 自动准备
         */
        CHANGE_PLAYER_NUM,
        ZiDongZhunBei,
        XIAO_JU_10S_AOTO_READY,

    }

    public enum DZPKWanFaEnum {

        QUICK_SPORT,
        NORMAL_SPORT,

    }

    public enum DZPKSANTIAN_SHUNZIEnum {

        SHUN_ZI_BIG,
        SAN_TIAO_BIG,

    }

    public enum DZPKSPosStateEnum {
        PLAYING,//游戏中
        END,//结束
        LEAVE,//已经离开
        GUAN_ZHAN,//观战
    }


    public enum DZPK_BET_STATE {
        DI_PAI,//翻牌
        FAN_PAI,//翻牌
        ZHUAN_PAI,//转牌
        HE_PAI,//河牌
        END,//结束

    }


    public enum DZPK_DaXiaoMangEnum {
        MANG_1_2(1, 2),
        MANG_2_4(2, 4),
        MANG_3_6(3, 6),
        MANG_4_8(4, 8),
        ;
        public int xiaoMang;
        public int daMang;


        DZPK_DaXiaoMangEnum(int xiaoMang, int daMang) {
            this.xiaoMang = xiaoMang;
            this.daMang = daMang;

        }


        public static DZPK_DaXiaoMangEnum getMang(int odinal) {
            for (DZPK_DaXiaoMangEnum mangEnum : DZPK_DaXiaoMangEnum.values()) {
                if (mangEnum.ordinal() == odinal) {
                    return mangEnum;
                }
            }
            return DZPK_DaXiaoMangEnum.MANG_2_4;
        }
    }

    public enum DZPK_SNGDaXiaoMangEnum {
        MANG_10(10),
        MANG_15(15),
        MANG_20(20),
        MANG_30(30),
        MANG_40(40),
        MANG_50(50),
        MANG_60(60),
        MANG_75(75),
        MANG_100(100),
        MANG_125(125),
        MANG_150(300),
        MANG_175(350),
        MANG_200(200),
        MANG_250(250),
        MANG_400(400),
        MANG_500(500),
        MANG_600(600),
        MANG_750(750),
        MANG_1000(1000),
        MANG_1200(1200),
        MANG_1500(1500),
        MANG_2000(2000),
        MANG_3000(3000),
        MANG_4000(4000),
        MANG_5000(5000),
        ;
        public int xiaoMang;


        DZPK_SNGDaXiaoMangEnum(int xiaoMang) {
            this.xiaoMang = xiaoMang;

        }

        public static DZPK_SNGDaXiaoMangEnum getMang(int odinal) {
            if (odinal > values().length) {
                odinal = values().length - 1;
            }
            for (DZPK_SNGDaXiaoMangEnum mangEnum : DZPK_SNGDaXiaoMangEnum.values()) {
                if (mangEnum.ordinal() == odinal) {
                    return mangEnum;
                }
            }
            return DZPK_SNGDaXiaoMangEnum.MANG_10;
        }
    }

    public enum DZPK_Ante {
        ANTE_0(0),
        ANTE_1(1),
        MANG_2(2),
        MANG_3(3),
        MANG_4(4),
        MANG_5(5),
        MANG_6(6),
        MANG_8(8),
        MANG_10(10),
        MANG_15(15),
        MANG_20(20),
        MANG_25(25),
        MANG_50(50),
        ;
        public int value;


        DZPK_Ante(int value) {
            this.value = value;

        }

        public static DZPK_Ante getDZPK_Ante(int odinal) {
            for (DZPK_Ante mangEnum : DZPK_Ante.values()) {
                if (mangEnum.ordinal() == odinal) {
                    return mangEnum;
                }
            }
            return DZPK_Ante.ANTE_1;
        }

        public static int getValue(int odinal) {
            return getDZPK_Ante(odinal).value;
        }
    }

    //玩法四种玩法：德州局、短牌局、奥马哈、SNG；
    public static enum DZPK_MoShi {
        DE_ZHOU(0), DUAN_PAI(1), AO_HA_MA(2), SNG(3),
        ;
        private int value;

        private DZPK_MoShi(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static DZPK_MoShi getMoShi(String value) {
            String gameTypyName = value.toUpperCase();
            for (DZPK_MoShi flow : DZPK_MoShi.values()) {
                if (flow.toString().equals(gameTypyName)) {
                    return flow;
                }
            }
            return DZPK_MoShi.DE_ZHOU;
        }

    }


    //牌类型		
    public enum DZPK_CARD_TYPE {
        /**
         * 默认状态
         */
        DZPK_CARD_TYPE_NOMARL(0),
        /**
         * 高牌
         */
        GAO_PAI(1),
        /**
         * 一对
         */
        DAN_DUI(2),
        /**
         * 两对
         */
        LIANG_DUI(3),
        /**
         * 三条
         */
        SAN_TIAO(4),
        /**
         * 顺子
         */
        SHUN_ZI(5),
        /**
         * 同花
         */
        TONG_HUA(6),
        /**
         * 葫芦
         */
        HU_LU(7),
        /**
         * 四条
         */
        SI_TIAO(8),
        /**
         * 同花顺
         */
        TONG_HUA_SHUN(9),
        /**
         * 皇家同花顺
         */
        KING_SHUN(10),


        ;
        private int value;

        DZPK_CARD_TYPE(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

    }


}
