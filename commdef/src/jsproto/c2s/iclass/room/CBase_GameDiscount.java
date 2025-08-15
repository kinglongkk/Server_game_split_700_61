package jsproto.c2s.iclass.room;

import jsproto.c2s.cclass.BaseSendMsg;

public class CBase_GameDiscount extends BaseSendMsg {
    private int selectCityId;
    private int gameId;
    private long clubId;
    private long unionId;

    public static CBase_GameIdList make(int selectCityId) {
        CBase_GameIdList ret = new CBase_GameIdList();
        ret.setSelectCityId(selectCityId);
        return ret;
    }

    public int getSelectCityId() {
        return selectCityId;
    }

    public void setSelectCityId(int selectCityId) {
        this.selectCityId = selectCityId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public long getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public long getUnionId() {
        return unionId;
    }

    public void setUnionId(int unionId) {
        this.unionId = unionId;
    }
}