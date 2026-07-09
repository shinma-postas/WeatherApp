# 天気アプリ 仕様書

## 1. アプリ概要

### 1.1 アプリ名

天気検索アプリ

### 1.2 アプリ概要

本アプリは、利用者が都市名を選択し、天気情報を取得して表示するAndroidアプリである。

外部API（Open-Meteo）から天気情報を取得し、現在の気温や天気を画面上に表示する。

Android開発学習用アプリとして作成し、以下の技術要素の習得を目的とする。

- MVVM
- Hilt
- Retrofit
- Coroutines
- Repository
- UseCase
- Navigation Component
- DataBinding
- XML Layout

### 1.3 利用イメージ

```text
アプリ起動

↓

都市選択

↓

天気取得ボタン押下

↓

API通信

↓

気温・天気情報表示
```

表示例

```text
都市：東京

現在気温：31.2℃

天気：晴れ
```

---

# 2. 学習目的

- Activity
- Fragment
- Navigation Component
- DataBinding
- MVVM
- ViewModel
- UseCase
- Repository
- Retrofit
- Kotlin Serialization
- OkHttp
- Coroutines
- Hilt

---

# 3. システム構成

```text
MainActivity

└── WeatherFragment
        │
        ▼
WeatherViewModel
        │
        ▼
GetWeatherUseCase
        │
        ▼
WeatherRepository
        │
        ▼
WeatherApiService
        │
        ▼
Retrofit
        │
        ▼
Open-Meteo API
```

---

# 4. 使用技術

## 必須

- Kotlin
- XML
- DataBinding
- Hilt
- Fragment
- ViewModel
- LiveData
- Retrofit
- Coroutines

---

# 5. 画面レイアウト

## WeatherFragment

```text
┌──────────────────────────────┐
│        天気検索アプリ          │ ← TextView
├──────────────────────────────┤
│ 都市選択                     │
│ [ 東京 ▼ ]                   │ ← Spinner
│                              │
│ [ 天気取得 ]                 │ ← Button
│                              │
├──────────────────────────────┤
│ 現在気温                     │
│ 31.2℃                        │
│                              │
│ 天気                         │
│ 晴れ                         │
└──────────────────────────────┘
```

## 画面項目一覧

| No | 項目名 | UI部品 | 説明 |
|----|--------|---------|------|
| 1 | タイトル | TextView | アプリ名を表示する |
| 2 | 都市選択ラベル | TextView | 都市選択項目を表示する |
| 3 | 都市選択 | Spinner | 都市を選択する |
| 4 | 天気取得ボタン | Button | API通信を実行する |
| 5 | 気温ラベル | TextView | 現在気温の項目名 |
| 6 | 気温表示 | TextView | APIから取得した気温 |
| 7 | 天気ラベル | TextView | 天気の項目名 |
| 8 | 天気表示 | TextView | APIから取得した天気 |

---

# 6. 対象都市

| 都市 | 緯度 | 経度 |
|--------|--------|--------|
| 東京 | 35.6764 | 139.6500 |
| 大阪 | 34.6937 | 135.5023 |
| 福岡 | 33.5902 | 130.4017 |

---

# 7. 機能仕様

## 天気取得

### 入力

利用者が都市を選択する

例

```text
東京
```

### 処理

天気取得ボタン押下

↓

選択された都市の緯度経度取得

↓

API通信

↓

レスポンス受信

↓

画面表示

### 表示

```text
現在気温：31.2℃
天気：晴れ
```

---

# 8. API仕様

## 利用API

Open-Meteo Forecast API

### リクエスト例

Open-Meteoは 緯度(latitude)・経度(longitude) を渡して天気を取得する。
下記は東京（緯度：35.6764、経度：139.6500）の例。

```http
GET https://api.open-meteo.com/v1/forecast?latitude=35.6764&longitude=139.6500&current=temperature_2m,weather_code
```

### レスポンス例

```json
{
  "current": {
    "temperature_2m": 31.2,
    "weather_code": 0
  }
}
```

---

# 9. モデル仕様

## WeatherRequest

```kotlin
data class WeatherRequest(
    val latitude: Double,
    val longitude: Double
)
```

## WeatherResponse

```kotlin
@Serializable
data class WeatherResponse(
    val current: CurrentWeather
)
```

## CurrentWeather

```kotlin
@Serializable
data class CurrentWeather(
    val temperature_2m: Double,
    val weather_code: Int
)
```

## WeatherResult

```kotlin
data class WeatherResult(
    val temperature: String,
    val weatherText: String
)
```

---

# 10. クラス一覧

| クラス名 | 種別 | 役割 |
|-----------|-----------|-----------|
| MainActivity | Activity | Fragmentホスト |
| WeatherFragment | Fragment | UI表示 |
| WeatherViewModel | ViewModel | 画面状態管理 |
| GetWeatherUseCase | UseCase | 天気取得処理 |
| WeatherRepository | Repository | データ取得処理 |
| WeatherApiService | API | Retrofit通信 |
| WeatherRequest | Model | リクエスト情報 |
| WeatherResponse | DTO | APIレスポンス |
| CurrentWeather | DTO | 現在天気情報 |
| WeatherResult | Model | 表示用モデル |

---

# 11. パッケージ構成

```text
com.example.weatherapp

├─ ui
│   ├─ MainActivity
│   └─ WeatherFragment
│
├─ viewmodel
│   └─ WeatherViewModel
│
├─ domain
│   ├─ usecase
│   │   └─ GetWeatherUseCase
│   │
│   └─ model
│       └─ WeatherResult
│
├─ data
│   ├─ api
│   │   └─ WeatherApiService
│   │
│   ├─ repository
│   │   └─ WeatherRepository
│   │
│   └─ dto
│       ├─ WeatherResponse
│       └─ CurrentWeather
│
├─ di
│   └─ NetworkModule
│
└─ util
```

---

# 12. アーキテクチャ

```text
WeatherFragment
       │
       ▼
WeatherViewModel
       │
       ▼
GetWeatherUseCase
       │
       ▼
WeatherRepository
       │
       ▼
WeatherApiService
       │
       ▼
Open-Meteo API
```

---

# 13. 実装時の注意事項

## 必須

- Activityは1つ
- Fragmentは1つ
- XMLレイアウトを利用する
- DataBindingを利用する
- Hiltを利用する
- ViewModelを利用する
- Retrofitを利用する
- Coroutinesを利用する
- API通信を行う

## 意識してほしいこと

- FragmentはUIのみ担当する
- ViewModelは画面状態のみ管理する
- API呼び出しをViewModelに直接書かない
- Repositoryを経由してデータ取得する
- 通信処理はCoroutinesで実装する

## NG例

```text
FragmentからRetrofitを直接呼び出す

ViewModelからRetrofitを直接呼び出す

Fragmentでレスポンス解析を行う
```

## OK例

```text
Fragment
 ↓
ViewModel
 ↓
UseCase
 ↓
Repository
 ↓
Retrofit
```
