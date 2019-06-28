package com.wuzai.os.dynamic.constants;

public class CacheConstants {

	//===============时间变量配置，开始===========================
	/**秒数*/
	public final static int SECOND = 1;
	/**分*/
	public final static int MINUTE = SECOND*60;
	/**30分*/
	public final static int THIRTY_MINUTE = MINUTE*30;
	/**一小时的秒数*/
	public final static int HOURS= MINUTE*60;
	/**6小时的秒数*/
	public final static int SIX_HOURS=6*HOURS;
	/**12小时的秒数*/
	public final static int TWELVE_HOURS=SIX_HOURS*2;
	/**24小时的秒数*/
	public final static int ONE_DAY=TWELVE_HOURS*2;
	/**30天的秒数*/
	public final static int THIRTY_DAY=ONE_DAY*30;
	//===============时间变量配置，结束===========================

	//===============所有的缓存Key的配置，开始=====================
	/**数据源存储区域*/
	public static final String DATA_SOURCES_REGION="DATA_SOURCES_REGION";
	/**数据源容器缓存*/
	public static final String DATASOURCE="DATASOURCE";






}
