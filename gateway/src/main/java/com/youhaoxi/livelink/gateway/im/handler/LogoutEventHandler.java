package com.youhaoxi.livelink.gateway.im.handler;

import com.youhaoxi.livelink.gateway.cache.UserRelationHashCache;
import com.youhaoxi.livelink.gateway.dispatch.Worker;
import com.youhaoxi.livelink.gateway.im.event.IMsgEvent;
import com.youhaoxi.livelink.gateway.im.event.LogoutEvent;
import com.youhaoxi.livelink.gateway.cache.ChatRoomRedisManager;
import com.youhaoxi.livelink.gateway.util.ConnectionManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutEventHandler extends IMEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(LogoutEventHandler.class);

    public LogoutEventHandler(ChannelHandlerContext ctx, IMsgEvent msg) {
        super(ctx, msg);
    }

    @Override
    public void execute(Worker woker) {
        logger.debug(">>>用户登出事件:"+msg.toString());
        LogoutEvent logoutEvent =  (LogoutEvent)msg;
        ConnectionManager.closeConnection(ctx);
        UserRelationHashCache.removeUserIdHostRelation(logoutEvent.from.getUserId());
    }
}
