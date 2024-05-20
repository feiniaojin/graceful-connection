package com.feiniaojin.graceful.connection.server.lifecycle;

import com.feiniaojin.graceful.connection.server.ServerSystemContext;
import com.feiniaojin.lifecycle.SystemCallbackRegister;
import com.feiniaojin.lifecycle.SystemLifecycleCallback;

/**
 * 服务端回调注册容器
 */
public class ServerCallbackRegister extends SystemCallbackRegister<ServerSystemContext,
        SystemLifecycleCallback<ServerSystemContext>> {
}
