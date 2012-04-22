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
package jp.techie.sample.servlet;

import javax.ejb.EJB;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.techie.sample.action.ActionBeanImpl;
import jp.techie.sample.util.LogUtil;

/**
 * Servlet3.0非同期サンプル Servlet with EJB3.1
 * 
 * @author bose999
 *
 */
//@WebServlet(name = "sample", urlPatterns = {"/sample"}, asyncSupported = true)
@SuppressWarnings("serial")
public class SampleServlet extends HttpServlet {

    /**
     * ログユーティリティ
     */
    public static LogUtil logUtil = new LogUtil(SampleServlet.class);

    /**
    * 非同期処理をするEJB
    * EJBコンテナによりDIされる
    */
    @EJB
    private ActionBeanImpl actionBeanImpl;

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        long startTime = 0;
        if (logUtil.isInfoEnabled()) {
            // Servlet処理開始ログ
            startTime = System.currentTimeMillis();
            logUtil.info("Start Servlet Method");
        }

        // 非同期処理準備
        AsyncContext asyncContext = request.startAsync();

        // EJBの非同期処理を実行
        // Servletコンテナだけの時とは違いListnerは利用しない
        actionBeanImpl.execute(asyncContext);

        if (logUtil.isInfoEnabled()) {
            // Servlet処理終了ログ
            long doTime = System.currentTimeMillis() - startTime;
            logUtil.info("End Servlet Method:" + doTime + "ms.");
        }
    }
}
