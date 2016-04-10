# AutoValue Extension for Android

[![Build Status](https://api.travis-ci.org/xizzhu/simple-tool-tip.svg?branch=master)](https://travis-ci.org/xizzhu/auto-droid)

An extension for Google's [AutoValue](https://github.com/google/auto/tree/master/value) that generates the following for for `@AutoValue` annotated objects:
- `createFromSharedPreferences(SharedPreferences sharedPreferences)`

## Download

## Usage

```java
// annotate your AutoValue objects as usual
@AutoValue
public abstract class MyPreference {
    // indicate that the factory will retrieve the value from shared preferences
    @SharedPreference(key = "keyOfMyInt", defaultValue = "8964")
    abstract int myIntFromPreference();

    // adds the factory method
    public static MyPreference create(SharedPreferences sharedPreferences) {
        return AutoValue_MyPreference.createFromSharedPreferences(sharedPreferences);
    }
}
```

For objects constructed from shared preferences, the following types are supported:
- `boolean` / `Boolean`: the default value is `false` if not specified
- `int` / `Integer`: the default value is `0` if not specified
- `long` / `Long`: the default value is `0L` if not specified
- `float` / `Float`: the default value is `0.0F` if not specified
- `double` / `Double`: the default value is `0.0` if not specified
- `String`: the default value is `null` if not specified
- `Set<String>`: the default value is always `null`

## License

```
Copyright 2016 Xizhi Zhu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
