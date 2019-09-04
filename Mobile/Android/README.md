# Tutorial for Android base project

## Table of contents:
* [Services used in this project](#aws-services)
* [Install the CLI](#install-amplify-cli)
* [Install cocoapod](#install-cocoapod) - Android packages manager tool
* [Configuration](#configuration)
* [AppSync Configuration](#AppSync-Configuration)
* [Android Implementation](#Android-Implementation)

## AWS Services
- AWS Cognito
- AWS Pinpoint
- AWS AppSync
- AWS S3

## Install Amplify CLI
[Full document](https://github.com/aws-amplify/amplify-cli)
 - Requires Node.js® version 8.11.x or later

Install and configure the Amplify CLI as follows:

```bash
$ npm install -g @aws-amplify/cli
$ amplify configure
```

##  Modify your build.gradle and AndroidManifest.xml  
[Full document](https://aws-amplify.github.io/docs/android/start)
<br/>
<br/>
### In project/build.gradle :
```bash
classpath 'com.amazonaws:aws-android-sdk-appsync-gradle-plugin:2.8.+'
```
### In app/build.gradle
```bash
dependencies {
    //Library Aws mobile client
    implementation ('com.amazonaws:aws-android-sdk-mobile-client:2.15.+@aar') { transitive = true }
    implementation 'com.amazonaws:aws-android-sdk-core:2.15.+'
    //Library Aws authentication
    implementation 'com.amazonaws:aws-android-sdk-auth-userpools:2.15.+'
    implementation("com.amazonaws:aws-android-sdk-cognitoauth:2.15.+@aar") { transitive = true }
    implementation 'com.amazonaws:aws-android-sdk-auth-ui:2.15.+'
    //Library Aws Appsync
    implementation 'com.amazonaws:aws-android-sdk-appsync:2.8.+'
    //Library Aws S3
    implementation 'com.amazonaws:aws-android-sdk-s3:2.15.+'
    //Library aws service (recomend 1.2.0 because if using version latest will be get error subscription unstable)
    implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.0'
    implementation 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
    //Library aws pinpoint
    implementation 'com.amazonaws:aws-android-sdk-pinpoint:2.15.+'

    // import notification
    implementation 'com.google.android.gms:play-services-auth:17.0.0'

    implementation 'com.google.firebase:firebase-core:17.0.1'
    implementation 'com.google.firebase:firebase-messaging:19.0.1'
}
apply plugin: 'com.amazonaws.appsync'
```

## Configuration (path: app/res/raw)
File `awsconfiguration.json` will contain all keys, project identifier, ... about AWS services, so please make sure all information in this file have been correct before run project (replace all values start with `"YOUR_"`)
```json
{
    "UserAgent": "aws-amplify-cli/0.1.0",
    "Version": "1.0",
    "IdentityManager": {
        "Default": {}
    },
    "AppSync": {
        "Default": {
            "ApiUrl": "https://YOUR_APP_ID.appsync-api.ap-southeast-2.amazonaws.com/graphql",
            "Region": "YOUR_REGION",
            "AuthMode": "AMAZON_COGNITO_USER_POOLS",
            "ClientDatabasePrefix": "YOUR_PREFIX_AMAZON_COGNITO_USER_POOLS"
        }
    },
    "CredentialsProvider": {
        "CognitoIdentity": {
            "Default": {
                "PoolId": "YOUR_PROVIDER_POOL_ID",
                "Region": "YOUR_REGION"
            }
        }
    },
    "CognitoUserPool": {
        "Default": {
            "PoolId": "YOUR_COGNITO_POOL_ID",
            "AppClientId": "YOUR_APP_CLIENT_ID",
            "AppClientSecret": "YOUR_APP_CLIENT_SECRECT",
            "Region": "YOUR_REGION"
        }
    },
    "Auth": {
        "Default": {
            "OAuth": {
                "WebDomain": "YOUR_COGNITO_WEB_DOMAIN",
                "AppClientId": "YOUR_COGNITO_APP_CLIENT_ID",
                "AppClientSecret": "YOUR_COGNITO_APP_CLIENT_SECRET",
                "SignInRedirectURI": "myapp://",
                "SignOutRedirectURI": "myapp://",
                "Scopes": [
                    "phone",
                    "email",
                    "openid",
                    "profile",
                    "aws.cognito.signin.user.admin"
                ]
            }
        }
    },
    "PinpointAnalytics": {
        "Default": {
            "AppId": "YOUR_PINPOINT_APP_ID",
            "Region": "YOUR_REGION"
        }
    },
    "PinpointTargeting": {
        "Default": {
            "Region": "YOUR_REGION"
        }
    },
    "S3TransferUtility": {
        "Default": {
            "Bucket": "YOUR_S3_BUCKET",
            "Region": "YOUR_REGION"
        }
    }
}
```
## AppSync Configuration
```bash
amplify init
```
### We did the following:
![alt text][logo]

[logo]: ../images/step1.png 
![alt text][logo1]

#### In step 2, choose android
[logo1]: ../images/step2.png 
![alt text][logo2]

[logo2]: ../images/step3.png 
![alt text][logo3]

[logo3]: ../images/step4.png 
![alt text][logo4]

[logo4]: ../images/step5.png 
![alt text][logo5]

[logo5]: ../images/step6.png 

### After Completion these steps, we did the following this code if you have AppSync Api Key:
```bash
amplify add codegen --apiId APPSYNC_API_KEY
```
### If you have created schema and file.graphql for your logic business, you can use codegen like below
- Add file.graphql an schema in file graphql with path (project : app/src/main/graphql)
And then, rebuild app. You will get directory api auto generate

## Android Implementation

## Table of contents:
* [Authentication in Android](#Authentication-Android)
* [Realtime System in Android](#Realtime-System-Android)
* [Storage in Android](#Storage-Android)
* [Analytics in Android](#Analytics-Android)

### Authentication Android

#### In the build.gradle file, we did the following:
- import AWSMobileClient

##### Login with Email & Password in function onTapLoginEmail function
- We did the following this code to sign in.
```kotlin
 AWSMobileClient.getInstance()
                .signIn("email", "password", null, object : Callback<SignInResult> {
                    //code handle
                }
```

##### Login with Facebook in function onTapLoginFacebook
- We did the following this code to sign in / sign up by Facebook.
```kotlin
 val hostedUi = HostedUIOptions.builder()
            .scopes(
                "phone",
                "email",
                "openid",
                "profile",
                "aws.cognito.signin.user.admin"
            )
            .identityProvider("Facebook")
            .build()

        val signInUiOptions = SignInUIOptions.builder()
            .hostedUIOptions(hostedUi)
            .build()
// Present the Hosted UI sign in.
AWSMobileClient.getInstance().showSignIn(this, signInUiOptions, object : Callback<UserStateDetails> {
    // code handle
}

```

##### Login with Google in function onTapLoginGoogle
- We did the following this code to sign in / sign up by Google.
```kotlin
val hostedUiOptions = HostedUIOptions.builder()
            .scopes(
                "phone",
                "email",
                "openid",
                "profile",
                "aws.cognito.signin.user.admin"
            )
            .identityProvider("Google")
            .build()

        val signInUiOptions = SignInUIOptions.builder()
            .hostedUIOptions(hostedUiOptions)
            .build()

// Present the Hosted UI sign in.
AWSMobileClient.getInstance().showSignIn(this, signInUiOptions, object : Callback<UserStateDetails> {
    // code handle
}
```

#### In the RegisterActivity.class file, we did the following:

##### Sign Up with Email & Password in onTapRegisterAccount function:
- We did the following this code to sign up.
```kotlin
AWSMobileClient.getInstance().signUp(
                    "email",
                    "password",
                    attribute,
                    null,
                    object : Callback<SignUpResult> {
                        //code handle
                    })
```

##### Confirmation with code in onTapConfirmCode function:
- We did the following this code to confirm.
```kotlin
AWSMobileClient.getInstance().confirmSignUp(
                "email",
                "confirm_code",
                object : Callback<SignUpResult> {
                    // code handle
                })
```

##### Resend Confirmation Code in onTapResendConfirmCode function:
- We did the following this code to resend confirmation code.
```kotlin
 AWSMobileClient.getInstance().resendSignUp("user_name/email", object : Callback<SignUpResult> {
        // code handle
 })
```

#### Listen User State
- First Time
```kotlin
AWSMobileClient.getInstance()
                .initialize(applicationContext, awsConfig, object : Callback<UserStateDetails> {
                    // code handle
                })
```
- Listen User State In case of Using Application
```kotlin
AWSMobileClient.getInstance().addUserStateListener(applicationContext, object : Callback<UserStateDetails> {})
```

### Realtime System Android
#### In the App.class file, we did the following:
- Add lateinit var awsAppSyncClient : AWSAppSyncClient
- We did the following this code to init appSyncClient
```kotlin
 awsAppSyncClient = AWSAppSyncClient.builder()
            .context(applicationContext)
            .awsConfiguration(AWSConfiguration(applicationContext))
            .cognitoUserPoolsAuthProvider {
                try {
                    AWSMobileClient.getInstance().tokens.idToken.tokenString
                } catch (e: Exception) {
                    Log.e("APPSYNC_ERROR", e.localizedMessage)
                    e.localizedMessage
                }
            }
            .build()
```
#### In the HomeActivity.class file:
##### Query
- We did the following this code to Query Users List.
```kotlin
App.instance.awsAppSyncClient.query(AllUsersQuery.builder().build())
            ?.responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
            ?.enqueue(object : GraphQLCall.Callback<AllUsersQuery.Data>() {
                // code handle
            })
```

##### Mutation
- In deleteIfAdmin function, We did the following this code to delete User.
```kotlin
private fun deleteUser(userName: String) {
        val deleteUser = DeleteUserMutation.builder()
            .userName(userName)
            .build()
        App.instance.awsAppSyncClient.mutate(deleteUser)?.enqueue(object : GraphQLCall.Callback<DeleteUserMutation.Data>() {
            // code handle
        })
    }
```

##### Subscription
- Add var discard: Cancellable?
- In deleteIfAdmin function, we did the following this code to subscribe.
```kotlin 
 private fun subscriptionListUser() {
        val subscription = OnUpdateUserSubscription1.builder().build()
        subscriptionListUser = App.instance.awsAppSyncClient.subscribe(subscription)
        subscriptionListUser?.execute(object : AppSyncSubscriptionCall.Callback<OnUpdateUserSubscription1.Data> {})
    }
```

#### Storage Android

- Upload: In uploadAvatar function, we did the following code.
```kotlin
 val transferUtility = TransferUtility.builder()
            .context(applicationContext)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(
                AmazonS3Client(
                    AWSMobileClient.getInstance().credentials,
                    Region.getRegion(Regions.AP_SOUTHEAST_2)
                )
            )
            .defaultBucket("skg-dev-s3bucket-mbz2y336iyll")
            .build()

 val pathImage = "protected/${AWSMobileClient.getInstance().identityId}/filename"

        val uploadObserver =
            transferUtility.upload(
                pathImage,
                File(fileUri), CannedAccessControlList.PublicRead
            )

        uploadObserver.setTransferListener(object : TransferListener {
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                // handle when progress change
            }

            override fun onStateChanged(id: Int, state: TransferState?) {
                if (TransferState.COMPLETED == state) {
                    // handle when update complete
                } else if (TransferState.FAILED == state) {
                    // handle when update fail 
                }
            }

            override fun onError(id: Int, ex: java.lang.Exception?) {
                // // handle when error
            }
        })
```
### Analytics Android
#### Configuration
- In the file App.class we did init AWSPinPoint
```kotlin 
  lateinit var pinpointManager: PinpointManager
```
```kotlin
val pinpointConfig = PinpointConfiguration(
                applicationContext,
                AWSMobileClient.getInstance(),
                awsConfig
            )
 pinpointManager = PinpointManager(pinpointConfig)
```

#### Submit Event
- Send Event with attribute, Metric,in the file HomeActivity.class, we did the following this code:
```kotlin
private fun logEvent() {
        val event = App.instance.pinpointManager.analyticsClient.createEvent("EventName")
            .withAttribute("Attribute1", "Value")
            .withMetric("Metric", Math.random())
        App.instance.pinpointManager.analyticsClient.recordEvent(event)
        App.instance.pinpointManager.analyticsClient.submitEvents()
    }
```

- Send Event, we did the following this code:
```kotlin
    SessionClient sessionClient = pinpointManager.getSessionClient();
    sessionClient.startSession();
    sessionClient.stopSession();
    pinpointManager.getAnalyticsClient().submitEvents();
```

- Send Monetization Event, we did the following this code:
```kotlin
final AnalyticsEvent event =
       AmazonMonetizationEventBuilder.create(pinpointManager.getAnalyticsClient())
           .withCurrency("USD")
           .withItemPrice(10.00)
           .withProductId("DEMO_PRODUCT_ID")
           .withQuantity(1.0)
           .withProductId("DEMO_TRANSACTION_ID").build();
    pinpointManager.getAnalyticsClient().recordEvent(event);
```
### [Complete Source Code](SampleBizFolder)