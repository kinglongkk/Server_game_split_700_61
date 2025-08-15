package core.server;

import BaseCommon.CommLog;
import BaseServer.Monitor;
import ConsoleTask.ConsoleTaskManager;
import business.global.GM.MaintainServerMgr;
import business.global.club.ClubMgr;
import business.global.config.*;
import business.global.family.FamilyManager;
import business.global.redBagActivity.ActivityManager;
import business.global.room.PlayBackMgr;
import business.global.room.key.GoldKeyMgr;
import business.global.room.key.RoomKeyMgr;
import business.global.room.sharekey.ShareCurrencyKeyMgr;
import business.global.room.sharekey.ShareGoldKeyMgr;
import business.global.room.sharekey.ShareRoomKeyMgr;
import business.global.sharegm.ShareInitMgr;
import business.global.sharegm.ShareNodeServerLogicMgr;
import business.global.sharegm.ShareNodeServerMgr;
import business.global.shareroom.ShareRoomMgr;
import business.global.union.UnionMgr;
import business.player.PlayerMgr;
import business.player.Robot.RobotMgr;
import business.rocketmq.TryConnectNotify;
import business.shareplayer.SharePlayerMgr;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.IApp;
import com.ddm.server.common.ehcache.EhCacheFactory;
import com.ddm.server.common.mgr.sensitive.SensitiveWordMgr;
import com.ddm.server.common.mongodb.MongoDbMgr;
import com.ddm.server.common.redis.RedisMgr;
import com.ddm.server.common.rocketmq.MqConsumerMgr;
import com.ddm.server.common.rocketmq.MqProducerMgr;
import com.ddm.server.common.task.TaskMgr;
import com.ddm.server.http.server.HttpDispather;
import com.ddm.server.http.server.MGHttpServer;
import com.ddm.server.mq.factory.MqConsumerTopicFactory;
import com.sun.net.httpserver.HttpServer;
import core.config.refdata.RefDataMgr;
import core.db.DataBaseMgr;
import core.db.mgr.Game_DBVersionManager;
import core.db.mgr.Log_DBVersionManager;
import core.db.other.DBFlowMgr;
import core.dispatch.DispatcherComponent;
import core.dispatcher.HandlerServerDispatcher;
import core.ioc.ContainerMgr;
import core.logger.flow.FlowLogger;
import core.logger.flow.GameFlowLogger;
import core.logger.flow.disruptor.log.BatchDbLogComponent;
import core.network.client2game.ClientAcceptor;
import core.network.http.handler.Recharge;
import core.timing.TimingProcessor;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServer extends IApp {
	
	public static void main(String[] args) throws Exception {
		GameServer app = new GameServer();
		app.init(args);
		app.start();
	}

	@Override
	protected void beforeInit(String configdit) {
        loadBasicConfig(configdit, "logback_game.xml");
		loadRemoteConfig("server_id", System.getProperty("game_sid"), configdit, "game.properties");
		initLog4J(configdit);
		initORM(configdit);
		initRedis(configdit);
		initTask(configdit);
		initMq(configdit);
//		initMongoDb(configdit);
	}

	@Override
	protected boolean initBase() {

		// 基础管理器初始化
		ConsoleTaskManager.GetInstance().setRunner(new BshRunner()); //System.in控制台监听线程
		CommLogD.info("===========基础管理器初始化完成==============");
		RefDataMgr.getInstance().reload(); //配置表加载

		SensitiveWordMgr.getInstance().init(RefDataMgr.getInstance().getRefPath() + File.separator + "keywords.json");
		SensitiveWordMgr.getInstance().reload();  //敏感词检测

		// 检查并且更新版本
		Game_DBVersionManager.getInstance().checkAndUpdateVersion("core.db.entity.clarkGame");
		DBFlowMgr.getInstance().updateDB("core.db.entity.clarkLog");

		// db 自动更新
		Game_DBVersionManager.getInstance().setNewestVersion("1.0.0.1");
		Log_DBVersionManager.getInstance().setNewestVersion("1.0.0.1");

		if (!Game_DBVersionManager.getInstance().runAutoVersionUpdate("core.db.version.update.clarkGame")
				|| !Log_DBVersionManager.getInstance().runAutoVersionUpdate("core.db.version.update.clarkLog")) {
			CommLogD.error("!!!!!!!!! 数据库版本升级失败 !!!!!!!!!!");
			return false;
		}

		// 初始化流水日志
		FlowLogger.getInstance().init(GameFlowLogger.class, "core.logger.flow.impl");
		// 初始化开服时间
		OpenSeverTime.getInstance().init();
		// 注册内存清理策略
		Monitor.getInstance().regCleanMemory(CleanMemory.GetInstance());
		// 获取游戏列表
		GameListConfigMgr.getInstance().init();
		// 默认的日志数据库派发中心实现
		BatchDbLogComponent.getInstance().init();
		// 默认的消息派发中心实现
		DispatcherComponent.getInstance().init();
		// 缓存管理器初始化
		EhCacheFactory.getInstance().initalize();
		// 初始化服务接口
		HandlerServerDispatcher.getInstance().init();
		return true;
	}

	@Override
	protected boolean initNetwork() {
		//启动mina
		Optional<String> socketTypeOp = Optional.ofNullable(System.getProperty("socketType"));
		int socketType = Integer.valueOf(socketTypeOp.orElse("1"));
		ClientAcceptor.getInstance().init(socketType);
		ClientAcceptor.getInstance().startSocket(Integer.getInteger("GameServer.ClientPort"));
		starHttp();
		return true;
	}

	@Override
	protected boolean initLogic() {
		// 初始化订阅主题
		MqConsumerTopicFactory.getInstance().init(new TryConnectNotify());
		// 清除节点在线玩家,重启的情况
		SharePlayerMgr.getInstance().init();
		//清除节点共享房间,重启的情况
		ShareRoomMgr.getInstance().init();
		// 初始化缓存key
		ShareInitMgr.getInstance().init();
		// 初始化通用key
		CurrencyKeyMgr.getInstance().init();
		// 初始化房间key
		RoomKeyMgr.getInstance().init();
		// 初始化练习场key
		GoldKeyMgr.getInstance().init();
		// 初始化房间共享key
		ShareRoomKeyMgr.getInstance().init();
		// 初始化练习场房间共享key
//		ShareGoldKeyMgr.getInstance().init();
		// 初始化亲友圈赛事共享key
		ShareCurrencyKeyMgr.getInstance().init();
		// 服务器维护
		MaintainServerMgr.getInstance().init();
		// 玩家管理
		PlayerMgr.getInstance().init();
		// 折扣活动
		DiscountMgr.getInstance().init();
		// 工会管理
		FamilyManager.getInstance().init();
		// 初始化俱乐部
		ClubMgr.getInstance().init();
		// 初始化联赛
		UnionMgr.getInstance().init();
		//红包活动
		ActivityManager.getInstance().init();
		// 初始化机器人
		RobotMgr.getInstance().init();
		// 活动任务配置管理
		TaskConfigMgr.getInstance().init();
		// 初始化配置
		LuckDrawConfigMgr.getInstance().init();
		// 初始回放key
		PlayBackMgr.getInstance().init();


		return true;
	}

	@Override
	protected void afterInit() {
		// 共享节点启动逻辑初始化
		ShareNodeServerLogicMgr.getInstance().initLogic();
		// 共享节点启动初始化
		ShareNodeServerMgr.getInstance().init();
		// 切换端口
		changePort(30000);

	}

	@Override
	protected void start() {
		// HeartBeat.start();
	}

	/**
	 * 开启定时器
	 *
	 * @param timer 时间
	 */
	public void changePort(long timer) {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.schedule(() -> {
			GameListConfigMgr.getInstance().updateAllPort();
		}, timer, TimeUnit.MILLISECONDS);
	}

	public void starHttp() {
		try {
			HttpDispather dispather = new HttpDispather();
			String pack = Recharge.class.getPackage().getName();
			dispather.init(pack);
			MGHttpServer.getInstance().createServer(Integer.getInteger("Server.HttpServer", 9888), dispather,"/");
		} catch (Exception e) {
			CommLogD.error("启动Http错误", e);
			System.exit(-1);
		}
	}

	public static void reload() {
		RefDataMgr.getInstance().reload();
		SensitiveWordMgr.getInstance().reload();
	}

	/**
	 * 对象注入，动态代理service，数据源初始化
	 * @param configdit
	 */
	public void initORM(String configdit){
		loadRemoteConfig("server_id", System.getProperty("game_sid"),configdit, "db.properties");
		initDataSource();
		try {
			ContainerMgr.get().init(DataBaseMgr.class.getPackage().getName());//容器反转
			ContainerMgr.get().autowired();//依赖注入service
			ContainerMgr.get().proxyObj();//service动态代理
			CommLogD.info("ioc and autowired and proxyObj success");
		}catch (Exception e){
			CommLogD.error("ioc or autowired or proxyObj fail:{}",e.getMessage());
		}
	}

	/**
	 * 初始化log4j
	 * @param configdit
	 */
	public void initLog4J(String configdit){
		loadRemoteConfig("server_id", System.getProperty("game_sid"),configdit, "log4j.properties");
		PropertyConfigurator.configure(System.getProperties());
	}

	/**
	 * 初始化redis
	 * @param configdit 相对路径
	 */
	public void initRedis(String configdit){
		try {
			RedisMgr.get().loadConfig(String.format("%s/redis.properties", configdit));
		} catch (Exception e) {
			CommLog.error("[GameServer]:[{}] error:{}","initRedis",e.getMessage(), e);
		}
	}

	/**
	 * 初始化mongodb
	 * @param configdit 相对路径
	 */
	public void initMongoDb(String configdit){
		try {
			MongoDbMgr.get().loadConfig(String.format("%s/mongodb.properties", configdit), "business");
		} catch (Exception e) {
			CommLog.error("[GameServer]:[{}] error:{}","initMongodb",e.getMessage(), e);
		}
	}
	
	/**
	 * 初始化task
	 * @param configdit
	 */
	public void initTask (String configdit) {
		doTask(TimingProcessor.class.getPackage().getName(), configdit);
	}

	/**
	 * 执行定时任务初始化
	 * @param packageName
	 * @param configdit
	 */
	private void doTask(String packageName, String configdit){
		try {
			ContainerMgr.get().init(packageName);
			TaskMgr.get().init(String.format("%s/task.properties", configdit));
			ContainerMgr.get().startTask();
			TaskMgr.get().startAll();
		} catch (Exception e) {
			CommLog.error("[GameServer]:[{}] error:{}", "initTask", e.getMessage(), e);
		}
	}
	

	/**
	 * 初始化数据源
	 */
	public void initDataSource(){
		String sourceList = String.valueOf(System.getProperties().get("sourceList"));
		if (sourceList != null && sourceList.length() > 0) {
			String[] sourcePre = sourceList.split(",");
			Arrays.asList(sourcePre).forEach(pre->{
				Map<String,String> properties =System.getProperties().entrySet().stream().filter(n->n.getKey()!=null&&String.valueOf(n.getKey()).startsWith(pre+".")).collect(HashMap::new, (m, v)->
						m.put(String.valueOf(v.getKey()).replace(pre+".",""), String.valueOf(v.getValue())),HashMap::putAll);
				try {
					DataBaseMgr.init(pre,properties);
					CommLogD.info("init dataSource name={} success",pre);
				}catch (Exception e){
					CommLogD.error("init dataSource error:{}",e.getMessage());
				}
			});
		}

	}

	/**
	 * 初始化mq
	 * @param configdit 相对路径
	 */
	public void initMq(String configdit){
		try {
			//生产者
			MqProducerMgr.get().loadConfig(String.format("%s/mq.properties", configdit));
			//消费者
			MqConsumerMgr.get().loadConfig(String.format("%s/mq.properties", configdit), "business.rocketmq");
			MqConsumerMgr.get().start();
		} catch (Exception e) {
			CommLog.error("[GameServer]:[{}] error:{}", "initMq", e.getMessage(), e);
		}
	}

}
