package com.feiniaojin.graceful.connection.lifecycle;

import com.feiniaojin.graceful.connection.AbstractSystemContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 系统回调注册容器
 */
public class SystemCallbackRegister<CTX extends AbstractSystemContext, CALL extends SystemLifecycleCallback> {

    private List<CALL> lifecycleCallbackList = new CopyOnWriteArrayList<>();

    public void registerCallback(CALL callback) {
        lifecycleCallbackList.add(callback);
    }

    /**
     * 执行创建后的回调
     *
     * @param ctx
     */
    public void invokeBeforeCreateCallback(CTX ctx) {
        for (CALL call : lifecycleCallbackList) {
            call.beforeCreate(ctx);
        }
    }

    /**
     * 执行创建后的回调
     *
     * @param ctx
     */
    public void invokeAfterCreateCallback(CTX ctx) {
        for (CALL call : lifecycleCallbackList) {
            call.afterCreate(ctx);
        }
    }

    /**
     * 执行启动前回调
     *
     * @param ctx
     */
    public void invokeBeforeStartCallback(CTX ctx) {
        for (CALL call : lifecycleCallbackList) {
            call.beforeStart(ctx);
        }
    }

    /**
     * 执行启动后回调
     *
     * @param ctx
     */
    public void invokeAfterStartCallback(CTX ctx) {
        for (CALL call : lifecycleCallbackList) {
            call.afterStart(ctx);
        }
    }
}
