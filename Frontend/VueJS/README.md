# Tutorial for VueJS base project

## Table of contents:
* [Services used in this project](#aws-services)
* [Install the package](#install-the-pagekage)
* [Configuration](#configuration)
* [AppSync Configuration](#AppSync-Configuration)
* [VueJS Implementation](#VueJS-Implementation)

## AWS Services
- AWS Cognito
- AWS AppSync
- AWS S3
- AWS Analytics

## Install the pagekage
[Full document](https://github.com/aws-amplify/amplify-cli)

System Requirement.
In order to install amplify, requires Node.js® version 8.11.x or later

Clone the repository and install all the necessary dependencies
```sh
$ git clone https://github.com/skglobal-jsc/AWS-Source-Base-Package.git
$ cd Frontend/VueJS/Starter
$ npm i | yarn install
```

## Configuration
After creating AWS infrastructure by backend source (Base/Starter), you download files `aws-exports.js`, `schema.graphql`, `app-sync.js` from AWS Appsync

File `aws-exports.js, app-sync.js` will contain all keys, project identifier, ... about AWS services

File `schema.graphql` contain information of graphql APIs.

### Modify aws-exports.js
You need to replace all values start with prefix `"YOUR_"` in `src/config/aws-exports.js` of Starter source code. Or you can replace this file by the one which you downloaded from AWS Appsync
```js
{
  aws_project_region: 'YOUR_REGION',
  aws_cognito_identity_pool_id: 'YOUR_PROVIDER_POOL_ID',
  aws_cognito_region: 'YOUR_REGION',
  aws_user_pools_id: 'YOUR_USER_POOL_ID',
  aws_user_pools_web_client_id: 'YOUR_USER_POOL_WEB_CLIENT_ID',
  oauth: {
    domain: 'YOUR_COGNITO_WEB_DOMAIN',
    scope: ['phone', 'email', 'openid', 'profile', 'aws.cognito.signin.user.admin'],
    redirectSignIn: 'http://localhost:3000/user/signed-in/',
    redirectSignOut: 'http://localhost:3000/',
    responseType: 'code'
  },
  federationTarget: 'COGNITO_USER_POOLS',
  aws_mobile_analytics_app_id: 'YOUR_MOBILE_ANALYTICS_APP_ID',
  aws_mobile_analytics_app_region: 'YOUR_MOBILE_ANALYTICS_APP_REGION',
  aws_user_files_s3_bucket: 'YOUR_S3_BUCKET',
  aws_user_files_s3_bucket_region: 'YOUR_S3_BUCKET_REGION',
  Storage: {
    AWSS3: {
      bucket: 'YOUR_S3_BUCKET', // REQUIRED -  Amazon S3 bucket
      region: 'YOUR_S3_BUCKET_REGION' // OPTIONAL -  Amazon service region
    }
  }
}
```
## AppSync Configuration
Please make sure all information in this file have been correct before run project.
You need to replace all values start with prefix `"YOUR_"` in `src/config/app-sync.js` of Starter source code.
```js
{
  ApiUrl: 'https://YOUR_APP_ID.appsync-api.ap-southeast-2.amazonaws.com/graphql',
  Region: 'YOUR_REGION',
  AuthMode: 'AMAZON_COGNITO_USER_POOLS'
}
```

# VueJS Implementation
Note: In order to use function in AWS sdk, you have to import Auth to any where you use it.

## Authentication

### 1) Sign up with email and password by Cognito User Pool 
[Reference aws cognito fields](https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-settings-attributes.html)
```js
import Auth from '@aws-amplify/auth'

const user = {
    attributes: {
        email: // Must required email field
    },
    username: // Must required username field,
    password: // Must required password field
}

Auth.signUp(user)
.then((result) => {
    // Handle successfully
})
.catch(error => {
    // Handle error
})
```

### 2) Login with Email & Password via Cognito User Pool
```js
import Auth from '@aws-amplify/auth'

Auth.signIn(username, password)
.then((result) => {
    // Handle successfully
})
.catch(error => {
    // Handle error
})
```

After this code executed successfully, there is verification code send to registered mail.
After get this code, you need to verify it by below statement.

```js
Auth.confirmSignUp(username, code)
.then((result) => {
    // Handle successfully
})
.catch(error => {
    // Handle error
})
```
After that, you can login normally with registered email and password.

### 3) Resend verification Code
In case verification code is expired, you need AWS to resend it. 
Execute below code:

```js
import Auth from '@aws-amplify/auth'

Auth.resendSignUp(username)
.then((result) => {
    // Handle successfully
})
.catch(error => {
    // Handle error
})
```

::: info
After login success, all credentials of user will be stored in `Auth.currentAuthenticatedUser()` to use for other service require authentication
:::

### 4) Login with social network

#### a) Login with Facebook

```js
import Auth from '@aws-amplify/auth'
const provider = 'Facebook'
Auth.federatedSignIn({ provider: provider })
```

#### b) Login with Google

```js
import Auth from '@aws-amplify/auth'
const provider = 'Google'
Auth.federatedSignIn({ provider: provider })
```


## Work with Appsync GraphQL
### Config
In order to call GraphQl API from Appsync, first, we need to intialize AppSync config. This project is using vue-apollo lib to handle query, mutation, subcription.

```js
import Vue from "vue";
import AWSAppSyncClient, { AUTH_TYPE } from "aws-appsync";
import Auth from '@aws-amplify/auth'
import VueApollo from "vue-apollo";

import { defaultApp } from '@/config/app-sync'

const config = {
  url: defaultApp.ApiUrl,
  region: defaultApp.Region,
  auth: {
    type: AUTH_TYPE.AMAZON_COGNITO_USER_POOLS,
    jwtToken: async () => (await Auth.currentSession()).getIdToken().getJwtToken()
  },
  disableOffline: true
};

const options = {
  defaultOptions: {
    watchQuery: {
      fetchPolicy: "cache-and-network"
    }
  }
};

const defaultClient = new AWSAppSyncClient(config, options);

const apolloProvider = new VueApollo({ defaultClient });

Vue.use(VueApollo);

export default apolloProvider;
```

This statement will read setting from aws-exports.js file and initialize appSyncClientObject. 

After run codegen statement, API get all users can be called easily by below code

#### Query
We need prepare queries code
```js
export const allUsers = `query AllUsers(
  $limit: Int
  $nextToken: String
) {
  allUsers(
    limit: $limit
    nextToken: $nextToken
  ) {
    items {
      Enabled
      UserStatus
      Username
      Attributes {
        Name
        Value
      }
      groups {
        GroupName
      }
    },
    nextToken
  }
}`
```
Let's hanlde it
```js
import gql from 'graphql-tag'
import { allUsers } from '@/api/graphql/queries'

this.$apollo.query({
    query: gql`${allUsers}`,
    variables: { limit: ..., nextToken: ... }
})
.then((result) => {
    // Handle successfully
})
.catch(error => {
    // Handle error
})
```

#### Mutation
We need prepare mutation code
```js
export const deleteUser = `mutation DeleteUser($userName: String!) {
  deleteUser(userName: $userName) {
    Username
  }
}`
```
Let's hanlde it
```js
import gql from 'graphql-tag'
import { deleteUser } from '@/api/graphql/mutation'

this.$apollo.mutate({
    mutation: gql`${deleteUser}`,
    variables: { userName }
})
.then((result) => {
    // Handle successfully
})
.catch(error => {
    // Handle error
})
```

#### Subscription
We need prepare subcription code
```js
export const onUpdateUser = `subscription OnUpdateUser {
  onUpdateUser {
    Username
  }
}`
```
Let's hanlde it
```js
import gql from 'graphql-tag'
import { onUpdateUser } from '@/api/graphql/subscriptions'

this.$apollo.subscribe({ query: gql(onUpdateUser) })
.then((result) => {
    // Handle successfully
})
.catch(error => {
    // Handle error
})
```
When any user is deleted, resultHandler callback will be executed.

## Work with AWS S3
- In order to work with AWS, you need to import Storage from '@aws-amplify/storage'

- Upload a file to an Amazon S3 bucket.
```js
import Storage from '@aws-amplify/storage'

Storage.put(key, file, {
    level: // 'private', 'protected' or 'public',
    contentType: file.type,
    cacheControl: '31536000', // 1 year
    progressCallback: ({ total, loaded }) => {
        onProgress({ percent: Math.round((loaded / total) * 100) }, file)
    }
})
.then((result) => {
    // Handle successfully
})
.catch(error => {
    // Handle error
})
```

- Get a file from an Amazon S3 bucket.
```js
import Storage from '@aws-amplify/storage'

Storage.get(key, {
    level: // 'private', 'protected' or 'public'
})
.then((result) => {
    // Handle successfully
})
.catch(error => {
    // Handle error
})
```
## Work with Analytics

#### Configuration
- Add import AWSPinpoint
- Init AWSPinPoint, it read config information from `awsconfiguration.json`

```js
import Analytics from '@aws-amplify/analytics'
import Amplify from '@aws-amplify/core'
Amplify.configure({
  Analytics: {
    disabled: true
  }
})
```

#### Submit Event
- Send Event with attribute, Metric, we did the following this code:

```js
// Record an event
Analytics.record({ name: 'trackEvent' })
```
- Send Custom Event
```js
// Record a custom event
Analytics.record({
    name: 'Album',
    attributes: { genre: 'Rock', year: '1989' }
})
```
### [Complete Source Code](./Sample)