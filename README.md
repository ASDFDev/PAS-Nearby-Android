# PAS-Nearby-Android

Presence checking using Nearby API

*Designed to improve the current Attendance Taking System(ATS) used in Singapore Polytechnic.*


# Screnshots
![](materials/screenshot.png)
![](materials/receive-broadcast.png)
![](materials/database_layout.gif)

	
Getting Started
---------------

The code cannot be compiled straight from source as it is missing 2 things:

- Google API Key

- google-services.json


1. Create a project on
[Google Developer Console](https://console.developers.google.com/). 

1. Click on `APIs & auth -> APIs`, and enable `Nearby Messages API`.

1. Click on `Credentials`, then click on `Create new key`, and pick
`Android key`. Then register your Android app's SHA1 certificate
fingerprint and package name for your app. Use
`org.sp.attendance`
for the package name.

1. Copy the API key generated, and paste it in `gradle.properties` file.

See [gradle.properties.sample](materials/gradle.properties.sample) file for more info on using the keys.

Personally for me, I have 2 keys; 1 for debugging and 1 for production. You **SHOULD** add your package name and SHA-1 signing-certificate fingerprint to restrict usage to your Android apps during key creation to prevent abuse. 

1. Create a new project on [Firebase Developer Console](https://console.firebase.google.com/).
1. Click on `Add Firebase to your Android app`. Enter `org.sp.attendance` for the package name and click next.
1. `google-services.json` will be downloaded. Put the file in app/


Start building!


TODO
------------
- Use fragment instead of starting activities for lecturer interface. 


Team members
------------
1. Daniel Quah
1. Justin Xin

**Special thanks to Mr. Teo Shin Jen for the help in this project. This project would not be possible without him.**


### License
```
Copyright 2016-2017 Daniel Quah and Justin Xin

This file is part of org.sp.attendance
 
ATS_Nearby is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
ATS_Nearby is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

```
