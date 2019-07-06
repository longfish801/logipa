/*
 * ServerAgent.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.logipa

import groovy.util.logging.Slf4j
import com.sun.net.httpserver.HttpServer

/**
 * HttpServerを設定ならびに起動します。
 * @version 1.0.00 2019/05/29
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ServerAgent {
	/** コンテキストパスと ContextHandlerとのマップ */
	Map contexts = [:]
	/** HttpServer */
	HttpServer server = HttpServer.create()
	
	/**
	 * ContextHandlerインスタンスを新規に作成します。<br/>
	 * コンテキストパスは nullならば '/'に、
	 * '/'始まりではない場合は '/'始まりに補正します。<br/>
	 * ContextHandlerインスタンスを生成し、メンバ変数contextsに保持します。
	 * @param path コンテキストパス（'/'で開始する文字列）
	 * @return 生成した ContextHandlerインスタンス
	 */
	ContextHandler context(String path){
		// コンテキストパスを補正します
		if (path == null) path = '/'
		if (path.indexOf('/') != 0) path = "/${path}"
		// コンテキストパスに対応するContextHandlerが未作成であれば作成します
		if (!contexts.containsKey(path)){
			LOG.debug('コンテキストを生成します。path={}', path)
			contexts[path] = new ContextHandler()
			server.createContext(path, contexts[path])
		}
		return contexts[path]
	}
}
