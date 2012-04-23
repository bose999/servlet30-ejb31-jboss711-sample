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

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.servlet.AsyncContext;
import javax.transaction.UserTransaction;

import jp.techie.sample.service.ServiceBeanImpl;
import jp.techie.sample.util.LogUtil;

/**
 * BMTでトランザクションを実装したサンプル<br />
 * EJB3.1はインターフェイスも要らない<br />
 * シングルトンとしてインスタンス化
 * 
 * @author bose999
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ActionBeanImpl {

    /**
     * ログユーティリティ
     */
    public static LogUtil logUtil = new LogUtil(ActionBeanImpl.class);
    
    /**
     * UserTransaction
     */
    @Resource
    private UserTransaction usrTransaction;

    /**
    * 非同期処理をするEJB<br />
    * EJBコンテナによりDIされる
    */
    @EJB
    private ServiceBeanImpl serviceBeanImpl;

    /**
     * 非同期サーブレットから呼び出す非同期メソッド
     * 
     * @param asyncContext
     */
    @Asynchronous
    public void execute(AsyncContext asyncContext) {
        // サーブレットから呼び出され処理を非同期で開始
        // このメッソドを先頭にしてトランザクションを開始する
        try {
            long startTime = 0;
            if (logUtil.isInfoEnabled()) {
                // EJB処理開始ログ
                startTime = System.currentTimeMillis();
                logUtil.info("Start Asynchronous EJB Method");
            }
            
            // トランザクションタイムアウトを設定する
            // ブラウザのタイムアウトよりも短い280sでとりあえず設定
            usrTransaction.setTransactionTimeout(280);
            
            // トランザクション開始
            usrTransaction.begin();

            // サービスクラス実行
            String dispatchUrl = serviceBeanImpl.execute();
            
            // トランザクションコミット
            usrTransaction.commit();
            
            // ビジネスロジックをの返り値でdispatch
            // コミット前にコールしてしまうとcommit時の例外が拾えないでコミット後
            asyncContext.dispatch(dispatchUrl);

            if (logUtil.isInfoEnabled()) {
                // EJB処理終了ログ
                long doTime = System.currentTimeMillis() - startTime;
                logUtil.info("End Asynchronous EJB Method:" + doTime + "ms.");
            }
        } catch (Exception e) {
          //例外が上がってきたのでトランザクションをロールバック
            logUtil.fatal("execute:Exception Transaction RollBack",e);
            try {
                usrTransaction.rollback();
            } catch (Exception e2) {
                logUtil.fatal("execute:Transaction RollBack Exception",e);
            }
            logUtil.fatal("execute:move errorPage");
            // サービスクラスでの処理においてExceptionが発生した為 エラーページへ遷移させる
            // サンプルとして大雑把な記述。。。
            asyncContext.dispatch("/WEB-INF/jsp/error.jsp");
        }
    }
}