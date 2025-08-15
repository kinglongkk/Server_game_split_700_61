package business.global.pk.nn;

import java.util.*;
import java.util.stream.Collectors;

import BaseCommon.CommLog;

public class BasePockerLogic extends BasePocker{
	//排序 大到小
	public static Comparator<Integer> sorter = (left, right) -> {
		Integer v1 = getCardValue( left );
		Integer v2 = getCardValue( right );
		if (v1 == v2)
		{
			return getCardColor( right ) - getCardColor( left ) ;
		}
		return  v2 -  v1;
	};

	/* pockerType 获取对子 获取三条 获取四张一样 （炸弹）
	 * inList 传入数组
	 * outList 返回的对子数组
	 *return 数量
	*/
	public static int  getSameCardByType(ArrayList< ArrayList<Integer> > outList, ArrayList<Integer> inList, PockerValueType pockerType){
		int count  = 0;

		ArrayList<Integer> valueList = sort(inList, false);
		for(int i = 0 ; i < valueList.size(); i++){
			ArrayList<Integer> temp = new ArrayList<Integer>(4);
			Integer tmp = valueList.get(i);
			//获取对子
			if( (i+1 < valueList.size() )
					&& PockerValueType.POCKER_VALUE_TYPE_SUB == pockerType
					&& getCardValue(tmp) == getCardValue(valueList.get(i+1))){
				temp.add(tmp);
				temp.add(valueList.get(i+1));
				outList.add((ArrayList<Integer>) temp.clone());
				temp.clear();
				i++;
				count++;
			//获取三条
			}else if( (i+2 < valueList.size() )
					&& (PockerValueType.POCKER_VALUE_TYPE_THREE == pockerType )
					&& (getCardValue(tmp) == getCardValue(valueList.get(i+1)))
					&& (getCardValue(tmp) == getCardValue(valueList.get(i+2)))){
				temp.add(tmp);
				temp.add(valueList.get(i+1));
				temp.add(valueList.get(i+2));
				outList.add((ArrayList<Integer>) temp.clone());
				temp.clear();
				i += 2;
				count++;
			//获取四张一样 （炸弹）
			}else if( (i+3 < valueList.size() )
					&&  (PockerValueType.POCKER_VALUE_TYPE_BOMB == pockerType )){
				temp.add(tmp);
				while (i+1 < valueList.size() &&  getCardValue(tmp) == getCardValue(valueList.get(i+1))) {
					temp.add(valueList.get(i+1));
					i++;
				}
				if (temp.size() >= PockerValueType.POCKER_VALUE_TYPE_BOMB.value() + 1) {
					outList.add((ArrayList<Integer>) temp.clone());
					count++;
				}
				temp.clear();
			}
		}

		return count;
	}

