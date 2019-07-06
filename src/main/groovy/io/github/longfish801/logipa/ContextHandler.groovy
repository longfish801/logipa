/*
 * ContextHandler.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.logipa

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils

/**
 * HTTPリクエストに対しレスポンスを返します。
 * @version 1.0.00 2019/05/29
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ContextHandler implements HttpHandler {
	/** メソッド（大文字）と対応ハンドラーとのマップ */
	Map methods = ['GET': new GetHandler()]
	
	/**
	 * リクエストメソッド別のハンドラにHttpExchangeを渡します。<br/>
	 * リクエストメソッド（大文字に変換）に対応するハンドラを
	 * クラス変数methodsから参照し、その methodメソッドを実行します。<br/>
	 * メソッドに対応するハンドラがない要求は 405 Method Not Allowdを返します。<br/>
	 * 例外発生時には 500 Internal Server Errorを返します。
	 * @param exchange HttpExchange
	 */
	@Override
	public void handle(HttpExchange exchange){
		try {
			String method = exchange.requestMethod.toUpperCase()
			LOG.debug('handle method={} requestURI={}', method, exchange.requestURI)
			if (!methods.containsKey(method)){
				// メソッドに対応するハンドラがない要求は 405 Method Not Allowdを返します
				LOG.warn('405 Method Not Allowd, {} {}', exchange.requestMethod, exchange.requestURI)
				exchange.sendResponseHeaders(405, 0)
			} else {
				methods[method].method(exchange)
			}
		} catch (exc) {
			LOG.error('500 Internal Server Error, {} {}', exchange.requestMethod, exchange.requestURI, exc)
			exchange.sendResponseHeaders(500, 0)
		} finally {
			exchange.responseBody.close()
		}
	}
}
