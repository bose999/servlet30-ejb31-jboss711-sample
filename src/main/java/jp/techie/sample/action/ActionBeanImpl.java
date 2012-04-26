/*
 * Copyright 2012 bose999.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.techie.sample.action;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.servlet.AsyncContext;

import jp.techie.sample.exception.SampleException;
import jp.techie.sample.service.ServiceBeanImpl;
import jp.techie.sample.util.LogUtil;

/**
 * BMTでトランザクションを実装したサンプル<br />
 * EJB3.1はインターフェイスも要らない
 * 
 * @author bose999
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ActionBeanImpl {

    /**
     * ログユーティリティ
     */
    public static LogUtil logUtil = new LogUtil(ActionBeanImpl.class);

    /**
    * サービスクラス<br />
    * EJBコンテナによりDIされる
    */
    @EJB
    private ServiceBeanImpl serviceBeanImpl;

    /**
     * サーブレットから非同期で呼び出されて<br />
     * 非同期サービスクラスを実行してハンドリングする
     * 
     * @param asyncContext
     */
    @Asynchronous
    public void execute(AsyncContext asyncContext) {
        long startTime = 0;
        if (logUtil.isInfoEnabled()) {
            // EJB処理開始ログ
            startTime = System.currentTimeMillis();
            logUtil.info("Start Asynchronous Method");
        }

        String dispatchUrl = "/";
        Future<String> serviceRetrun = null;
        try {
            // 非同期実行
            serviceRetrun = serviceBeanImpl.execute();
            // 10秒でタイムアウトを設定（あくまでサンプル）
            dispatchUrl = serviceRetrun.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            dispatchUrl = dispatchForException(asyncContext, e);
        } catch (ExecutionException e) {
            dispatchUrl = dispatchForException(asyncContext, e);
        } catch (TimeoutException e) {
            // タイムアウトなので処理をキャンセル
            // タイミングによってスレッドを止められないケースがあるが一応コール
            serviceRetrun.cancel(true);
            dispatchUrl = dispatchForException(asyncContext, e);
        } catch (SampleException e) {
            dispatchUrl = dispatchForException(asyncContext, e);
        }

        // 処理結果で遷移
        asyncContext.dispatch(dispatchUrl);

        if (logUtil.isInfoEnabled()) {
            // EJB処理終了ログ
            long doTime = System.currentTimeMillis() - startTime;
            logUtil.info("End Asynchronous Method:" + doTime + "ms.");
        }
    }

    /**
     * Exception時のエラーページへのdispatch
     * 
     * @param asyncContext AsyncContext
     * @param e Exception
     */
    private String dispatchForException(AsyncContext asyncContext, Exception e) {
        logUtil.fatal("execute:Exception Transaction RollBack", e);
        return "/WEB-INF/jsp/error.jsp";
    }
}