	/* pockerType 获取对子 获取三条 获取四张一样 （炸弹）
	 * inList 传入数组
	 * outList 返回的对子数组
	 *return 数量
	*/
	public static int  getSameCardByTypeEx(ArrayList< ArrayList<Integer> > outList, ArrayList<Integer> inList, PockerValueType pockerType){
		int count  = 0;

		ArrayList<Integer> valueList = sort(inList, false);
		for(int i = 0 ; i < valueList.size(); i++){
			ArrayList<Integer> temp = new ArrayList<Integer>(4);
			Integer tmp = valueList.get(i);
			//获取单张
			if (PockerValueType.POCKER_VALUE_TYPE_SINGLE == pockerType
					&& (valueList.size() > i + 1 && getCardValue(tmp) != getCardValue(valueList.get(i+1))  || i+1 >= valueList.size() )) {
				temp.add(tmp);
				outList.add((ArrayList<Integer>) temp.clone());
				count++;
			}
			//获取对子
			else if( (i+1 < valueList.size() )
					&& PockerValueType.POCKER_VALUE_TYPE_SUB == pockerType
					&& getCardValue(tmp) == getCardValue(valueList.get(i+1))
					&& (valueList.size() > i + 2 && getCardValue(tmp) != getCardValue(valueList.get(i+2))  || i+2 >= valueList.size() ) ){
				temp.add(tmp);
				temp.add(valueList.get(i+1));
				outList.add((ArrayList<Integer>) temp.clone());
				temp.clear();
				i++;
				count++;
			//获取三条
			}else if( (i+2 < valueList.size() )
					&& (PockerValueType.POCKER_VALUE_TYPE_THREE == pockerType )
					&& (getCardValue(tmp) == getCardValue(valueList.get(i+1)))
					&& (getCardValue(tmp) == getCardValue(valueList.get(i+2)))
					&& (valueList.size() > i + 3 && getCardValue(tmp) != getCardValue(valueList.get(i+3))  || i+3 >= valueList.size() ) ){
				temp.add(tmp);
				temp.add(valueList.get(i+1));
				temp.add(valueList.get(i+2));
				outList.add((ArrayList<Integer>) temp.clone());
				temp.clear();
				i += 2;
				count++;
			//获取四张一样 （炸弹）
			}else if( (i+3 < valueList.size() )
					&&  (PockerValueType.POCKER_VALUE_TYPE_BOMB == pockerType ) ){
				temp.add(tmp);
				while (i+1 < valueList.size() &&  getCardValue(tmp) == getCardValue(valueList.get(i+1))) {
					temp.add(valueList.get(i+1));
					i++;
				}
				if (temp.size() >= PockerValueType.POCKER_VALUE_TYPE_BOMB.value() + 1) {
					outList.add((ArrayList<Integer>) temp.clone());
					temp.clear();
					count++;
				}
			}
		}

		return count;
	}

	/*
	 * 获取顺子
	 * 	inList 传入数组
	 * 	outList 返回的对子数组
	 *	return 数量
	 * **/
	public static int getShunZi(ArrayList< ArrayList<Integer> > outList, ArrayList<Integer> inList){
		int count = 0, idx = 0;

		ArrayList<Integer> valueList = deleteValueEqual(inList);
		System.out.printf("%s", valueList.toString());
		ArrayList<Integer> temp = new ArrayList<Integer>(5);
		for(int i = 0 ; i < valueList.size(); i++){
			temp.add(valueList.get(i));
			if(idx == 0){
				idx++;
				continue;
			}
			if(i == valueList.size() - 1)
			{
				if(temp.size() >= MIN_FLUSH_COUNT){
					outList.add((ArrayList<Integer>) temp.clone());
					count++;
				}
				continue;
			}
			if(Math.abs(getCardValue(temp.get(idx)) - getCardValue(temp.get(idx-1))) == 1   ){
				idx++;
			}else{
				if(temp.size() >= MIN_FLUSH_COUNT){
					temp.remove(idx);
					outList.add((ArrayList<Integer>) temp.clone());
					temp.clear();
					count++;
					idx = 0;
					i--;
				}else{
					idx = 0;
				}
			}
		}
		return count;
	}

	/*
	 * 获取顺子  不重复 固定长度
	 * 	inList 传入数组
	 * 	outList 返回的对子数组
	 *	return 数量
	 * **/
	public static int getShunZiEx(ArrayList< ArrayList<Integer> > outList, ArrayList<Integer> inList, int Count){
		ArrayList<Integer> valueList = deleteValueEqual(inList);
		//System.out.printf("%s", valueList.toString());

		for(int i = 0 ; i <= valueList.size() - Count; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>(Count);
			Integer value = valueList.get(i);
			temp.add(value);
			for(int j = 1; j < Count; j++){
				if(Math.abs(getCardValue(valueList.get(i+j)) - getCardValue(valueList.get(i+j-1))) != 1  ){
					break;
				}
				temp.add(valueList.get(i+j));
			}
			if(temp.size() != Count){
				continue;
			}
			outList.add(temp);
		}
		return outList.size();
	}

