# 키워드

- ConstraintLayout
- EditText
- WebView

# 구현 목록

- 웹사이트 불러오기
- 앞으로 가기, 뒤로 가기, 홈 버튼
- 웹사이드 로딩 확인

# 개발 과정

## 1. 기본 UI 설정하기

`ConstraintLayout` 을 추가로 설정해서 상단바를 만들었다. 이후 좌측부터 홈, 주소창, 뒤로 가기, 앞으로 가기를 넣어줬다. 이번에 어플을 만들면서 `match_parent` 와 `0dp` 의 차이가 헷갈려서 다시 공부했는데 0dp는 `match_constraint` 와 같은 의미이다. 

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/toolBar"
        android:layout_width="0dp"
        android:layout_height="?attr/actionBarSize"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <ImageButton
            android:id="@+id/goHomeButton"
            android:layout_width="wrap_content"
            android:layout_height="0dp"
            android:src="@drawable/ic_home"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <EditText
            android:id="@+id/addressBar"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:inputType="textUri"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toRightOf="@id/goHomeButton"
            app:layout_constraintRight_toLeftOf="@+id/goBackButton"
            app:layout_constraintTop_toTopOf="parent" />

        <ImageButton
            android:id="@+id/goForwardButton"
            android:layout_width="wrap_content"
            android:layout_height="0dp"
            android:src="@drawable/ic_forward"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <ImageButton
            android:id="@+id/goBackButton"
            android:layout_width="wrap_content"
            android:layout_height="0dp"
            android:src="@drawable/ic_back"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintRight_toLeftOf="@+id/goForwardButton"
            app:layout_constraintTop_toTopOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>

    <WebView
        android:id="@+id/webView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toBottomOf="@id/toolBar"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        />

</androidx.constraintlayout.widget.ConstraintLayout>
```

각 버튼을 보면 `layout_height` 에 0dp가 걸려있는 것을 볼 수 있는데 match_constraint 속성에 의해서 높이가 구현이 된다.

## 2. URL 로딩 기능 구현하기

### initView

위의 사진을 보면 앱을 실행해도 아직 `webView Layout` 에는 아무것도  뜨지 않은 것을 볼 수 있다. 앱을 실행했을 때 default로 `[www.google.com](http://www.google.com)` 이 접속되도록 설정하였다.

```kotlin
	private fun initView() {
        webView.webViewClient = WebViewClient() // 외부 웹브라우져로 전환되는 것을 막음
        webView.settings.javaScriptEnabled = true // 자바스크립트 사용을 허용
        webView.loadUrl("https://www.google.com")
    }

// webView 객체가 중첩되므로 apply를 사용하면 코드가 좀 더 이뻐진다.
	private fun initView() {
        webView.apply {
            webViewClient = WebViewClient() // 외부 웹브라우져로 전환되는 것을 막음
            settings.javaScriptEnabled = true // 자바스크립트 사용을 허용
            loadUrl("https://www.google.com")
        }
    }
```

앱을 실행해서 구글 사이트에 접속하면 외부 브라우져로 전환되는 버그?가 발생한다. 이를 해결하기 위해 웹브라우져가 이 앱을 의미하도록 webViewClient를 재설정하는 과정을 거쳤다. 또한 이렇게 로드된 구글 사이트에서 자바스크립트 사용이 막혀져있는 것을 볼 수 있다. 좌측 상단의 햄버거 버튼이나 각종 버튼이 실행이 안되는 이유가 바로 여기있다. 이를 해결하기 위해 `javaScriptEnabled` 속성을 통해 자바스크립트 사용을 허가해줬다.

![bandicam 2021-09-22 09-25-09-304.jpg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/331465ee-5142-4db7-ba35-79d25c1fc682/bandicam_2021-09-22_09-25-09-304.jpg)

### bindView

`default` 로 구글 사이트가 접속되는 것을 구현했다. 다음은 주소창에 url을 입력했을 때 해당 사이트로 이동되는 것을 구현하려고 한다. 이 때 `setOnEditorActionListener` 를 사용한다. `setOnClickListener` 와 동일하다. 

```kotlin
	<EditText
            android:id="@+id/addressBar"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:imeOptions="actionDone"
            android:inputType="textUri"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toRightOf="@id/goHomeButton"
            app:layout_constraintRight_toLeftOf="@+id/goBackButton"
            app:layout_constraintTop_toTopOf="parent" />
```

`EditText` 옵션에 넣어준 `imeOptions` 속성을 이용한다. 이는 EditText로 띄어진 키보드에 속성을 준다.

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/f2cc3091-182b-44e7-b000-f47c0e5940bc/Untitled.png)

