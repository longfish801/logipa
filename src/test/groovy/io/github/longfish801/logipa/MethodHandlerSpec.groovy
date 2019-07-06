/*
 * MethodHandlerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.logipa

import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpContext
import com.sun.net.httpserver.HttpExchange
import groovy.util.logging.Slf4j
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * MethodHandlerのテスト。
 * @version 1.0.00 2019/06/10
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class MethodHandlerSpec extends Specification {
	/** 資材格納フォルダ */
	File testDir = new File('src/test/resources/' + this.class.canonicalName.replaceAll(/\./, '/'))
	/** テスト対象のTestHandler */
	@Shared TestHandler handler
	/** Headers */
	@Shared Headers headers
	/** HttpExchangeを取得するクロージャ */
	@Shared Closure getExchange
	
	class TestHandler implements MethodHandler {}
	
	def setup(){
		handler = new TestHandler()
		handler.dir = testDir
		getExchange = { String address ->
			HttpExchange exchange = Mock()
			exchange.requestMethod >> 'get'
			exchange.requestURI >> new URI(address)
			exchange.responseBody >> new ByteArrayOutputStream()
			headers = Mock()
			exchange.responseHeaders >> headers
			HttpContext context = Mock()
			context.path >> '/'
			exchange.httpContext >> context
			return exchange
		}
	}
	
	def '特定メソッドについてリクエストに対しレスポンスを返すこと'(){
		given:
		HttpExchange exchange = getExchange('http://dummy.url/dir/')
		
		when:
		handler.method(exchange)
		
		then:
		1 * exchange.sendResponseHeaders(200, 0)
	}
	
	def 'パスに対応するファイルが存在しなければ 404 Not Foundを返すこと'(){
		given:
		HttpExchange exchange = getExchange('http://dummy.url/nosuch/file')
		
		when:
		handler.method(exchange)
		
		then:
		1 * exchange.sendResponseHeaders(404, 0)
	}
	
	def 'リクエストに対応するファイルとContent-typeに基づきレスポンスを返すこと'(){
		given:
		HttpExchange exchange = getExchange('http://dummy.url/dir/')
		File file = handler.getFile(exchange)
		
		when:
		handler.response(exchange, file, 'text/html')
		
		then:
		1 * headers.set('Content-Type', 'text/html')
		1 * exchange.sendResponseHeaders(200, 0)
	}
	
	@Unroll
	def 'リクエストに対応するファイルを返すこと'(){
		expect:
		handler.getFile(getExchange(url)).path == path
		
		where:
		url									| path
		'http://dummy.url/dir/index.html'	| /src\test\resources\io\github\longfish801\logipa\MethodHandlerSpec\dir\index.html/
		'http://dummy.url/dir/'				| /src\test\resources\io\github\longfish801\logipa\MethodHandlerSpec\dir\index.html/
		'http://dummy.url/dir'				| /src\test\resources\io\github\longfish801\logipa\MethodHandlerSpec\dir\index.html/
	}
}
