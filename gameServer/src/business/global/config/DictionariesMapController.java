package business.global.config;

import org.apache.commons.lang3.StringUtils;

import core.db.entity.clarkGame.DictionariesMapBO;
import core.db.other.Restrictions;
import core.db.service.clarkGame.DictionariesMapBOService;
import core.ioc.ContainerMgr;
public class DictionariesMapController {

	/**
	 * 获取key对应的配置
	 * */
	public static int  getIntByKey(String key) {
		return getIntByKey(key, 0);
	}
	
	/**
	 * 获取key对应的配置
	 * */
	public static int getIntByKey(String key, int iDefault) {
		int temp = 0;
		DictionariesMapBOService dictionariesMapBOService = ContainerMgr.get().getComponent(DictionariesMapBOService.class);
		DictionariesMapBO  dictionariesMapBo =  dictionariesMapBOService.findOne(Restrictions.eq("key", key),null);
		if (null != dictionariesMapBo) {
			String value = dictionariesMapBo.getValue();
			if (StringUtils.isNumeric(value)) {
				temp = Integer.valueOf(value);
			}
		}
		return temp;
	}
	
	/**
	 * 获取key对应的配置
	 * */
	public static long  getLongByKey(String key) {
		return getLongByKey(key, 0);
	}
	
	/**
	 * 获取key对应的配置
	 * */
	public static long  getLongByKey(String key, long iDefault) {
		long temp = 0;
		DictionariesMapBOService dictionariesMapBOService = ContainerMgr.get().getComponent(DictionariesMapBOService.class);
		DictionariesMapBO  dictionariesMapBo =  dictionariesMapBOService.findOne(Restrictions.eq("key", key),null);
		if (null != dictionariesMapBo) {
			String value = dictionariesMapBo.getValue();
			if (StringUtils.isNumeric(value)) {
				temp = Long.valueOf(value);
			}
		}
		return temp;
	}
	
	/**
	 * 获取key对应的配置
	 * */
	public static float  getFloatByKey(String key) {
		return getFloatByKey(key, 0);
	}
	
	/**
	 * 获取key对应的配置
	 * */
	public static float  getFloatByKey(String key, float iDefault) {
		float temp = 0;
		DictionariesMapBOService dictionariesMapBOService = ContainerMgr.get().getComponent(DictionariesMapBOService.class);
		DictionariesMapBO  dictionariesMapBo =  dictionariesMapBOService.findOne(Restrictions.eq("key", key),null);
		if (null != dictionariesMapBo) {
			String value = dictionariesMapBo.getValue();
			if (StringUtils.isNumeric(value)) {
				temp = Float.valueOf(value);
			}
		}
		return temp;
	}
	
	/**
	 * 获取key对应的配置
	 * */
	public static double  getDoubleByKey(String key) {
		return getDoubleByKey(key, 0);
	}
	
	/**
	 * 获取key对应的配置
	 * */
	public static double  getDoubleByKey(String key, double iDefault) {
		double temp = 0;
		DictionariesMapBOService dictionariesMapBOService = ContainerMgr.get().getComponent(DictionariesMapBOService.class);
		DictionariesMapBO  dictionariesMapBo =  dictionariesMapBOService.findOne(Restrictions.eq("key", key),null);
		if (null != dictionariesMapBo) {
			String value = dictionariesMapBo.getValue();
			if (StringUtils.isNumeric(value)) {
				temp = Double.valueOf(value);
			}
		}
		return temp;
	}
	
	/**
	 * 获取key对应的配置
	 * */
	public static boolean  getBooleanByKey(String key) {
		return getBooleanByKey(key, false);
	}
	
	/**
	 * 获取key对应的配置
	 * */
	public static boolean  getBooleanByKey(String key, boolean iDefault) {
		boolean temp = false;
		DictionariesMapBOService dictionariesMapBOService = ContainerMgr.get().getComponent(DictionariesMapBOService.class);
		DictionariesMapBO  dictionariesMapBo =  dictionariesMapBOService.findOne(Restrictions.eq("key", key),null);
		if (null != dictionariesMapBo) {
			String value = dictionariesMapBo.getValue();
			if (StringUtils.isNumeric(value)) {
				temp = "0".equals(value) ? false : true;
			}else{
				temp = "true".equals(value) ? true : false;
			}
		}
		return temp;
	}
	
	/**
	 * 获取key对应的配置
	 * */
	public static String  getStringByKey(String key) {
		return getStringByKey(key, "");
	}
	
	/**
	 * 获取key对应的配置
	 * */
	public static String  getStringByKey(String key, String iDefault) {
		String temp = "";
		DictionariesMapBOService dictionariesMapBOService = ContainerMgr.get().getComponent(DictionariesMapBOService.class);
		DictionariesMapBO  dictionariesMapBo =  dictionariesMapBOService.findOne(Restrictions.eq("key", key),null);
		if (null != dictionariesMapBo) {
			String value = dictionariesMapBo.getValue();
			if (!StringUtils.isEmpty(value)) {
				temp = value;
			}
		}
		return temp;
	}
}
