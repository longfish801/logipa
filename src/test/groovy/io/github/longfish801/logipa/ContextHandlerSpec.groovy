/*
 * ContextHandlerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.logipa

import com.sun.net.httpserver.HttpExchange
import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * ContextHandlerのテスト。
 * @version 1.0.00 2019/06/10
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ContextHandlerSpec extends Specification {
	def 'リクエストメソッド別のハンドラにHttpExchangeを渡すこと'(){
		given:
		ContextHandler handler = new ContextHandler()
		handler.methods['GET'] = new SuccessGetHandler()
		HttpExchange exchange = Mock()
		exchange.requestMethod >> 'get'
		exchange.requestURI >> new URI('http://dummy.url/')
		exchange.responseBody >> new ByteArrayOutputStream()
		
		when:
		handler.handle(exchange)
		
		then:
		1 * exchange.sendResponseHeaders(200, 0)
	}
	
	def '例外発生時には 500 Internal Server Errorを返すこと'(){
		given:
		ContextHandler handler = new ContextHandler()
		handler.methods['GET'] = new FailGetHandler()
		HttpExchange exchange = Mock()
		exchange.requestMethod >> 'get'
		exchange.requestURI >> new URI('http://dummy.url/')
		exchange.responseBody >> new ByteArrayOutputStream()
		
		when:
		handler.handle(exchange)
		
		then:
		1 * exchange.sendResponseHeaders(500, 0)
	}
	
	def 'メソッドに対応するハンドラがない要求は 405 Method Not Allowdを返すこと'(){
		given:
		ContextHandler handler = new ContextHandler()
		HttpExchange exchange = Mock()
		exchange.requestMethod >> 'POST'
		exchange.requestURI >> new URI('http://dummy.url/')
		exchange.responseBody >> new ByteArrayOutputStream()
		
		when:
		handler.handle(exchange)
		
		then:
		1 * exchange.sendResponseHeaders(405, 0)
	}
	
	class SuccessGetHandler implements MethodHandler {
		MethodHandler method(HttpExchange exchange){
			exchange.sendResponseHeaders(200, 0)
			return this
		}
	}
	
	class FailGetHandler implements MethodHandler {
		MethodHandler method(HttpExchange exchange){
			int num = 1 / 0
			return this
		}
	}
}
