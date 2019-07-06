/*
 * Logipa.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.logipa

import groovy.util.logging.Slf4j

/**
 * 複数のHttpServerを管理します。
 * @version 1.0.00 2019/05/29
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class Logipa {
	/** GroovyShell */
	GroovyShell shell = new GroovyShell(Logipa.class.classLoader)
	/** InetSocketAddressとServerAgentとのマップ */
	Map servers = [:]
	/** 各HttpServer起動後に実行するクロージャ */
	Closure doLastClosure
	/** ContentTypeJudgeのClass */
	Class contentTypes = ContentTypeJudge
	
	/**
	 * コンストラクタ。<br/>
	 * DSL内で変数Logipa（値はLogipaインスタンス）を使用できるよう設定します。
	 */
	Logipa(){
		shell.setVariable('Logipa', this)
	}
	
	/**
	 * メインメソッド。<br>
	 * 以下を実行します。</p>
	 * <ul>
	 * <li>リソース「logipa.groovy」を参照し、DSLとして実行します。
	 * このときコマンドライン引数を DSLの引数として与えます。</li>
	 * <li>各HttpServerを起動します。</li>
	 * </ul>
	 * @param args コマンドライン引数
	 */
	static void main(args){
		LOG.info('HttpServerを起動します。')
		try {
			Logipa logipa = new Logipa()
			logipa.run('logipa.groovy', ClassLoader.systemClassLoader?.getResource('logipa.groovy')?.toURI(), args)
			logipa.start()
			LOG.info('HttpServerを起動しました。')
		} catch (Throwable exc){
			LOG.error('HttpServerの起動に失敗しました。', exc)
		}
	}
	
	/**
	 * DSLを実行します。<br/>
	 * URIが nullだった場合、DSLは実行せずに nullを返します。
	 * @param name DSLの識別名（ログに出力するのみ）
	 * @param uri DSLのURI
	 * @param args DSL実行に与える引数
	 * @return DSLの実行結果（URIが nullのときは nullを返す）
	 */
	Object run(String name, URI uri, String[] args){
		if (uri == null){
			LOG.warn('DSLの URIが nullです。name={}', name)
			return null
		}
		LOG.debug('DSLを実行します。name={}, uri={}', name, uri)
		return shell.run(uri, args)
	}
	
	/**
	 * ポート番号に対応するServerAgentを返します。
	 * @param port ポート番号
	 * @return ServerAgent
	 * @see #server(InetSocketAddress)
	 */
	ServerAgent server(int port){
		return server(new InetSocketAddress(port), 0)
	}
	
	/**
	 * InetSocketAddressに対応するServerAgentを返します。<br/>
	 * ServerAgentを未作成であれば新規作成し、メンバ変数serversに格納します。
	 * @param address InetSocketAddress
	 * @param backlog ソケットのバックログ
	 * @return ServerAgent
	 */
	ServerAgent server(InetSocketAddress address, int backlog){
		if (!servers.containsKey(address)){
			ServerAgent serverAgent = new ServerAgent()
			serverAgent.server.bind(address, backlog)
			servers[address] = serverAgent
		}
		return servers[address]
	}
	
	/**
	 * 各HttpServer起動後に実行するクロージャを設定します。<br/>
	 * 委譲先として Logipaインスタンスを設定します。
	 * @param closure 各HttpServer起動後に実行するクロージャ
	 * @return 自インスタンス
	 */
	Logipa doLast(Closure closure){
		closure.delegate = this
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		doLastClosure = closure
		return this
	}
	
	/**
	 * HttpServerを一括起動します。<br/>
	 * メンバ変数serversに格納したServerAgentインスタンスすべてに対し、
	 * HttpServerの起動を実行します。<br/>
	 * 各HttpServerを起動後、メンバ変数 doLastClosureが nullでなければ実行します。
	 * @return メンバ変数doLastClosureの実行結果（doLastClosureが nullならば nullを返します）
	 */
	Object start(){
		if (servers.empty) LOG.warn('起動対象となるHttpServerがありません。')
		servers.each {
			LOG.debug('HttpServerを起動します。address={}', it.key)
			it.value.server.start()
		}
		return doLastClosure?.call()
	}
}
