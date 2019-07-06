/*
 * ServerAgentSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.logipa

import groovy.util.logging.Slf4j
import spock.lang.Specification
import spock.lang.Unroll

/**
 * ServerAgentのテスト。
 * @version 1.0.00 2019/06/27
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ServerAgentSpec extends Specification {
	@Unroll
	def 'ContextHandlerインスタンスを新規に作成すること'(){
		given:
		ServerAgent agent = new ServerAgent()
		ContextHandler handler
		Closure getHandler = { String path ->
			handler = agent.context(path)
			return handler
		}
		
		expect:
		getHandler(path1) == agent.contexts[path2]
		
		where:
		path1	| path2
		null	| '/'
		'/abc'	| '/abc'
		'/'		| '/'
		''		| '/'
		'abc'	| '/abc'
		'/'		| '/'
	}
}
