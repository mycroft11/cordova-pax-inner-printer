cordova plugin for newland

add below to the AndroidManidest.xml to application where plugin is installing in <application></application>
<activity android:label="Preferences" android:name="com.example.Scanner">
        <intent-filter>
            <action android:name="com.example.Scanner" />
            <category android:name="android.intent.category.DEFAULT" />
        </intent-filter>
    </activity>
