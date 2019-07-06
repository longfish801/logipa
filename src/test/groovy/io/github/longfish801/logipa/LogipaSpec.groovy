/*
 * LogipaSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.logipa

import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * Logipaのテスト。
 * @version 1.0.00 2019/06/10
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class LogipaSpec extends Specification {
	def '変数Logipaを設定していること'(){
		given:
		Logipa logipa = new Logipa()
		
		expect:
		logipa.shell.getVariable('Logipa') == logipa
	}
	
	def 'DSLを実行すること'(){
		when:
		URI uri = ClassLoader.systemClassLoader?.getResource('logipa.groovy')?.toURI()
		
		then:
		new Logipa().run('logipa.groovy', uri, ['World'] as String[]) == 'Hello, World'
	}
	
	def 'URIが nullだった場合、DSLは実行せずに nullを返すこと'(){
		expect:
		new Logipa().run('logipa.groovy', null, ['World'] as String[]) == null
	}
	
	def 'InetSocketAddressに対応するServerAgentを返すこと'(){
		given:
		Logipa logipa = new Logipa()
		InetSocketAddress address8080 = new InetSocketAddress(8080)
		
		when:
		logipa.server(80)
		
		then:
		logipa.servers.keySet().find { it.port == 80 } != null
		
		when:
		logipa.server(address8080, 0)
		
		then:
		logipa.servers.keySet().find { it.port == 8080 } != null
		
		when:
		logipa.server(address8080, 0)
		
		then:
		logipa.servers.keySet().find { it.port == 8080 } != null
	}
	
	def '各HttpServer起動後に実行するクロージャを設定すること'(){
		given:
		Closure doLast = { return 'OK' }
		Logipa logipa
		
		when:
		logipa = new Logipa().doLast(doLast)
		
		then:
		logipa.doLastClosure.call() == 'OK'
	}
	
	def 'HttpServerを一括起動すること'(){
		given:
		Logipa logipa = new Logipa()
		logipa.server(8008).with { context('/') }
		Object result
		
		when:
		result = logipa.start()
		logipa.servers.each { it.value.server.stop(1) }
		
		then:
		result == null
	}
	
	def 'メンバ変数 doLastClosureが nullでなければ実行すること'(){
		given:
		Closure doLast = { return 'OK' }
		Logipa logipa = new Logipa()
		logipa.doLast(doLast)
		Object result
		
		when:
		result = logipa.start()
		
		then:
		result == 'OK'
	}
}
