package business.sss.c2s.iclass;


import business.sss.c2s.cclass.entity.PlayerResult;
import jsproto.c2s.iclass.S_GetRoomInfo;

import java.util.ArrayList;
import java.util.List;

public class SSSS_GetRoomInfo extends S_GetRoomInfo {
    public List<PlayerResult> posResultList= new ArrayList<>();
    public long  zjid=0;
    public  List<CSSS_Ranked> rankeds= new ArrayList<>();
    public int  beishu=0;

    public List<PlayerResult> getPosResultList() {
        return posResultList;
    }

    public void setPosResultList(List<PlayerResult> posResultList) {
        this.posResultList = posResultList;
    }

    public long getZjid() {
        return zjid;
    }

    public void setZjid(long zjid) {
        this.zjid = zjid;
    }

    public List<CSSS_Ranked> getRankeds() {
        return rankeds;
    }

    public void setRankeds(List<CSSS_Ranked> rankeds) {
        this.rankeds = rankeds;
    }

    public int getBeishu() {
        return beishu;
    }

    public void setBeishu(int beishu) {
        this.beishu = beishu;
    }
}