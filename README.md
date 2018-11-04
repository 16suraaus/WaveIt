# WaveIt

Utilizes ZXing barcode scanning library

Barcode scanning library for Android, using [ZXing][2] for decoding.

The project is loosely based on the [ZXing Android Barcode Scanner application][2], but is not affiliated with the official ZXing project.

Features:

1. Can be used via Intents (little code required).
2. Can be embedded in an Activity, for advanced customization of UI and logic.
3. Scanning can be performed in landscape or portrait mode.
4. Camera is managed in a background thread, for fast startup time.

A sample application is available in [Releases](https://github.com/journeyapps/zxing-android-embedded/releases).

By default, Android SDK 19+ is required because of `zxing:core` 3.3.2.
To support SDK 14+, downgrade `zxing:core` to 3.3.0 -
see [instructions](#adding-aar-dependency-with-gradle).



## License

Licensed under the [Apache License 2.0][7]

	Copyright (C) 2012-2018 ZXing authors, Journey Mobile

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.



[1]: http://journeyapps.com
[2]: https://github.com/zxing/zxing/
[3]: https://github.com/zxing/zxing/wiki/Scanning-Via-Intent
[4]: https://github.com/journeyapps/zxing-android-embedded/blob/2.x/README.md
[5]: zxing-android-embedded/src/com/google/zxing/integration/android/IntentIntegrator.java
[7]: http://www.apache.org/licenses/LICENSE-2.0
