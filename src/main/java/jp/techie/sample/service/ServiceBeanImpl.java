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
package jp.techie.sample.service;

import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import jp.techie.sample.exception.SampleException;
import jp.techie.sample.util.LogUtil;

/**
 * 非同期呼び出し後ビジネスロジックを実行するサービスEJB実装サンプル
 * 
 * @author bose999
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ServiceBeanImpl {

    /**
     * ログユーティリティ
     */
    public static LogUtil logUtil = new LogUtil(ServiceBeanImpl.class);

    /**
     * SessionContext
     */
    @Resource
    private SessionContext sessionContext;

    /**
     * 非同期で実行するサービス<br />
     * ハンドリング出来る例外が起きたらSampleExceptionでラップしてthrow
     * 
     * @return Future<String> dispatchUrl
     * @throws SampleException
     */
    @Asynchronous
    public Future<String> execute() {
        // 非同期メソッドなのでここでトランザクションがREQUREIRES NEWされる
        // 実際に実装する時はここからトランザクションが始まると考えて実装する
        long startTime = 0;
        if (logUtil.isInfoEnabled()) {
            // EJB処理開始ログ
            startTime = System.currentTimeMillis();
            logUtil.info("Start Asynchronous Service Method");
        }
        String dispatch = "/";
        try {
            dispatch = makeDispatchUrl();
        } catch (Exception e) {
            // 例外なんて起きないけど例外のサンプル
            // 例外が起きたら処理をハンドリングしてスローする
            sessionContext.setRollbackOnly();
            logUtil.fatal("Service execute Exception:", e);
            throw new SampleException(e.getMessage());
        }
        if (logUtil.isInfoEnabled()) {
            // EJB処理終了ログ
            long doTime = System.currentTimeMillis() - startTime;
            logUtil.info("End Asynchronous Service Method:" + doTime + "ms.");
        }
        return new AsyncResult<String>(dispatch);
    }

    /**
     * dispatchUrl生成
     * 
     * @return String dispatchUrl
     */
    protected String makeDispatchUrl() {
        //      // タイムアウトを試す場合はコメントを外す
        //      int i = 1;
        //      logUtil.info("looping!");
        //      while(i<20000){
        //          //logUtil.info("looping:" + i);
        //          try {
        //              Thread.sleep(1);
        //          } catch (InterruptedException e) {
        //          }
        //          i++;
        //      }
        //      logUtil.info("looping end!");

        return "/WEB-INF/jsp/result.jsp";
    }
}
