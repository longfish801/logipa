/*
 * ContentTypeJudgeSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.logipa

import groovy.util.logging.Slf4j
import spock.lang.Specification
import spock.lang.Unroll

/**
 * ContentTypeJudgeのテスト。
 * @version 1.0.00 2019/06/10
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ContentTypeJudgeSpec extends Specification {
	File testDir = new File('src/test/resources/' + this.class.canonicalName.replaceAll(/\./, '/'))
	
	@Unroll
	def 'ファイルに対応する Content-typeヘッダ値を返すこと'(){
		given:
		ContentTypeJudge.typeMap = [
			'css': 'text/css',
			'js': 'application/javascript',
		]
		
		expect:
		ContentTypeJudge.judge(new File(testDir, fname)) == expected
		
		where:
		fname			| expected
		'index.html'	| 'text/html'
		'style.css'		| 'text/css'
		'script.js'		| 'application/javascript'
		'text.txt'		| 'text/plain'
		'image.gif'		| 'image/gif'
		'image.jpg'		| 'image/jpeg'
		'image.png'		| 'image/png'
		'data.dat'		| 'application/octet-stream'
	}
}
