# NetworkCenter
NetworkingCenter is a compilation of tools which will help you easily detect network state changes on Android

# Download
```
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.NiccoloZaza:NetworkingCenter:1.0'
}
```

# How do I use NetworkCenter?
First of all you should initialize NetworkCenter in your Application class
```
class SampleApp: Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkCenter.init(this)
    }
}
```


There are multiple ways of using NetworkCenter. 

You can easily access network state in any object just by using provided extension
```
class TestClass {
    fun doSomething1() {
        if (networkAvailable) {
            // Code
        }
    }

    fun doSomething2() {
        if (networkState == Connectivity.Connected) {
            // Code
        }
    }
}
```

Or you could access those properties and other ones by using NetworkCenter object

```
class TestClass {
    fun doSomething1() {
        if (NetworkCenter.instance.networkAvailable) {
            // Code
        }
    }

    fun doSomething2() {
        if (NetworkCenter.instance.networkState == Connectivity.Connected) {
            // Code
        }
    }

    fun doSomething3() {
        if (NetworkCenter.instance.connectionType == ConnectivityType.Wifi) {
            // Code
        }
    }
}
```

You can use NetworkAwareActivity, NetworkAwareCompatActivity, NetworkAwareFragment and NetworkAwareDialogFragment
```
class SampleActivity : NetworkAwareCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showInfo()
    }

    override fun connectivityChanged(
        connectivity: Connectivity,
        connectivityStrength: ConnectivityStrength,
        connectivityType: ConnectivityType
    ) {
        showInfo()

        Toast.makeText(
            this,
            networkInfo,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showInfo() {
        network_status_text.text = networkInfo
    }

    private val networkInfo: String 
        get() {
            return if (networkAvailable) 
                "Network Available(${NetworkCenter.instance.connectionType.name})" 
            else 
                "Network Not Available"
        }
}
```

To detect NetworkChanges in other classes you can use following example
```
class TestClass: IOnConnectivityChangeListener {
    override fun connectivityChanged(
        connectivity: Connectivity,
        connectivityStrength: ConnectivityStrength,
        connectivityType: ConnectivityType
    ) {
        //code
    }

    fun startObserving() {
        NetworkCenter.instance.addObserver(this)
    }

    fun endObserving() {
        NetworkCenter.instance.removeObserver(this)
    }
}
```
