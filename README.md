# logipa
## 概要

　シンプルな HTTPサーバです。  
　静的な表示のみ対応しています。要求アドレスのパスに応じてファイルシステム上のファイル内容を返します。

　個人が自学自習を目的として開発したものであり、故障対応や問合せ回答といったサポートはいっさいしていません。  

## 特徴

* HTTPメソッドは GETのみ対応しています。
* 設定は Groovy DSLで記述します。
* HTTPサーバとしての処理には Javaの com.sun.net.httpserverパッケージを利用しています。

## クイックスタート

　使い方は以下のとおりです。

1. 設定ファイル conf/logipa.groovyを編集します。
2. 実行ファイル logipa.exeをダブルクリックし、コンソールを起動します。これにより HTTPサーバが起動します。
3. コンソールを閉じると、HTTPサーバは終了します。

　設定ファイルのサンプルを以下に示します。  
　ポート番号80で HTTPサーバを起動します。  
　ブラウザを起動して sample/index.htmlのファイル内容を表示します。

~~~
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
~~~

　詳細は[ドキュメント](https://longfish801.github.io/logipa/)を参照してください。

