/*
 * ContentTypeJudge.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.logipa

import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils

/**
 * Content-typeヘッダの値を判断します。
 * @version 1.0.00 2019/06/22
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ContentTypeJudge {
	/** すべてのコンテキストに共通する、ファイルの拡張子（小文字）と Content-typeヘッダの値とのマップ */
	static Map typeMap = [:]
	/** Content-typeヘッダのデフォルト値 */
	static String defaultType = 'application/octet-stream'
	
	/**
	 * ファイルに対応する Content-typeヘッダ値を返します。<br/>
	 * 以下の順番でContent-typeを判定します。</p>
	 * <ol>
	 * <li>メンバ変数 typeMapから参照します。</li>
	 * <li>ファイル名から{@link URLConnection#guessContentTypeFromName(String)}で推測します。</li>
	 * <li>上記のいずれも値を確定できない場合、メンバ変数 defaultTypeを返します。</li>
	 * </ul>
	 * @param file リクエストに対応するファイル
	 * @return Content-typeヘッダ値
	 */
	static String judge(File file){
		String contentType = typeMap[FilenameUtils.getExtension(file.name).toLowerCase()]
			?: URLConnection.guessContentTypeFromName(file.name)
			?: defaultType
		LOG.debug('contentType={} filename={}', contentType, file.name)
		return contentType
	}
}
