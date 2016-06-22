# ATS_Nearby
Presence checking with Nearby API for SP ATS

The code cannot be compiled straight from source as it is missing 2 things: - Google API Key
																			- google-services.json

Getting Started
---------------

1. Create a project on
[Google Developer Console](https://console.developers.google.com/). 

1. Click on `APIs & auth -> APIs`, and enable `Nearby Messages API`.

1. Click on `Credentials`, then click on `Create new key`, and pick
`Android key`. Then register your Android app's SHA1 certificate
fingerprint and package name for your app. Use
`org.sp.attendance`
for the package name.

1. Copy the API key generated, and paste it in `gradle.properties` file.

So, your file should have this line `AppKey="1337shamaladingdong420"`.

1. Create a new project on [Firebase Developer Console](https://console.firebase.google.com/).
1. Click on `Add Firebase to your Android app`. Enter `org.sp.attendance` for the package name and click next.
1. `google-services.json` will be downloaded. Put the file in under ATS_Nearby/app

Start building!