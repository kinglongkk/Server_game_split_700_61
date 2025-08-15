package business.global.pk.nn;

import java.util.ArrayList;
import java.util.Comparator;

public class NNGameLogic {
	public final static int MAX_COUNT = 5;
	//扑克类型
	public final static int OX_VALUE0		=0;										//混合牌型
	
	public final static int OX_THREE_SAME	=101;									//三条牌型
	public final static int OX_FOURKING		=102;									//天王牌型
	public final static int OX_FIVEKING		=103;									//天王牌型 五张都是10以上
	public final static int OX_FOUR_SAME	=104;									//四条牌型  炸弹
	public final static int OX_FIVE_SMALL   =105;                                	//5小牌
	
	//获取牌值
	public static Integer GetCardValue(Integer card){
		return BasePocker.getCardValue(card);
	}
	
	//获取牌颜色
	public static Integer GetCardColor(Integer card){
		return BasePocker.getCardColor(card);
	}
	
	//获取类型
	@SuppressWarnings({ "unchecked", "unused" })
	public static int GetCardType( ArrayList<Integer> list)
	{
		GetOxCard(list);
		
		//5小牌
		Integer cbCardCount = (Integer) list.size();
		Integer smallCardNum=0,smallCardSum=0;
		for(int i=0;i<MAX_COUNT;i++)
		{
			
			if(GetCardValue(list.get(i))<5)
			{
				smallCardNum++;
				smallCardSum += GetCardValue(list.get(i));
			}
			else
				break;

		}
		
		if(smallCardSum<=10 && smallCardNum==5)
			return OX_FIVE_SMALL;

		////炸弹牌型
		Integer bSameCount = 0;
		ArrayList<Integer> tempCardData = (ArrayList<Integer>) list.clone();

		tempCardData.sort(sorter);
		Integer bSecondValue = GetCardValue(tempCardData.get(MAX_COUNT/2));
		for(Integer i=0;i<cbCardCount;i++)
		{
			if(bSecondValue == GetCardValue(tempCardData.get(i)))
			{
				bSameCount++;
			}
		}
		if(bSameCount==4)return OX_FOUR_SAME;

		

		Integer bKingCount=0,bTenCount=0;
		for(Integer i=0;i<cbCardCount;i++)
		{
			if(GetCardValue(list.get(i))>10)
			{
				bKingCount++;
			}
			else if(GetCardValue(list.get(i))==10)
			{
				bTenCount++;
			}
		}
		if(bKingCount==MAX_COUNT) return OX_FIVEKING;


		ArrayList<Integer> bTemp = new ArrayList<Integer>(MAX_COUNT);
		Integer bSum=0;
		for (int i=0;i<cbCardCount;i++)
		{
			Integer value = GetCardLogicValue(list.get(i));
			bTemp.add(value);
			bSum+=value;
		}

		for (int i=0;i<cbCardCount-1;i++)
		{
			for (int j=i+1;j<cbCardCount;j++)
			{
				if((bSum-bTemp.get(i)-bTemp.get(j))%10==0)
				{	
					int owValue = ((bTemp.get(i)+bTemp.get(j))>10)?(bTemp.get(i)+bTemp.get(j)-10):(bTemp.get(i)+bTemp.get(j));
					return owValue;
				}
			}
		}

		return OX_VALUE0;
	}

	//获取牛牛
	@SuppressWarnings("unchecked")
	public static  boolean GetOxCard(ArrayList<Integer> list)
	{
		ArrayList<Integer> bTemp = new ArrayList<Integer>(list.size());
		ArrayList<Integer> bTempData = (ArrayList<Integer>) list.clone();
		Integer bSum=0;
		for (Integer i=0;i<list.size();i++)
		{
			bTemp.add(i, GetCardLogicValue(list.get(i)));
			bSum+=bTemp.get(i);
		}

		//查找牛牛
		for (int i=0;i<list.size()-1;i++)
		{
			for (int j=i+1;j<list.size();j++)
			{
				if((bSum-bTemp.get(i)-bTemp.get(j))%10==0)
				{
					int bCount=0;
					for (int k=0;k<list.size();k++)
					{
						if(k!=i && k!=j)
						{
							list.set(bCount++, bTempData.get(k));
						}
					}
					assert(bCount==3);

					list.set(bCount++, bTempData.get(i));
					list.set(bCount++, bTempData.get(j));

					return true;
				}
			}
		}

		return false;
	}

	//逻辑数值
	public static  Integer GetCardLogicValue(Integer cbCardData)
	{
		//扑克属性
		Integer bCardValue=GetCardValue(cbCardData);

		//转换数值
		return (bCardValue>10)?(10):bCardValue;
	}


	
	public static Comparator<Integer> sorter = (left, right) -> {
		Integer v1 = NNGameLogic.GetCardValue( left );
		Integer v2 = NNGameLogic.GetCardValue( right );
		if (v1 == v2)
		{
			return NNGameLogic.GetCardColor( right ) - NNGameLogic.GetCardColor( left ) ;
		}
		return  v2 -  v1;
	};
	
	@SuppressWarnings("unchecked")
	public static  boolean CompareCard( ArrayList<Integer> cbLeftData,  ArrayList<Integer> cbRightData)
	{
		if (cbLeftData.size() < 3 || cbLeftData.size() > MAX_COUNT)
		{
			return false;
		}
		//这里先进行倍数比较
		int leftType = NNGameLogic.GetCardType(cbLeftData);
		int RightType = NNGameLogic.GetCardType(cbRightData);

		

		if(leftType!=RightType)
			return leftType > RightType;

	  
		ArrayList<Integer> tempLeftData = (ArrayList<Integer>) cbLeftData.clone() ;
		ArrayList<Integer> tempRightData = (ArrayList<Integer>) cbRightData.clone();
		
		tempLeftData.sort(sorter);
		tempRightData.sort(sorter);

		Integer cbLeftMaxValue= NNGameLogic.GetCardValue(tempLeftData.get(0));
		Integer cbRightMaxValue= NNGameLogic.GetCardValue(tempRightData.get(0));
		if( cbLeftMaxValue != cbRightMaxValue ) {
			return cbLeftMaxValue > cbRightMaxValue;
		}
		//比较颜色
		return GetCardColor(tempLeftData.get(0)) > GetCardColor(tempRightData.get(0));
	}
}
