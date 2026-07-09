# 天気アプリ サンプルコード

## MainActivity.kt

```kotlin
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
```

## WeatherFragment.kt

```kotlin
@AndroidEntryPoint
class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentWeatherBinding.bind(view)

        binding.btnSearch.setOnClickListener {
            val city = binding.spinnerCity.selectedItem.toString()
            viewModel.getWeather(city)
        }

        viewModel.weather.observe(viewLifecycleOwner) {
            binding.tvTemperature.text = it.temperature
            binding.tvWeather.text = it.weatherText
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

## WeatherViewModel.kt

```kotlin
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _weather = MutableLiveData<WeatherResult>()
    val weather: LiveData<WeatherResult> = _weather

    fun getWeather(city: String) {
        viewModelScope.launch {
            _weather.value = getWeatherUseCase.execute(city)
        }
    }
}
```

## GetWeatherUseCase.kt

```kotlin
class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {

    suspend fun execute(city: String): WeatherResult {

        val location = when (city) {
            "東京" -> 35.6764 to 139.6500
            "大阪" -> 34.6937 to 135.5023
            else -> 33.5902 to 130.4017
        }

        return repository.getWeather(
            location.first,
            location.second
        )
    }
}
```

## WeatherRepository.kt

```kotlin
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) {

    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): WeatherResult {

        val response = apiService.getWeather(
            latitude,
            longitude,
            "temperature_2m,weather_code"
        )

        return WeatherResult(
            temperature = "${response.current.temperature_2m}℃",
            weatherText = weatherCodeToText(
                response.current.weather_code
            )
        )
    }

    private fun weatherCodeToText(code: Int): String {
        return when (code) {
            0 -> "晴れ"
            1, 2, 3 -> "曇り"
            else -> "雨"
        }
    }
}
```

## WeatherApiService.kt

```kotlin
interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String
    ): WeatherResponse
}
```

## DTO

```kotlin
@Serializable
data class WeatherResponse(
    val current: CurrentWeather
)

@Serializable
data class CurrentWeather(
    val temperature_2m: Double,
    val weather_code: Int
)
```

## Model

```kotlin
data class WeatherResult(
    val temperature: String,
    val weatherText: String
)
```

## NetworkModule.kt

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(
                Json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(
        retrofit: Retrofit
    ): WeatherApiService {
        return retrofit.create(
            WeatherApiService::class.java
        )
    }
}
```

## activity_main.xml

```xml
<androidx.fragment.app.FragmentContainerView
    android:id="@+id/navHost"
    android:name="androidx.navigation.fragment.NavHostFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:defaultNavHost="true"
    app:navGraph="@navigation/nav_graph" />
```

## fragment_weather.xml

```xml
<layout>
    <LinearLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:text="天気検索アプリ"
            android:textSize="24sp"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <Spinner
            android:id="@+id/spinnerCity"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />

        <Button
            android:id="@+id/btnSearch"
            android:text="天気取得"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />

        <TextView
            android:text="現在気温"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <TextView
            android:id="@+id/tvTemperature"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <TextView
            android:text="天気"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <TextView
            android:id="@+id/tvWeather"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

    </LinearLayout>
</layout>
```
