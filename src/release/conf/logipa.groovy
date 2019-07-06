/*
 * logipa.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

import java.awt.Desktop

Logipa.with {
	// ファイルの拡張子と Content-typeとの対応を設定します
	contentTypes.typeMap = [
		'css': 'text/css',
		'js': 'application/javascript',
	]
	
	// ポート番号80の HTTPサーバを設定します
	server(80).with {
		// コンテキストパスを設定します
		context('/').with { methods.GET.dir = new File('sample') }
	}
	
	// HTTPサーバ起動後に実行する処理を設定します
	doLast {
		Desktop.desktop.browse(new URI('http://localhost/'))
	}
}
