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
 - Requires Node.js® version 8.11.x or later

Install and configure the Amplify CLI as follows:

```bash
$ npm install -g @aws-amplify/cli
$ amplify configure
```

## Install Cocoapod
[Full document](https://guides.cocoapods.org/using/getting-started.html)
<br/>
<br/>
Install by Ruby gem command
```bash
$ sudo gem install cocoapods
```
After that export these PATHs to `.bash_profile`
```bash
export GEM_HOME=$HOME/.gem
export PATH=$GEM_HOME/bin:$PATH
```

## Configuration
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

[logo1]: ../images/step2.png 
![alt text][logo2]

[logo2]: ../images/step3.png 
![alt text][logo3]

[logo3]: ../images/step4.png 
![alt text][logo4]

[logo4]: ../images/step5.png 
![alt text][logo5]

[logo5]: ../images/step6.png 

### After Completion these steps, we did the following this code to create API.swift file:
```bash
amplify add codegen --apiId APPSYNC_API_KEY
```

## iOS Implementation

## Table of contents:
* [Authentication in iOS](#Authentication-iOS)
* [Realtime System in iOS](#Realtime-System-iOS)
* [Storage in iOS](#Storage-iOS)
* [Analytics in iOS](#Analytics-iOS)

### Authentication iOS

#### In the SignInVC.swift file, we did the following:
- add import AWSMobileClient

##### Login with Email & Password in signInBtnTapped function
- We did the following this code to sign in.
```swift
AWSMobileClient.sharedInstance().signIn(username: username, password: password) 
```

##### Login with Facebook in signInByFB
- We did the following this code to sign in / sign up by Facebook.
```swift
let hostedUIOptions = HostedUIOptions(scopes: ["phone", "email", "openid", "profile", "aws.cognito.signin.user.admin"], identityProvider: "Facebook")
// Present the Hosted UI sign in.
AWSMobileClient.sharedInstance().showSignIn(navigationController: self.navigationController!, hostedUIOptions: hostedUIOptions)
```

##### Login with Facebook in signInByFB
- We did the following this code to sign in / sign up by Google.
```swift
let hostedUIOptions = HostedUIOptions(scopes: ["phone", "email", "openid", "profile", "aws.cognito.signin.user.admin"], identityProvider: "Google")
// Present the Hosted UI sign in.
AWSMobileClient.sharedInstance().showSignIn(navigationController: self.navigationController!, hostedUIOptions: hostedUIOptions)
```

#### In the SignUpVC.swift file, we did the following:
- add import AWSMobileClient

##### Sign Up with Email & Password in signupBtnTapped function:
- We did the following this code to sign up.
```swift
AWSMobileClient.sharedInstance().signUp(username: username, password: password, userAttributes: [:], validationData: [:])
```

#### In the ConfirmVC.swift file, we did the following:
- add import AWSMobileClient

##### Confirmation with code in confirmBtnTapped function:
- We did the following this code to confirm.
```swift
AWSMobileClient.sharedInstance().confirmSignUp(username: username, confirmationCode: confirmCode)
```

##### Resend Confirmation Code in resendBtnTapped function:
- We did the following this code to resend confirmation code.
```swift
AWSMobileClient.sharedInstance().resendSignUpCode(username: username)
```

#### Listen User State
- First Time
```swift
AWSMobileClient.sharedInstance().initialize { (userState, error)
```
- Listen User State In case of Using Application
```swift
AWSMobileClient.sharedInstance().addUserStateListener(self) { (userState, info)
```

### Realtime System iOS
#### In the HomeVC.swift file, we did the following:
- Add import AWSAppSync

#### Config
- Add var appSyncClient: AWSAppSyncClient? = nil
- In fetchUsers() function.
- We did the following this code to init appSyncClient
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

#### Query
- We did the following this code to Query Users List.
```swift
appSyncClient?.fetch(query: AllUsersQuery.init(), cachePolicy: .fetchIgnoringCacheData, queue: DispatchQueue.main, resultHandler: { (task, error))
```

#### Mutation
- In deleteIfAdmin function, We did the following this code to delete User.
```swift
appSyncClient?.perform(mutation: DeleteUserMutation(userName: username), queue: DispatchQueue.main, optimisticUpdate: nil, conflictResolutionBlock: nil, resultHandler: { (result, error)
```

#### Subscription
- Add var discard: Cancellable?
- In deleteIfAdmin function, we did the following this code to subscribe.
```swift
discard = try self.appSyncClient?.subscribe(subscription: OnUpdateUserSubscription.init(), resultHandler: { (result, transitions, error)
```

### Storage iOS
#### In the HomeVC.swift file, we did the following:
- Add import AWSS3

- Setup For Upload: In setupAWSUploadConfiguration function, we did the following code.
```swift
let serviceConfiguration = AWSServiceConfiguration(region: .APSoutheast2, 
credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
let transferUtilityConfigurationWithRetry = AWSS3TransferUtilityConfiguration()
transferUtilityConfigurationWithRetry.isAccelerateModeEnabled = false
transferUtilityConfigurationWithRetry.retryLimit = 10
transferUtilityConfigurationWithRetry.multiPartConcurrencyLimit = 6
transferUtilityConfigurationWithRetry.timeoutIntervalForResource = 15*60 //15 minutes

if AWSS3TransferUtility.s3TransferUtility(forKey: "with-retry") != nil {
    return
}
AWSS3TransferUtility.register(
    with: serviceConfiguration!,
    transferUtilityConfiguration: transferUtilityConfigurationWithRetry,
    forKey: "with-retry"
)
```

- In upload function, The following example shows how to upload a file to an Amazon S3 bucket.
```swift
func uploadData(data: Data) {

  let expression = AWSS3TransferUtilityUploadExpression()
     expression.progressBlock = {(task, progress) in
        DispatchQueue.main.async(execute: {
          // Do something e.g. Update a progress bar.
       })
  }

  var completionHandler: AWSS3TransferUtilityUploadCompletionHandlerBlock?
  completionHandler = { (task, error) -> Void in
     DispatchQueue.main.async(execute: {
        // Do something e.g. Alert a user for transfer completion.
        // On failed uploads, `error` contains the error object.
     })
  }

  let transferUtility = AWSS3TransferUtility.default()

  transferUtility.uploadData(data,
       bucket: "YourBucket",
       key: "YourFileName",
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
### Analytics iOS
- Add import AWSPinpoint
- Add var pinpoint: AWSPinpoint?

#### Configuration
- The following example shows how to init AWSPinPoint
```swift
func initPinPoint() {
    let pinpointConfiguration = AWSPinpointConfiguration.defaultPinpointConfiguration(launchOptions: nil)
    pinpoint = AWSPinpoint(configuration: pinpointConfiguration)
}
```

#### Submit Event
- Send Event, we did the following this code:
```swift
let event = analyticsClient.createEvent(withEventType: eventName)
event.addAttribute(attributeValue, forKey: attributeName)
event.addMetric(NSNumber(value: metricValue), forKey: metricName)
analyticsClient.record(event)
analyticsClient.submitEvents()
```

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

- Send Monetization Event, we did the following this code:
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
### [Complete Source Code](iOS/Sample\ Biz)