우측 하단의 완료 버튼이 `action_done` 속성이다. 완료버튼이 눌리면 `Listener` 가 호출되고 action_id를 비교한다. id가 action_done인 경우에 주소창에 넣어준 text를 load하게 된다. 그렇지 않은 경우 false를 리턴한다. 하지만 이렇게 구현해도 `[naver.com](http://naver.com)` 와 같이 불완전한 상태의 url인 경우에 접속이 되지 않는다.

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/8769b849-9c24-4243-9a1a-3e702cf61c0c/Untitled.png)

이를 해결하기 위해 `Manifest` 에 `usesCleartextTraffic` 이라는속성을 줘서 자체적으로 `[https://www](https://www)` 를 붙여주도록 구현했다.

## 3. 네비게이션 기능 구현하기

### bindViews

home, back, forward 버튼에 동작을 추가한다. 매우 간단하다. webView 클래스에서 메소드가 다 구현이 되어 있어서 이를 사용하면 된다.

```kotlin
	private fun bindViews() {
        addressBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                webView.loadUrl(v.text.toString())
            }
            return@setOnEditorActionListener false
        }

        goBackButton.setOnClickListener {
            webView.goBack()
        }

        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }
    }
```

### onBackPressed

하지만 추가해줄 것이 있는데 우리가 웹 브라우져 어플을 사용하다보면 안드로이드 자체 UI의 뒤로 가기 버튼을 누르더라도 이전 페이지로 이동한다는 것을 알 것이다. 하지만 지금 우리가 구현한 어플은 뒤로 가기 버튼을 누르면 바로 앱이 종료하는데 이를 해결해야 한다. 이를 위해 `onBackPressed` 메소드를 오버라이드 해줬다.

```kotlin
	// 하단바의 뒤로가기 버튼을 누르더라도 앱이 종료되지 않기 위해
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed() // 하단바의 뒤로가기 버튼 클릭시 종료되는 메소드
        }
    }
```

기본적으로 `super.onBackPressed` 가 뒤로가기 버튼 클릭시 앱이 종료되는 메소드이다. 따라서 webView가 이전 페이지가 있는 경우 `goBack` 메소드를 통해 이전 페이지로 이동하고 그렇지 못한 경우에만 앱을 종료하도록 구현했다.

## 4. 완성도 높이기

우선 기본적인 기능은 구현이 완료됐다. 하지만 아직 뭔가 부족하다. UI가 이쁘지 않은 것을 볼 수 있는데 이를 수정해보려고 한다.

### 상단바 꾸미기

현재 상단바에 존재하는 3개의 버튼을 보면 뒤에 회색 배경이 들어간 것을 볼 수 있다. 이를 제거하기 위해 `?attr/selectableItemBackground` 속성을 통해 drawable 이미지 고유의 배경을 그대로 사용할 수 있다. 

```kotlin
	<ImageButton
            android:id="@+id/goHomeButton"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="?attr/selectableItemBackground"
            android:src="@drawable/ic_home"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintDimensionRatio="1:1"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintTop_toTopOf="parent" />
```

하지만 이렇게 구현하면 기존에 layout_width가 match_content의 속성을 가지고 있었는데 0dp로 변경되면서 UI에서 사라지게 된다. 이를 해결하기 위해 `constraintDimenstionRatio` 속성으로 가로 세로 1:1의 비율을 가지도록 수정했다.

상단 주소창 역시 뭔가 밋밋하다. drawable 이미지를 추가해서 이를 꾸며줬다.

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/light_gray" />
    <corners android:radius="16dp" />

</shape>

```

![bandicam 2021-09-22 09-25-09-304.jpg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/fdea6e61-3c94-44ca-9415-6a0b366bb4b9/bandicam_2021-09-22_09-25-09-304.jpg)

![bandicam 2021-09-22 10-28-05-329.jpg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/22da0487-eb25-42e2-bbf3-49a68cdd3700/bandicam_2021-09-22_10-28-05-329.jpg)

상단바가 전보다 확실히 이뻐졌다.

### SwipeRefreshLayout

근데 뭔가 아직도 부족하다. 생각해보니 화면을 `reload` 해주는 기능이 없었다. 화면을 `reload` 하기 위해선 우선 `dependencies` 에 추가를 해줘야한다.

```kotlin
dependencies {
    implementation 'androidx.core:core-ktx:1.6.0'
    implementation 'androidx.appcompat:appcompat:1.3.1'
    implementation 'com.google.android.material:material:1.4.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.0'
    implementation "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
}
```

`swiperrefreshlayout` 가 추가된 것을 볼 수 있다. 다시 `activity_main.xml` 로 넘어와서 `reload` 가 필요한 레이아웃인 `webView` 영역을 `swiperrefreshlayout` 으로 감싸주면 끝이다.

```kotlin
	<androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        android:id="@+id/refreshLayout"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@id/toolBar">

        <WebView
            android:id="@+id/webView"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
