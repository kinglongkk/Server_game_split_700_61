package business.sss.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

/**
 * 红中麻将 配置
 * @author Clark
 *
 */
// 一局中各位置的信息
public class SSSSet_Pos{
	public int posID = -1;
	public List<String> shouCard = new ArrayList<>(); //手牌，如果不是自己，填0， 如果个数是3n+2,则独立显示手牌
	public int special = -1;

}
	
