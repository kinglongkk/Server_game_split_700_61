package business.nn.c2s.cclass;

/*
 * 牛牛宏定义
 * @author zaf
 * **/
public class NN_define {

    //牛牛类型
    public static enum NN_GameType {
        NN_ZYQZ(0), //自由抢庄
        NN_NNSZ(1), //牛牛上庄
        NN_GDZJ(2), //固定庄家
        NN_TBNN(3), //通比牛牛
        NN_MPQZ(4), //明牌抢庄
        NN_LZNN(5), //轮庄
        ;

        private int value;

        private NN_GameType(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static NN_GameType getGameType(String value) {
            String gameTypyName = value.toUpperCase();
            for (NN_GameType flow : NN_GameType.values()) {
                if (flow.toString().equals(gameTypyName)) {
                    return flow;
                }
            }
            return NN_GameType.NN_ZYQZ;
        }

        public static NN_GameType valueOf(int value) {
            for (NN_GameType flow : NN_GameType.values()) {
                if (flow.value == value) {
                    return flow;
                }
            }
            return NN_GameType.NN_ZYQZ;
        }
    }

    ;

    //牛牛游戏状态
    public static enum NN_GameStatus {
        NN_GAME_STATUS_SENDCARD_ONE(0), //发牌
        NN_GAME_STATUS_HOG(1), //抢庄
        NN_GAME_STATUS_ONSURECALLBACKER(2), //确认庄家
        NN_GAME_STATUS_BET(3), //下注
        NN_GAME_STATUS_SENDCARD_SECOND(4), //发牌
        NN_GAME_STATUS_RESULT(5), //结算
        ;

        private int value;

        private NN_GameStatus(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static NN_GameStatus getGameStatus(String value) {
            String gameTypyName = value.toUpperCase();
            for (NN_GameStatus flow : NN_GameStatus.values()) {
                if (flow.toString().equals(gameTypyName)) {
                    return flow;
                }
            }
            return NN_GameStatus.NN_GAME_STATUS_HOG;
        }

        public static NN_GameStatus valueOf(int value) {
            for (NN_GameStatus flow : NN_GameStatus.values()) {
                if (flow.value == value) {
                    return flow;
                }
            }
            return NN_GameStatus.NN_GAME_STATUS_HOG;
        }
    }

    /**
     * 可选玩法
     */
    public enum KeXuanWanFa {
        ZiDong(0);     // 自动准备
        private int type;

        KeXuanWanFa(int type) {
            this.type = type;
        }

        public static KeXuanWanFa valueOf(int type) {
            for (KeXuanWanFa keXuanWanFa : KeXuanWanFa.values()) {
                if (keXuanWanFa.type == type)
                    return keXuanWanFa;
            }
            return null;
        }

        public boolean has(int type) {
            return this.type == type;
        }

        public int getType() {
            return type;
        }
    }


}
