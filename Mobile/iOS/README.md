# Tutorial for iOS base project

## Table of contents:
* [Services used in this project](#aws-services)
* [Install the CLI](#install-amplify-cli)
* [Install cocoapod](#install-cocoapod) - iOS packages manager tool
* [Configuration](#configuration)
* [AppSync Configuration](#AppSync-Configuration)
* [iOS Implementation](#iOS-Implementation)

## AWS Services
- AWS Cognito
- AWS Pinpoint
- AWS AppSync
- AWS S3

## Install Amplify CLI
[Full document](https://github.com/aws-amplify/amplify-cli)

System Requirement.
In order to install amplify, requires Node.js® version 8.11.x or later

Install and configure the Amplify CLI as follows:

```bash
$ npm install -g @aws-amplify/cli
$ amplify configure
```

## Install Cocoapod


Install Cocoapod by Ruby gem command
```bash
$ sudo gem install cocoapods
```
After that export these PATHs to `.bash_profile`
```bash
export GEM_HOME=$HOME/.gem
export PATH=$GEM_HOME/bin:$PATH
```

For more detail, Reference https://guides.cocoapods.org/using/getting-started.html

## Download Starter source code
Before starting, you should download [Starter Source Code](./Starter/AWSDemoStarter) and do steps below. This is empty project which we added neccessary package for work with AWS.

After download, open this project by X-Code and do below steps

## Configuration
After creating AWS infrastructure by backend source (Base/Starter), you download files `awsconfiguration.json`, `schema.graphql`, `schema.json` from AWS Appsync

File `awsconfiguration.json` will contain all keys, project identifier, ... about AWS services

File `schema.graphql, schema.json` contain information of graphql APIs.

### Modify awsconfiguration.json
You need to replace all values start with prefix `"YOUR_"` in `DemoAWS/Common/awsconfiguration.json` of Starter source code. Or you can replace this file by the one which you downloaded from AWS Appsync
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

[logo]: ../images/step1.png 
![alt text][logo1]

[logo1]: ../images/step3.png 
![alt text][logo2]

[logo2]: ../images/step2.png 
![alt text][logo3]

[logo3]: ../images/step4.png 
![alt text][logo4]

[logo4]: ../images/step5.png 
![alt text][logo5]

[logo5]: ../images/step6.png 

### Generate schema to code
In order to work with Graphql APIs, after download 2 file `schema.json` and `schema.graphql`, you put it in path `DemoAWS/`. After that, open terminal and run below code.

```bash
amplify add codegen --apiId APPSYNC_API_KEY
```

 This statement will generate file with name API.swift. All your GraphQl API will be defined by classes in this file. After that, you can call these API easily via these classes. example class `AllUsersQuery` [AppSync usage below](#query)

# iOS Implementation
Note: In order to use function in AWS sdk, you have to import AWSMobileClient to any where you use it.

## Authentication

### 1) Sign up with email and password by Cognito User Pool 
```swift
AWSMobileClient.sharedInstance().signUp(username: username, password: password, userAttributes: [:], validationData: [:])
```

### 2) Login with Email & Password via Cognito User Pool
```swift
AWSMobileClient.sharedInstance().signIn(username: username, password: password) 
```

After this code executed successfully, there is verification code send to registered mail.
After get this code, you need to verify it by below statement.

```swift
AWSMobileClient.sharedInstance().confirmSignUp(username: username, confirmationCode: confirmCode)
```
After that, you can login normally with registered email and password.

### 3) Resend verification Code
In case verification code is expired, you need AWS to resend it. 
Execute below code:

```swift
AWSMobileClient.sharedInstance().resendSignUpCode(username: username)
```

::: info
After login success, all credentials of user will be stored in `AWSMobileClient.sharedInstance().getCredentialsProvider()` to use for other service require authentication
:::

### 4) Login with social network

#### a) Login with Facebook by hosted ui

```swift
let hostedUIOptions = HostedUIOptions(scopes: ["phone", "email", "openid", "profile", "aws.cognito.signin.user.admin"], identityProvider: "Facebook")
// Present the Hosted UI sign in.
AWSMobileClient.sharedInstance().showSignIn(navigationController: self.navigationController!, hostedUIOptions: hostedUIOptions)
```

#### b) Login with Google by hosted ui

```swift
let hostedUIOptions = HostedUIOptions(scopes: ["phone", "email", "openid", "profile", "aws.cognito.signin.user.admin"], identityProvider: "Google")
// Present the Hosted UI sign in.
AWSMobileClient.sharedInstance().showSignIn(navigationController: self.navigationController!, hostedUIOptions: hostedUIOptions)
```

## Listen User State
AWS provide function allow user to track user state realtime. Anytime user change state (sign in, sign out...) this method will be excecuted

Firstly, you need to initialize in HomeVC.swift
```swift
AWSMobileClient.sharedInstance().initialize { (userState, error)
```
After that, register listener to listen User State In case of Using Application
```swift
AWSMobileClient.sharedInstance().addUserStateListener(self) { (userState, info)
```

## Work with Appsync GraphQL
### Config
In order to call GraphQl API from Appsync, first, we need to intialize AppSync config.
```swift
do {
  let cacheConfiguration = try AWSAppSyncCacheConfiguration()
  
  let appSyncPrivateConfig = try AWSAppSyncClientConfiguration(
      appSyncServiceConfig: AWSAppSyncServiceConfig(),
      userPoolsAuthProvider: {
          return MyCognitoUserPoolsAuthProvider()
  }(),cacheConfiguration: cacheConfiguration)
  
  appSyncClient = try AWSAppSyncClient(appSyncConfig: appSyncPrivateConfig)
} catch {
  print("Error initializing appsync client. \(error)")
}
```

This statement will read setting from awsconfiguration.json file and initialize appSyncClientObject. 

After run codegen statement, API get all users can be called easily by below code

#### Query
```swift
appSyncClient?.fetch(query: AllUsersQuery.init(), cachePolicy: .fetchIgnoringCacheData, queue: DispatchQueue.main, resultHandler: { (task, error))
```

#### Mutation
```swift
appSyncClient?.perform(mutation: DeleteUserMutation(userName: username), queue: DispatchQueue.main, optimisticUpdate: nil, conflictResolutionBlock: nil, resultHandler: { (result, error)
```

#### Subscription

```swift
discard = try self.appSyncClient?.subscribe(subscription: OnUpdateUserSubscription.init(), resultHandler: { (result, transitions, error)
```
When any user is deleted, resultHandler callback will be executed.

## Work with AWS S3
- In order to work with AWS, you need to import AWSS3

- Setup For Upload: In setupAWSUploadConfiguration function, we did the following code.
```swift
// Credential configuration
let serviceConfiguration = AWSServiceConfiguration(region: .APSoutheast2, 
credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
// iOS S3 transfer tasks will be managed through an Utility, we need to initiate it first
let transferUtilityConfigurationWithRetry = AWSS3TransferUtilityConfiguration()
transferUtilityConfigurationWithRetry.isAccelerateModeEnabled = false
transferUtilityConfigurationWithRetry.retryLimit = 10
transferUtilityConfigurationWithRetry.multiPartConcurrencyLimit = 6
transferUtilityConfigurationWithRetry.timeoutIntervalForResource = 15*60 //15 minutes

if AWSS3TransferUtility.s3TransferUtility(forKey: "with-retry") != nil {
    return
}
// We register it to Storage object (AWSS3TransferUtility) to use every where in our app access by key-value, key here is `with-retry`
AWSS3TransferUtility.register(
    with: serviceConfiguration!,
    transferUtilityConfiguration: transferUtilityConfigurationWithRetry,
    forKey: "with-retry"
)
```

- Upload a file to an Amazon S3 bucket.
```swift
func uploadData(data: Data) {

  let expression = AWSS3TransferUtilityUploadExpression()
     expression.progressBlock = {(task, progress) in
        DispatchQueue.main.async(execute: {
          // Do something e.g. Update a progress bar.
       })
  }
  // Set public read access to read from link
  expression.setValue("public-read", forRequestHeader: "x-amz-acl")
  // After upload success, url will be in format
  // (https://$YOUR_BUCKET.s3-$YOUR_REGION.amazonaws.com/$YourFileName.ext)
  // Ex: https://skg-dev-s3bucket-ssids.s3-ap-southeast-2.amazonaws.com/skgavatar.jpg

  var completionHandler: AWSS3TransferUtilityUploadCompletionHandlerBlock?
  completionHandler = { (task, error) -> Void in
     DispatchQueue.main.async(execute: {
        // Do something e.g. Alert a user for transfer completion.
        // On failed uploads, `error` contains the error object.
     })
  }

  // Get object from same key `with-retry` from Storage object above
  let transferUtility = AWSS3TransferUtility.s3TransferUtility(forKey: "with-retry")

  transferUtility.uploadData(data,
       bucket: "YourBucket",
       key: "YourFileName.ext", // If want push file in sub folder, use "YourFolder/YourFileName.ext" instead
       contentType: "text/plain",
       expression: expression,
       completionHandler: completionHandler).continueWith {
          (task) -> AnyObject? in
              if let error = task.error {
                 print("Error: \(error.localizedDescription)")
              }

              if let _ = task.result {
                 // Do something with uploadTask.
              }
              return nil;
      }
}
```
## Work with Analytics

#### Configuration
- Add import AWSPinpoint
- Init AWSPinPoint, it read config information from `awsconfiguration.json`

```swift
func initPinPoint() {
    let pinpointConfiguration = AWSPinpointConfiguration.defaultPinpointConfiguration(launchOptions: nil)
    pinpoint = AWSPinpoint(configuration: pinpointConfiguration)
}
```

#### Submit Event
- Send Event with attribute, Metric, we did the following this code:

```swift
let event = analyticsClient.createEvent(withEventType: eventName)
for (key, value) in attributeDict {
    event.addAttribute(value, forKey: key)
}
for (key, value) in metricDict {
    event.addMetric(NSNumber(value: value), forKey: key)
}
analyticsClient.record(event)
analyticsClient.submitEvents()
```

- Send Monetization Event
```swift
let event = analyticsClient.createVirtualMonetizationEvent(
            withProductId: productId,
            withItemPrice: itemPrice,
            withQuantity: quantity,
            withCurrency: currency
)
analyticsClient.record(event)
analyticsClient.submitEvents()
```
### [Complete Source Code](./Sample%20Biz/DemoAWS)
