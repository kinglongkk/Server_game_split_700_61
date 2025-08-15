package business.global.pk.sss.utlis;

import business.sss.c2s.cclass.entity.Ranking;
import business.sss.c2s.cclass.entity.SimpleResult;
import business.sss.c2s.cclass.newsss.PockerCard;
import jsproto.c2s.cclass.pk.Victory;

import java.util.*;

/**
 * Created by Huaxing on 2017/5/11.
 */
public class PublicUtlis {


    /**
     * 顺子
     * @param numbers
     * @return
     */
    public static boolean isContinuous(List<Integer> numbers) {
    	double sum = 0;//等差数列求和
    	int count = 0;
    	if(numbers==null || numbers.size() <= 0){
            return false;
        }
    	List<Integer> list = new ArrayList<Integer>();
        for(int i=0;i<numbers.size();i++){
            count += numbers.get(i);
            list.add(numbers.get(i));
        }
        int len = list.size();
        PublicUtlis.getMaxSort(list);
        if (list.get(0) == 13 && list.get(len-1) == 1) {
        	list.remove(0);
        	list.add(0);
        	count -= 13;
        }
        Collections.sort(list);
        int num = len == 5 ? 4:2;
        
        if(Math.abs(list.get(0)-list.get(len-1))>num){
            return false;
        }
        for(int i=0; i<len-1; i++){
            if(list.get(i).equals(list.get(i + 1))){
                return false;
            }
        }
        
        sum = Double.parseDouble(String.valueOf(len*(list.get(0)+list.get(len-1))/2.0));
        if (count != sum) {
        	return false;
        }
        
        return true;
    }
    
    /**
     * 顺子列表
     * @param pockerCards
     * @return
     */
    public static List<Integer> isContinuousList(List<PockerCard> pockerCards) {

    	List<Integer> numbers = new ArrayList<Integer>();
      for (int i = 0,size = pockerCards.size() ;i<size;i++) {
          numbers.add(pockerCards.get(i).cardID);
      }
    	
      if (!isContinuous(numbers)) {
          return null;
      }
      getMaxSort(numbers);
      return numbers;
    }

    
    
    /**
     * 获取数字排序
     */
	public static void getSortCard(List<PockerCard> list) {
        // 字符串排序
        Collections.sort(list, new Comparator<PockerCard>() {

			@Override
			public int compare(PockerCard o1, PockerCard o2) {
                return new Integer(o1.cardID).compareTo(o2.cardID);
			}
        });
    }
    

	
    /**
     * 获取数字排序
     */
	public static void getSortA(List<Integer> list) {
        // 字符串排序
        Collections.sort(list);
        Collections.sort(list, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
            	// 金最前
            	if (o2 == 13 && o1 != 13) {
                    return 1;
                }
            	if (o1 == 13 && o2 != 13) {
                    return -1;
                }
                return new Integer(o1).compareTo(o2);
			}
        });
    }
	
    
    /**
     * 获取数字排序
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void getMaxSort(List list) {
        // 字符串排序
        Collections.sort(list);
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return new Integer((Integer) o2).compareTo(new Integer((Integer) o1));
            }
        });
    }
    /**
     * 获取数字排序
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void getSort(List list) {
        // 字符串排序
        Collections.sort(list);
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return new Integer((Integer) o1).compareTo(new Integer((Integer) o2));
            }
        });
    }


    /**
     * 获取重复数字
     * @return
     */
    public static List<Integer> getRepeat (List<Integer> repeat) {
    	List<Integer> repeat2 = new ArrayList<Integer>();
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        for (Integer integer : repeat) {
            if (hashMap.get(integer) != null){
                Integer value = hashMap.get(integer);
                hashMap.put(integer,value+1);
                repeat2.add(integer);
            } else {
                hashMap.put(integer, 1);
            }
        }
        return repeat2;
    }

    /**
     * 获取实体重复牌号
     * @param players
     * @return
     */
    public static List<Integer>  getRepeatPockerCard (List<PockerCard> players) {
    	List<Integer> repeat = new ArrayList<Integer>();
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        for (PockerCard string : players) {
            if (hashMap.get(string.cardID) != null) {
                Integer value = hashMap.get(string.cardID);
                hashMap.put(string.cardID, value+1);
                repeat.add(string.cardID);
            } else {
                hashMap.put(string.cardID, 1);
            }
        }
        return repeat;
    }

    /**
     * 获取实体重复花色
     * @param players
     * @return
     */
    public static List<Integer>  getRepeatPockerColor (List<PockerCard> players) {
    	List<Integer> repeat = new ArrayList<Integer>();
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        for (PockerCard string : players) {
            if (hashMap.get(string.type) != null) {
                Integer value = hashMap.get(string.type);
                hashMap.put(string.type, value+1);
                repeat.add(string.type);
            } else {
                hashMap.put(string.type, 1);
            }
        }
        return repeat;
    }
    
    
    
    /**
     * 判断是否有重复的元素
     * @param list
     * @return
     */
    public static boolean hasSame(List<? extends Object> list) {
        if(null == list) {
            return false;
        }
        return list.size() == new HashSet<Object>(list).size();
    }


    /**
     * 获取实体重复牌号
     * @param rankings
     * @return
     */
    public static List<Integer>  getRepeatRanking (List<Ranking> rankings) {
    	List<Integer> repeat = new ArrayList<Integer>();
        for (Ranking ranking : rankings) {
//            if (hashMap.get(ranking.getKey()) != null) {
//                Integer value = hashMap.get(ranking.getKey());
//                hashMap.put(ranking.getKey(), value+1);
                repeat.add(ranking.getKey());
//            } else {
//                hashMap.put(ranking.getKey(), 1);
//            }
        }
        return repeat;
    }

    
    /**
     * 获取实体重复牌号
     * @param rankings
     * @return
     */
    public static List<Integer>  getVictory(List<Victory> victories) {
    	List<Integer> repeat = new ArrayList<Integer>();
        for (Victory ranking : victories) {
                repeat.add(ranking.getPos());
        }
        return repeat;
    }
    
    /**
     * 两个值去最大值
     * @param i
     * @param j
     * @return
     */
    public static int getMax(int i,int j) {
        return i>j?i:j;
    }

    public static boolean isNull(SimpleResult simpleResult) {
        return simpleResult == null?false:true;
    }
    
    public static int chanceSelect(Map<Integer, Integer> keyChanceMap) {
        if (keyChanceMap == null || keyChanceMap.size() == 0) {
            return 0;
        }

        Integer sum = 0;
        for (Integer value : keyChanceMap.values()) {
            sum += value;
        }
        // 从1开始
        Integer rand = new Random().nextInt(sum) + 1;

        for (Map.Entry<Integer, Integer> entry : keyChanceMap.entrySet()) {
            rand -= entry.getValue();
            // 选中
            if (rand <= 0) {
                return entry.getKey();
            }
        }
        return 0;
    }

    


}