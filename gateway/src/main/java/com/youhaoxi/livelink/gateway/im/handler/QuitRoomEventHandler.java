package com.youhaoxi.livelink.gateway.im.handler;

import com.youhaoxi.livelink.gateway.cache.RoomUserRelationSetCache;
import com.youhaoxi.livelink.gateway.dispatch.Worker;
import com.youhaoxi.livelink.gateway.im.event.IMsgEvent;
import com.youhaoxi.livelink.gateway.im.event.QuitRoomEvent;
import com.youhaoxi.livelink.gateway.cache.ChatRoomRedisManager;
import com.youhaoxi.livelink.gateway.im.msg.ResultMsg;
import com.youhaoxi.livelink.gateway.im.msg.User;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuitRoomEventHandler extends IMEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(QuitRoomEventHandler.class);

    public QuitRoomEventHandler(ChannelHandlerContext ctx, IMsgEvent msg) {
        super(ctx, msg);
    }

    @Override
    public void execute(Worker woker) {
        logger.debug(">>>用户退出聊天室消息事件处理:"+msg.toString());
        QuitRoomEvent event = (QuitRoomEvent)msg;
        ChatRoomRedisManager.removeUserFromRoom(event.getUserId(),event.getRoomId());
        //判断群是否还有成员,如果已经没有成员则删除
        long count =  RoomUserRelationSetCache.getRoomMembersCount(event.getRoomId());
        if(count==0){
            RoomUserRelationSetCache.delRoom(event.getRoomId());
        }else{
            //给群发送群通知
            String name = event.from.name;
            ResultMsg result = new ResultMsg(40,name+"退出了聊天室");
            result.setRoomId(event.getRoomId());
            woker.dispatcher.groupDispatch(result,event.getRoomId());
        }


    }
}
