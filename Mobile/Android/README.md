# Tutorial for Android base project

## Table of contents:
* [Services used in this project](#aws-services)
* [Install the CLI](#install-amplify-cli)
* [Configuration](#configuration)
* [AppSync Configuration](#AppSync-Configuration)
* [Android Implementation](#Android-Implementation)

## AWS Services
- AWS Cognito
- AWS Pinpoint
- AWS AppSync
- AWS S3

## Install Amplify CLI

System Requirement.
In order to install amplify, requires Node.js® version 8.11.x or later

Install and configure the Amplify CLI as follows:

```bash
$ npm install -g @aws-amplify/cli
$ amplify configure
```

Reference https://github.com/aws-amplify/amplify-cli

## Download Starter source code
Before starting, you should download [Starter Source Code](./Starter) and do steps below. This is empty project which we added neccessary package for work with AWS.

After download, open this project by Android Studio and do below steps

## Configuration
After creating AWS infrastructure by backend source (Base/Starter), you download files `awsconfiguration.json`, `schema.graphql`, `schema.json` from AWS Appsync

File `awsconfiguration.json` will contain all keys, project identifier, ... about AWS services

File `schema.graphql, schema.json` contain information of graphql APIs.

### Modify awsconfiguration.json
You need to replace all values start with prefix `"YOUR_"` in `app/res/raw` of Starter source code. Or you can replace this file by the one which you downloaded from AWS Appsync
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
Open terminal in current project root folder and run init
```bash
amplify init
```
### We did the following:
![alt text][logo]

[logo]: ./image/step1.png 
![alt text][logo1]

[logo1]: ./image/step2.png 
![alt text][logo2]

[logo2]: ./image/step3.png 
![alt text][logo3]

[logo3]: ./image/step4.png 
![alt text][logo4]

[logo4]: ./image/step5.png 
![alt text][logo5]

[logo5]: ./image/step6.png 
![alt text][logo6]

[logo5]: ./image/step7.png 

### Generate schema to code
In order to work with Graphql APIs, after download 2 file `schema.json` and `schema.graphql` from AppSync, you put it in path `app/src/main/graphql`. After that, rebuild project. All your GraphQl APIs will be generate into classes under auto generated folder in project. After that, you can call these API easily via these classes.

# Android Implementation
Note: In order to use function in AWS sdk, you have to import AWSMobileClient to any where you use it.

## Authentication

### 1) Sign up with email and password by Cognito User Pool 
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

After this code executed successfully, there is verification code send to registered mail.
After get this code, you need to verify it by below statement.

```kotlin
AWSMobileClient.getInstance().confirmSignUp(
                "email",
                "confirm_code",
                object : Callback<SignUpResult> {
                    // code handle
                })
```
After that, you can login normally with registered email and password.

### 2) Resend verification Code
In case verification code is expired, you need AWS to resend it. 
Execute below code:

```kotlin
 AWSMobileClient.getInstance().resendSignUp("user_name/email", object : Callback<SignUpResult> {
        // code handle
 })
```

### 3) Login with Email & Password via Cognito User Pool
```kotlin
 AWSMobileClient.getInstance()
                .signIn("email", "password", null, object : Callback<SignInResult> {
                    //code handle
                }
```



### 4) Login with social network

#### a) Login with Facebook by hosted ui

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

#### b) Login with Google by hosted ui

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

## Listen User State
AWS provide function allow user to track user state realtime. Anytime user change state (sign in, sign out...) this method will be excecuted

Firstly, you need to initialize in App.kt
```kotlin
AWSMobileClient.getInstance()
                .initialize(applicationContext, awsConfig, object : Callback<UserStateDetails> {
                    // code handle
                })
```
After that, register listener to listen User State In case of Using Application
```kotlin
AWSMobileClient.getInstance().addUserStateListener(applicationContext, object : Callback<UserStateDetails> {})
```

## Work with Appsync GraphQL
### Config
In order to call GraphQl API from Appsync, first, we need to intialize AppSync config.
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

This statement will read setting from `awsconfiguration.json` file and initialize appSyncClient Object. 

After that, we can call GraphQL API via auto generated class like below

#### Query
```kotlin
App.instance.awsAppSyncClient.query(AllUsersQuery.builder().build())
            ?.responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
            ?.enqueue(object : GraphQLCall.Callback<AllUsersQuery.Data>() {
                // code handle
            })
```

#### Mutation
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

#### Subscription

```kotlin 
 private fun subscriptionListUser() {
        val subscription = OnUpdateUserSubscription1.builder().build()
        subscriptionListUser = App.instance.awsAppSyncClient.subscribe(subscription)
        subscriptionListUser?.execute(object : AppSyncSubscriptionCall.Callback<OnUpdateUserSubscription1.Data> {
            //code handle
        })
    }
```
When any user is deleted, resultHandler callback will be executed.

## Work with AWS S3
- Initialize TransferUtility object by the config for S3 in `awsconfiguration.json`

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
```

- Upload a file to an Amazon S3 bucket.
```kotlin
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
## Work with Analytics

#### Configuration
- Init AWSPinPoint with below code, it read config information from `awsconfiguration.json`

```kotlin
val pinpointConfig = PinpointConfiguration(
                applicationContext,
                AWSMobileClient.getInstance(),
                awsConfig
            )
 pinpointManager = PinpointManager(pinpointConfig)
```

#### Submit Event
In order to understand kind of events which AWS support, you should refer to https://aws-amplify.github.io/docs/android/analytics for more detail. We only describe events which we used in demo.

- Send Event with attribute, Metric, we did the following this code:

```kotlin
private fun logEvent() {
        val event = App.instance.pinpointManager.analyticsClient.createEvent("EventName")
            .withAttribute("Attribute1", "Value")
            .withMetric("Metric", Math.random())
        App.instance.pinpointManager.analyticsClient.recordEvent(event)
        App.instance.pinpointManager.analyticsClient.submitEvents()
    }
```
You can add more attribute,meric by append more .withAttribute and .withMetric

- Send Monetization Event
```kotlin
final AnalyticsEvent event =
       AmazonMonetizationEventBuilder.create(App.instance.pinpointManager.analyticsClient)
           .withCurrency("USD")
           .withItemPrice(10.00)
           .withProductId("DEMO_PRODUCT_ID")
           .withQuantity(1.0)
           .withProductId("DEMO_TRANSACTION_ID").build()
     App.instance.pinpointManager.analyticsClient.recordEvent(event)
     App.instance.pinpointManager.analyticsClient.submitEvents()
```
### [Complete Source Code](./SampleBizFolder)