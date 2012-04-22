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

import javax.ejb.Singleton;
import javax.ejb.Stateless;

import jp.techie.sample.util.LogUtil;

/**
 * 非同期呼び出し後ビジネスロジックを実行するサービスEJB実装サンプル
 * 
 * @author bose999
 *
 */
@Singleton
public class ServiceBeanImpl {
    
    /**
     * ログユーティリティ
     */
    public static LogUtil logUtil = new LogUtil(ServiceBeanImpl.class);

    public String execute() {
//        // TransactionTimeoutを試す時はコメントを外す        
//        int i = 1;
//        while(i<100000){
//            logUtil.info("looping:" + i);
//            i++;
//        }
        return "/WEB-INF/jsp/result.jsp";
    }
}
