package com.ddbb.im;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/socket/{fromUserId}")
@Component
@Slf4j
public class WebSocketServer {

    private static ConcurrentHashMap<String, WebSocketServer> websocketMap = new ConcurrentHashMap<>();

    private Session session; // 与某个客户端的连接会话，需要通过它来给客户端发送数据

    private String fromUserId = null;

    @OnOpen
    public void onOpen(Session session, @PathParam("fromUserId") String fromUserId) {
        this.session = session;
        this.fromUserId = fromUserId;

        if (websocketMap.containsKey(fromUserId)) {
            websocketMap.remove(fromUserId);
        }
        websocketMap.put(fromUserId, this);

    }

    @OnClose
    public void onClose() {
        if (websocketMap.containsKey(fromUserId)) {
            websocketMap.remove(fromUserId);
        }
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("用户消息:" + fromUserId + ",报文:" + message);

        if (StringUtils.isEmpty(message)) {
            return;
        }

        MessagePayload payload = JSON.parseObject(message, MessagePayload.class);
        if (null != payload.getToUserId() && websocketMap.containsKey(payload.getToUserId())) {
            try {
                websocketMap.get(payload.getToUserId()).sendMessage(payload);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        log.error("请求的userId:" + payload.getToUserId() + "不在该服务器上");
    }

    @OnError
    public void onError(Throwable error) {
        log.error("用户错误:" + this.fromUserId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    private void sendMessage(MessagePayload message) throws IOException {
        this.session.getAsyncRemote().sendText(JSON.toJSONString(message));
    }
}