```

해당 레이아웃과 연결해주는 프로퍼티를 선언하고 리로드가 호출되면 `reload` 메소드를 실행하도록 구현했다. 하지만... `reload bar` 가 사라지지 않는다. 

```kotlin
	inner class WebViewClient : android.webkit.WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            refreshLayout.isRefreshing = false
        }
    }
```

이를 해결하기 위해 `Inner class` 로 `WebViewClient` 를 오버라이딩 해줬다. 페이지 로드가 끝나면 `isRefreshing` 을 종료하도록 구현했다.

![bandicam 2021-09-22 10-35-46-479.jpg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/04013649-f679-4e6d-a986-e9795f30da7e/bandicam_2021-09-22_10-35-46-479.jpg)

![bandicam 2021-09-22 10-37-20-415.jpg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/b255f4dd-5bfb-4a7c-94d4-6f17d51de00e/bandicam_2021-09-22_10-37-20-415.jpg)

페이지 로드가 완료되면 `refresh` 가 사라지는 것을 볼 수 있다.

### progressBar

전체적인 UI는 수정했지만 아직도 뭔가 부족한 느낌이다. 사용자가 특정 웹페이지를 방문할 때 페이지가 로드되고 있는지를 알려주는 `progressBar` 를 추가하려고 한다.

```kotlin
	<androidx.core.widget.ContentLoadingProgressBar
        android:id="@+id/progressBar"
        style="@style/Widget.AppCompat.ProgressBar.Horizontal"
        android:layout_width="0dp"
        android:layout_height="2dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@id/toolBar" />
```

`activity_main.xml` 에 ProgressBar를 추가하고 style을 지정해준다. 수평으로 표시하는 progressBar이므로 horizontal 속성을 준다. 우선 페이지 로딩을 표시할 레이아웃은 구현했는데 작동은 어떻게 할까? 이 때 `WebChromeClient` 를 사용한다. 위에서는 `WebClient` , 여기서는 WebChromeClient 둘의 차이는 무엇일까? WebChromeClient은 브라우저 관점에서 자바스크립트 호출 등 좀 더 복잡한 일을 할 수 있고, WebClient는 단순히 콘텐츠를 보여주는? 그런 느낌이다. 

```kotlin
	private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.progressBar)
    }

	// ... 중략

	inner class WebViewClient : android.webkit.WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            refreshLayout.isRefreshing = false
            progressBar.hide()
            goForwardButton.isEnabled = webView.canGoForward()
            goBackButton.isEnabled = webView.canGoBack()
            addressBar.setText(url)
        }
    }

	inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            progressBar.progress = newProgress
        }
    }
```

역시 오버라이딩을 통해 `progressBar` 를 업데이트 해주는 방식을 구현했다. 또한 WebViewClient에서 메소드를 추가해줬는데 페이지가 로딩되기 시작할 때 호출되는 `onPageStarted` 에 `show` 메소드를 추가했고, 페이지 로드가 종료되면 호출되는 `onPageFinished` 에 `hide` 메소드를 추가했다. 또한 각 Button의 `isEnable` 을 통해 이전 페이지가 없는 경우 뒤로 가기 버튼이 눌리지 않게, 다음 페이지가 없는 경우 앞으로 가기 버튼이 눌리지 않게 구현했다. 또한 완전한 url로 바뀌도록 구현했다.

![bandicam 2021-09-22 10-37-20-415.jpg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/aebdc2a2-8832-4669-b589-ff4406f9ca62/bandicam_2021-09-22_10-37-20-415.jpg)

![Screenshot_20210922-111709_WebBrowser.jpg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/9a1760d7-087b-41ff-b49b-0b7dc5ef9c10/Screenshot_20210922-111709_WebBrowser.jpg)

progressBar가 추가된 것을 볼 수 있다. 근데 상단바가 거슬린다.

![bandicam 2021-09-22 11-38-44-921.jpg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/7341abf7-92cf-433b-8554-5b8badd949e3/bandicam_2021-09-22_11-38-44-921.jpg)

![Screenshot_20210922-113908_WebBrowser.jpg](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/0822fd3e-8765-4fcd-8640-3dc750b5c581/Screenshot_20210922-113908_WebBrowser.jpg)

`NoActionBar` 로 변경해서 상단의 ActionBar를 제거해줬다. 나름 괜찮은 웹브라우저 앱을 만들었다.