	/*
	 * 获取同花顺子
	 * 	inList 传入数组
	 * 	outList 返回的对子数组
	 *	return 数量
	 * **/
	public static int getTongHuaShun(ArrayList< ArrayList<Integer> > outList, ArrayList<Integer> inList){
		int count = 0, idx = 0;
		outList.clear();
		ArrayList<Integer> valueList = sort(inList, true);
		ArrayList<Integer> temp = new ArrayList<Integer>(8);
		for(int i = 0 ; i < valueList.size(); i++){
			temp.add(valueList.get(i) );
			if(idx == 0){
				idx++;
				continue;
			}
			if(Math.abs(getCardValue(temp.get(idx)) - getCardValue(temp.get(idx-1))) == 1
					&& getCardColor(temp.get(idx)) == getCardColor(temp.get(idx-1))) {
				idx++;
			}else{
				if(temp.size() >= MIN_FLUSH_COUNT){
					temp.remove(idx);
					outList.add((ArrayList<Integer>) temp.clone());
					temp.clear();
					count++;
					idx = 0;
					i--;
				}else{
					idx = 0;
				}
			}
		}
		return count;
	}

	/*
	 * 获取同花
	 * 	inList 传入数组
	 * 	outList 返回的对子数组
	 *	return 数量
	 * **/
	public static int getTongHua(ArrayList< ArrayList<Integer> > outList, ArrayList<Integer> inList){
		int count = 0, idx = 0;

		ArrayList<Integer> valueList = sort(inList, true);
		ArrayList<Integer> temp = new ArrayList<Integer>(8);
		for(int i = 0 ; i < valueList.size(); i++){
			temp.add(valueList.get(i));
			if(idx == 0){
				idx++;
				continue;
			}
			if(i == valueList.size() - 1 ){
				outList.add((ArrayList<Integer>) temp.clone());
				count++;
				continue;
			}
			if(getCardColor(temp.get(idx)) == getCardColor(temp.get(idx-1) )){
				idx++;
			}else{
				temp.remove(idx);
				outList.add((ArrayList<Integer>) temp.clone());
				temp.clear();
				i--;
				count++;
				idx = 0;
			}
		}
		return count;
	}

	/*
	 * 去掉相同值的牌
	 * */
	public static ArrayList<Integer> deleteValueEqual(ArrayList<Integer> pockerList){
		ArrayList<Integer> List =  sort(pockerList, false);
		ArrayList<Integer> outList = new ArrayList<Integer>();
		for(int i = 0 ; i < List.size(); i++){
			outList.add( List.get(i));
			while(i < List.size() - 1  && getCardValue(List.get(i)) == getCardValue(List.get(i+1)) ){
				i++;
			}
		}

		return outList;
	}

