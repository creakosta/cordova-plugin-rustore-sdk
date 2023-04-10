# Adding cordova-plugin-rustore-sdk to your project

- [Installation using CLI](#installation-using-cli)
- [Manual installation](#manual-installation)
- [Removing the plugin](#remove-plugin)

## <a id="installation-using-cli"> Installation using CLI:

directly from git branch:

```
$ cordova plugin add https://github.com/creakosta/cordova-plugin-rustore-sdk
```

## <a id="manual-installation"> Manual Installation:

1) Add the following code to your `config.xml` file in the root directly of your `www` folder:

```xml
<feature name="RuStorePlugin">
<param name="android-package" value="com.cordova.plugin.RuStorePlugin" /> // TODO
</feature>
```

2) Add the following code to your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

3) Copy rustore.js to `www/js/plugins` and reference it in `index.html`:

```html
<script type="text/javascript" src="js/plugins/rustore.js" />
```

4) Download the source files and copy them to your project:

Copy `RuStorePlugin.kt` to
`platforms/android/src/com/rustore/cordova/plugins` (create the folders if needed)

## <a id="remove-plugin"> Removing the plugin:

```
$ cordova plugin remove cordova-plugin-rustore-sdk
```