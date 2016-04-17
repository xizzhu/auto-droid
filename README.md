# AutoValue Extension for Android

[![Build Status](https://api.travis-ci.org/xizzhu/simple-tool-tip.svg?branch=master)](https://travis-ci.org/xizzhu/auto-droid) [![Release](https://jitpack.io/v/xizzhu/auto-droid.svg)](https://jitpack.io/#xizzhu/auto-droid) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-auto--droid-green.svg?style=true)](https://android-arsenal.com/details/1/3429)

An extension for Google's [AutoValue](https://github.com/google/auto/tree/master/value) that generates the following for for `@AutoValue` annotated objects:
- `createFromSharedPreferences(SharedPreferences sharedPreferences)`
- `createFromCursor(Cursor cursor)`
- Parcelable implementations if the class `implements Parcelable`

## Download
Add the following to your `build.gradle`:
```gradle
buildscript {
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}

repositories {
    maven { url "https://jitpack.io" }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies {
    compile 'com.github.xizzhu.auto-droid:auto-droid-annotations:0.1'
    apt 'com.github.xizzhu.auto-droid:auto-droid-processors:0.1'
}
```

## Usage

### From shared preference
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

### From cursor

```java
// annotate your AutoValue objects as usual
@AutoValue
public abstract class MyCursor {
    // indicate that the factory will retrieve the value from cursor
    @ColumnName("keyOfMyInt")
    abstract int myIntFromCursor();

    // indicate to use MyColumnAdapterFactory to generate the value
    // MyColumnAdapterFactory must provide a `static` method that
    // takes a `Cursor` and returns a `MyColumnAdapter`
    @ColumnAdapter(MyColumnAdapterFactory.class)
    abstract MyColumnAdapter myCustomColumn();

    // indicate to implement a method to create a ContentValues object with values put in
    abstract ContentValues toContentValues();

    // adds the factory method
    public static MyPreference create(Cursor cursor) {
        return AutoValue_MyPreference.createFromCursor(cursor);
    }
}
```

For objects constructed from cursors, the following types are supported:
- `int` / `Integer`
- `long` / `Long`
- `short` / `Short`
- `float` / `Float`
- `double` / `Double`
- `byte[]`
- `String`

### Implements parcelable

```java
// annotate your AutoValue objects as usual
// and just tells that it impements Parcelable
@AutoValue
public abstract class MyCursor implements Parcelable {
    // whatever you may have here
}
```

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