	/**
	 * 某一张牌的个数
	 */
	public static int getCardCount(ArrayList<Integer> cardList, Integer card, boolean isValueEqual) {
		int count = 0;
		try {

			int num = cardList.size();
			for (int i = 0; i < num; i++) {
				if (i >= cardList.size()) {
					continue;
				}
				Integer Integer1 = cardList.get(i);
				if (isValueEqual && BasePocker.getCardValue(Integer1) == BasePocker.getCardValue(card)) {
					count++;
				} else if (!isValueEqual && Integer1 == card) {
					count++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return count;
		}

	}

	/*
	 * 删除某一张牌
	 * */
	public static int  deleteSameCard(ArrayList<Integer> cardList, Integer card, boolean isValueEqual ) {
		int count = 0;
		for (int i = 0 ; i < cardList.size(); i++ ) {
			if(isValueEqual && BasePocker.getCardValue(cardList.get(i)) == BasePocker.getCardValue(card)){
				count++;
				cardList.remove(i);
			}else if (!isValueEqual && cardList.get(i) == card) {
				count++;
				cardList.remove(i);
			}
		}
		return count;
	}

	/*
	 * 获取相同的牌
	 * */
	public static ArrayList<Integer>  getSameCard(ArrayList<Integer> cardList, Integer card, boolean isValueEqual ) {
		ArrayList<Integer>  temp = new ArrayList<Integer>();
		for (int i = 0 ; i < cardList.size(); i++ ) {
			if(isValueEqual && BasePocker.getCardValue(cardList.get(i)) == BasePocker.getCardValue(card)){
				temp.add(cardList.get(i));
			}else if (!isValueEqual && cardList.get(i) == card) {
				temp.add(cardList.get(i));
			}
		}
		return temp;
	}

	/*
	 * 获取固定长度的顺子  比如 3456789 获取固定长度为5 有数字34567 45678 56789
	 *	inList 传入数组
	 * 	outList 返回的对子数组
	 * 	count 固定长度
	 *	return 数量
	 * */
	public static int getShunZiByCount(ArrayList< ArrayList<Integer> > outList, ArrayList<Integer> inList, int Count){
		ArrayList<Integer> list =  sort(inList, false);
		ArrayList< ArrayList<Integer> > equallist = new ArrayList< ArrayList<Integer> >();
		getPockerEqualValue(equallist, list);
		ArrayList< ArrayList<Integer> > tempList = new ArrayList< ArrayList<Integer> >(ONE_COLOR_POCKER_COUNT);
		getShunZiEx(tempList, list, Count);

		for(int i = 0; i < tempList.size(); i++){
			ArrayList<Integer> temp = tempList.get(i);
			ArrayList< ArrayList<Integer> > tempShunZilist =  new ArrayList< ArrayList<Integer> >(ONE_COLOR_POCKER_COUNT);
			tempShunZilist.add((ArrayList<Integer>) temp.clone());
			for(int j = 0; j < Count; j++){
				int num = tempShunZilist.size();
				Integer value = temp.get(j);
				ArrayList<Integer> equal = getSameCard(list, getCardValue(value), true);
 				if(null == equal || (null != equal && equal.size() == 0)){
					continue;
				}
				for(int k = 0; k < num; k++){
					ArrayList<Integer> tmp = (ArrayList<Integer>) tempShunZilist.get(k).clone();
					for(int h = 0; h < equal.size(); h++){
						if(equal.get(h) == value || equal.get(h) == null){
							continue;
						}
						tmp.set(j, equal.get(h));
						tempShunZilist.add((ArrayList<Integer>) tmp.clone());
					}
				}
			}
			outList.addAll(tempShunZilist);
		}

		//System.out.print("outList:");
		print( outList);
		//System.out.print("outList:\n");

		rechecking(outList);
		return outList.size();
	}

	//查找重复的
	public static void rechecking(ArrayList< ArrayList<Integer> > outList){
		for(int i = 0; i < outList.size() ; i++ ){
			ArrayList<Integer>  tempList = outList.remove(i);
			if(!outList.contains(tempList)){
				outList.add(tempList);
			}else{
				System.out.printf("重复 %s", tempList);
			}
		}
	}

	public static void recursionAdd(ArrayList< ArrayList<Integer> > outList, ArrayList<Integer> list, int  Count){
		ArrayList< ArrayList<Integer> > tempList = new ArrayList< ArrayList<Integer> >();
		tempList.add((ArrayList<Integer>) outList.clone());
		for(int i = 0; i < tempList.size(); i++){
			ArrayList<Integer> needList = tempList.get(i);
			outList.add((ArrayList<Integer>) needList.clone());

			for(int j = 0; j < Count; j++){
				Integer value = needList.get(j);
				for(int k = 0; k < list.size(); k++){
					Integer temp = list.get(k);
					if(getCardValue(value) == getCardValue(temp) && value != temp){
						needList.set(j, temp);
						outList.add((ArrayList<Integer>) needList.clone());

					}
				}
			}
		}
		recursionAdd(outList, list, Count);
	}

	/* 相同的牌放到一个数组中
	 * inList 传入数组
	 * outList 返回的对子数组
	 *return 数量
	*/
	public static int  getPockerEqualValue(ArrayList< ArrayList<Integer> > outList, ArrayList<Integer> inList){
		int count  = 0;

		ArrayList<Integer> valueList = sort(inList, false);

		for(int i = 0 ; i < valueList.size();){
			Integer tmp = valueList.get(i);
			int num = getCardCount(valueList, valueList.get(i), true);
			if(num <= 1) {
				i++;
				continue;
			}
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			for (int j = 0; j < num; j++) {
				Integer card = 0;
				if(i+j < valueList.size()) {
					card = valueList.get(i+j);
				}else{
					CommLog.error("getPockerEqualValue i="+i+",j="+j+",size="+valueList.size()+",card="+valueList.get(i));
				}
				if(card <= 0) {
					continue;
				}
				tempList.add(card);
			}
			i += num;
			count++;
			outList.add(tempList);
		}
		return count;
	}

	/*
	 * 排序
	 *  flag 是否加入花色排序 true:按花色 false：不按花色
	 *  return 返回的排序后的数组
	 * */
	public static ArrayList<Integer> sort(ArrayList<Integer> pockerList, boolean flag){
		ArrayList<Integer> outList = (ArrayList<Integer>) pockerList.clone();
		for(int i = 0; i < outList.size() -1; i++){
			for(int j = 0; j < outList.size() - 1 - i; j++){
				Integer Integer2 = outList.get(j+1);
				Integer Integer1 = outList.get(j);
				Integer temp = Integer1;
				if(flag){
					if(getCardColor(Integer1) != getCardColor(Integer2)){
						if(getCardColor(Integer1) > getCardColor(Integer2)){
							outList.set(j, Integer2);
							outList.set(j+1, temp);
						}
					}else{
						if(getCardValue(Integer1) > getCardValue(Integer2)){
							outList.set(j, Integer2);
							outList.set(j+1, temp);
						}
					}
				}else{
					if(getCardValue(Integer1) != getCardValue(Integer2)){
						if(getCardValue(Integer1) > getCardValue(Integer2)){
							outList.set(j, Integer2);
							outList.set(j+1, temp);
						}
					}else{
						if(getCardColor(Integer1) > getCardColor(Integer2)){
							outList.set(j, Integer2);
							outList.set(j+1, temp);
						}
					}
				}
			}
		}
		return outList;
	}

	/*
	 * 获取乱牌
	 * normalcount 加几幅普通牌
	 * trumpCount 带几对大小王
	 * */
	public static ArrayList<Integer> getRandomPockerList(int normalCount  , int trumpCount , PockerListType pocketListType){
		int total = MAX_NORMAL_POCKER * normalCount + trumpCount * MAX_TRUMP_POCKER;
		ArrayList<Integer> list = new ArrayList<Integer>(total);
		for(int i = 0; i  < normalCount ; i++){
			if(PockerListType.POCKERLISTTYPE_AFIRST == pocketListType) {
				list.addAll(Arrays.asList(PockerList_AFirst));
			}else	if(PockerListType.POCKERLISTTYPE_AEND == pocketListType) {
					list.addAll(Arrays.asList(PockerList_AEnd));
			} else {
				list.addAll(Arrays.asList(PockerList_TWOEnd));
			}
		}
		for(int i = 0; i  < trumpCount ; i++){
			list.addAll(Arrays.asList(Trump_PockerList));
		}
		Collections.shuffle(list);
		return list;
	}

	/*
	 *获取赖子
	 * **/
	public static Integer getRandomRazz(PockerListType pocketListType){
		int index = (int) (Math.random() * ONE_COLOR_POCKER_COUNT );
		Integer cardValue = -1;
		if(PockerListType.POCKERLISTTYPE_AFIRST == pocketListType) {
			cardValue = BasePocker.getCardValue(PockerList_AFirst[index]);
		}else	if(PockerListType.POCKERLISTTYPE_AEND == pocketListType) {
			cardValue = BasePocker.getCardValue(PockerList_AEnd[index]);
		} else {
			cardValue = BasePocker.getCardValue(PockerList_TWOEnd[index]);
		}
		return cardValue;
	}

	/*
	 * 打印二维数组
	 * */
	public static void print(ArrayList< ArrayList<Integer> > list){
		System.out.print("ArrayList< ArrayList<Integer> > start list:");
		for(int i = 0 ; i < list.size(); i++ ){
			System.out.print(list.get(i).toString());
			System.out.print(" \n");
		}
		System.out.print(" \n");
	}

	/**
	 * 删除牌组信息
	 * @return
	 */
	public static boolean deleteCard(ArrayList<Integer> cradList, ArrayList<Integer> deleteCardList) {
		boolean flag=true;
		List<Integer> notRepeat=deleteCardList.stream().distinct().collect(Collectors.toList());
		//检测牌的合理性 是否重复
		if(notRepeat.size()!=deleteCardList.size()){
			return false;
		}
		//判断要删除的牌是否都在
		for (Integer Integer1 : deleteCardList) {
			if(!cradList.contains(Integer1)){
				flag=false;
			}
		}
		if(flag){//牌都在的话删除
			cradList.removeAll(deleteCardList);
		}
		return flag;
	}


//	public static void permutateSequence(char[] strArrs,int i){
//        char temp;
//        if(strArrs==null||i>strArrs.length||i<0){
//            return;
//        }
//        else if(i==strArrs.length){
//           System.out.println(strArrs);
//       }
//       else{
//           for(int j=i;j<strArrs.length;j++){
//               temp = strArrs[j];//
//               strArrs[j] = strArrs[i];
//               strArrs[i] = temp;
//               permutateSequence(strArrs, i+1);
//               temp = strArrs[j];//
//               strArrs[j] = strArrs[i];
//               strArrs[i] = temp;
//           }
//       }
//   }


	//debug
	public static void main(String[] agrs) {
//		ArrayList<Integer> list = getRandomPockerList(1, 0, false);
//		System.out.printf("list=%s \n", list.toString());
//		System.out.print("\n");
//
//		//char strArrs[] = {'d', 'a', 'c', 'e'};
//        //permutateSequence(strArrs, 0);
//		Integer[] list1 = {0x02, 0x12,  0x03, 0x13,  0x04, 0x14, 0x24, 0x05, 0x15, 0x06, 0x16, 0x07, 0x17};
//		list.clear();
//		list.addAll(Arrays.asList(list1));
//		System.out.printf("list=%s \n", list.toString());
//		System.out.print("\n");
//		ArrayList< ArrayList<Integer> > outList = new ArrayList< ArrayList<Integer> >(ONE_COLOR_POCKER_COUNT);
//		getShunZiByCount(outList, list, 5);
//		print( outList);
//		System.out.print("\n");
//		outList.clear();
//		getShunZi(outList, list);
//		print( outList);
//		System.out.print("\n");
//		outList.clear();
//		getFlush(outList, list);
//		print( outList);
//		System.out.print("\n");
//		outList.clear();
//		getPockerValueType(outList, list, PockerValueType.POCKER_VALUE_TYPE_BOMB);
//		print( outList);
//		System.out.print("\n");
//		outList.clear();
//		getPockerValueType(outList, list, PockerValueType.POCKER_VALUE_TYPE_THREE);
//		print( outList);
//		System.out.print("\n");
//		outList.clear();
//		getPockerValueType(outList, list, PockerValueType.POCKER_VALUE_TYPE_SUB);
//		print( outList);
//		System.out.print("\n");
	}
}
