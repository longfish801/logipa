/*
 * package-info.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

/**
 * DSLによる設定および拡張可能なHTTPサーバです。<br/>
 * Logipaクラスにメインメソッドがあります。DSLを実行し、
 * HTTPサーバを起動します。<br/>
 * DSLの実行により com.sun.net.httpserverパッケージの
 * クラスを含む各インスタンスを生成し、HTTPサーバ起動に
 * 必要な設定値などを準備します。<br/>
 * その上で各HTTPサーバを起動します。<br/>
 * com.sun.net.httpserverパッケージのクラスと
 * 本パッケージのクラストの照応は以下のとおりです。</p>
 * <dl>
 * <dt>{@link com.sun.net.httpserver.HttpServer}</dt>
 * <dd>{@link ServerAgent}のメソッド変数serverとして保持。</dd>
 * <dd>{@link Logipa}はメソッド変数として
 * {@link ServerAgent}インスタンスのリストは保持。</dd>
 * <dt>{@link com.sun.net.httpserver.HttpHandler}</dt>
 * <dd>{@link ContextHandler}がimplementsしている。</dd>
 * </dl>
 * @version 1.0.00 2019/07/06
 * @author io.github.longfish801
 */
package io.github.longfish801.logipa
