package org.konggradio.unicron.iot.bootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.konggradio.unicron.iot.bootstrap.channel.SpringUtil;
import org.konggradio.unicron.iot.constant.RedisConstant;
import org.konggradio.unicron.iot.ip.IpUtils;
import org.konggradio.unicron.iot.properties.InitBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * netty 服务启动类
 *
 * @author lianyadong
 * @create 2023-11-18 14:03
 **/
@Setter
@Getter
@lombok.extern.slf4j.Slf4j
@Component
public class NettyBootstrapServer extends AbstractBootstrapServer {

	private final int MAX_CONNECTION_LOAD = 50000;


	ServerBootstrap bootstrap = null;// 启动辅助类
	private InitBean serverBean;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	private NioEventLoopGroup bossGroup;
	private NioEventLoopGroup workGroup;

	public InitBean getServerBean() {
		return serverBean;
	}

	public void setServerBean(InitBean serverBean) {
		this.serverBean = serverBean;
	}

	/**
	 * 服务开启
	 */
	public void start() {
		//jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable
		initEventPool();
		bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_REUSEADDR, serverBean.isReuseaddr())
			.option(ChannelOption.SO_BACKLOG, serverBean.getBacklog()).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).option(ChannelOption.SO_RCVBUF, serverBean.getRevbuf())
			.childHandler(new ChannelInitializer<SocketChannel>() {

				protected void initChannel(SocketChannel ch) throws Exception {
					initHandler(ch.pipeline(), serverBean);
				}
			}).childOption(ChannelOption.TCP_NODELAY, serverBean.isTcpNodelay()).childOption(ChannelOption.SO_KEEPALIVE, serverBean.isKeepalive())
			.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		bootstrap.bind(serverBean.getPort()).addListener((ChannelFutureListener) channelFuture -> {
			if (channelFuture.isSuccess()) {
				log.info("channelFuture is Success:{}", IpUtils.getHost());
				//服务器启动了
				log.info("服务端启动成功【isAliveLogicServer:" + serverBean.isAliveLogicServer() + "," + IpUtils.getHost() + ":" + serverBean.getPort() + "】");

			} else {
				log.error("服务端启动失败", channelFuture.cause());
				log.info("服务端启动失败【" + IpUtils.getHost() + ":" + serverBean.getPort() + "】");

			}
		});
	}

	private void offlineThisNodeHistoryConnection(InitBean serverBean) {
		if (serverBean.isAliveLogicServer()) {

		} else {
			log.info("++++++stringRedisTemplate null++++++++");
		}
	}

	private void setMQConsumerListenCate(InitBean serverBean) {
		if (serverBean.isAliveLogicServer()) {

			if (stringRedisTemplate == null) {
				StringRedisTemplate stringRedisTemplateTemp = SpringUtil.getBean(StringRedisTemplate.class);
				stringRedisTemplate = stringRedisTemplateTemp;
			}
		}

	}

	private void updateOfflineRedis() {
		String offlineKey = RedisConstant.UNICRON_GLOBAL_OFFLINE_LIST;
		HashOperations<String, String, String> vo = stringRedisTemplate.opsForHash();
		Map<String, String> offlineMap = vo.entries(offlineKey);
		for (String key : offlineMap.keySet()) {
			String data = offlineMap.get(key);
			if (StringUtils.isBlank(data)) {
				continue;
			}
		}
	}

	/**
	 * 初始化EnentPool 参数
	 */
	private void initEventPool() {
		bootstrap = new ServerBootstrap();
		bossGroup = new NioEventLoopGroup(serverBean.getBossThread(), new ThreadFactory() {

			private AtomicInteger index = new AtomicInteger(0);

			public Thread newThread(Runnable r) {
				return new Thread(r, "BOSS_" + index.incrementAndGet());
			}
		});
		workGroup = new NioEventLoopGroup(serverBean.getWorkThread(), new ThreadFactory() {

			private AtomicInteger index = new AtomicInteger(0);

			public Thread newThread(Runnable r) {
				return new Thread(r, "WORK_" + index.incrementAndGet());
			}
		});

	}

	/**
	 * 关闭资源
	 */
	public void shutdown() {
		if (workGroup != null && bossGroup != null) {
			try {
				bossGroup.shutdownGracefully().sync();// 优雅关闭
				workGroup.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				log.info("服务端关闭资源失败【" + IpUtils.getHost() + ":" + serverBean.getPort() + "】");
			}
		}
	}

}
