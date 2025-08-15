package jsproto.c2s.iclass.rank;

import java.util.ArrayList;
import java.util.List;

import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.rank.RankItem;

public class SRanking_List extends BaseSendMsg {
	public List<RankItem> rankItems = new ArrayList<RankItem>();
	
    public static SRanking_List make(List<RankItem> rankItems) {
    	SRanking_List ret = new SRanking_List();
    	ret.rankItems = rankItems;
        return ret;


    }
}
