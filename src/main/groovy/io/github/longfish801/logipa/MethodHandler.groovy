/*
 * MethodHandler.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.logipa

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils

/**
 * メソッド別にHTTPリクエストとレスポンスを処理するための特性です。
 * @version 1.0.00 2019/06/22
 * @author io.github.longfish801
 */
@Slf4j('LOG')
trait MethodHandler {
	/** コンテキストパスに対応するフォルダ */
	File dir = new File('.')
	/** URLにファイル名が省略されたときのデフォルト値 */
	String defaultFilename = 'index.html'
	/** Not Found時に表示するファイルの名前(404.html) */
	String notFouldFilename = '404.html'
	
	/**
	 * 特定メソッドについてリクエストに対しレスポンスを返します。<br/>
	 * リクエストに対応するファイルとContent-typeを取得し、
	 * {@link #response(HttpExchange,File,String)}メソッドを呼びます。<br/>
	 * パスに対応するファイルが存在しなければ 404 Not Foundを返します。
	 * @param exchange HttpExchange
	 * @return 自インスタンス
	 */
	MethodHandler method(HttpExchange exchange){
		File file = getFile(exchange)
		LOG.debug('method file={} requestURI={}', file, exchange.requestURI)
		if (!file.isFile()) return handleNotFound(exchange, file)
		response(exchange, file, ContentTypeJudge.judge(file))
		return this
	}

	/**
	 * リクエストに対応するファイルがないときの処理をします。<br/>
	 * Not Found時に表示するファイルがなければ 404 Not Foundを返します。<br/>
	 * 存在するときはそのファイルの内容をレスポンスとします。
	 * @param exchange HttpExchange
	 * @param file リクエストに対応するファイル
	 * @return 自インスタンス
	 */
	private MethodHandler handleNotFound(HttpExchange exchange, File file){
		File notFoundFile = new File(dir, notFouldFilename)
		if (!notFoundFile.isFile()){
			//  Not Found時に表示するファイルがなければ 404 Not Foundを返します
			LOG.warn('404 Not Found, {} {}, filepath={}', exchange.requestMethod, exchange.requestURI, file.path)
			exchange.sendResponseHeaders(404, 0)
		} else {
			//  存在するときはそのファイルの内容をレスポンスとします
			response(exchange, notFoundFile, ContentTypeJudge.judge(notFoundFile))
		}
		return this
	}
	
	/**
	 * リクエストに対応するファイルとContent-typeに基づきレスポンスを返します。<br/>
	 * 200 OK応答としてファイル内容を返します。<br/>
	 * Content-typeヘッダとして Content-typeの値を返します。
	 * @param exchange HttpExchange
	 * @param file HttpExchange
	 * @param contentType Content-typeヘッダ値
	 * @return 自インスタンス
	 */
	MethodHandler response(HttpExchange exchange, File file, String contentType){
		LOG.info('200 OK, {} {}', exchange.requestMethod, exchange.requestURI.path)
		exchange.responseHeaders.set('Content-Type', contentType)
		exchange.sendResponseHeaders(200, 0)
		file.withInputStream { exchange.responseBody << it }
		return this
	}
	
	/**
	 * リクエストに対応するファイルを返します。<br/>
	 * リクエストURLのパスを相対パスとみなし、コンテキストパスに対応する
	 * フォルダから相対する位置にあるファイルを返します。<br/>
	 * 相対する位置にあるのがフォルダの場合、そのフォルダを親フォルダとする
	 * デフォルトのファイル名に対応するファイルを返します。<br/>
	 * ファイルの存在チェックはせず、必ずファイルインスタンスを返します。
	 * @param exchange HttpExchange
	 * @return リクエストに対応するファイル
	 */
	File getFile(HttpExchange exchange){
		File file = new File(dir, exchange.requestURI.path.replaceFirst("${exchange.httpContext.path}/", ''))
		if (file.isDirectory()) file = new File(file, defaultFilename)
		return file
	}
}
