# SKG Base Package (SBP) - Source code base with AWS infrastructure

*Table of contents*
1. [Introduction](#intro)
   1. [Who is target for using SBP](#intro1)
   1. [Normal workflow without SBP](#intro2)
   1. [What is SBP?](#intro3)
   1. [What is SBP Benefits?](#intro4)    
   1. [What is SBP Feautures?](#intro5)    
1. [Setup](#setup)
   1. [Prerequisites](#setup1)
   1. [Create and Deploy your First App](#setup2)
   1. [Runtime-configuration](#setup3)
   1. [More configuration](#setup4)   
1. [How to add your Biz parts](#sample)
   1. [Demonstrate with Simple Sample](#sample1)   
   1. [Backend implementation](#sample2)   
   1. [Frontend implementation](#sample3)   
   1. [Backend implementation](#sample4)   
   
# Introduction<a name="intro"></a>

## Who is target for using SBP?<a name="intro1"></a>

   The SBP is using AWS CloudFormation as Infrastructure as Code (IaC). So the SBP requires the user to play as a developer role. The user MUST have basic knowledge about AWS CloudFormation.
Besides that, The SBP using a [serverless framework](https://serverless.com/) for development and coding. So basic knowledge about the serverless framework is required too.

## Normal workflow without SBP<a name="intro2"></a>
There are many ways to start a new project without SBP. But all of them have the same point is "too many steps have to do by manual".

The following will show a sample workflow at my company.

![alt text](https://raw.githubusercontent.com/buithaibinh/AWS-Source-Base-Package/master/docs/workflow.png "workflow without SBP")

In this workflow, the developer MUST do some critical config tasks by manual.
In case, the project needs to re-start, the developer MUST start from (1) and not re-use any steps.
Some time, delete resources of test env is difficult. Resources are scattered and do not link

## What is SBP?<a name="intro3"></a>

The SBP is Infrastructure as Code (IaC). The SBP makes it easy to create, configure, and implement mobile and web apps powered by AWS.  The SBP allows you quickly set up authentication, analytics, push notifications...with a few commands.


## What is SBP Benefits?<a name="intro4"></a>

- A package source code with many base functions: Authentication, Monitoring, Analytics, Notifications...
- CI/CD integration. Auto test, deploy to AWS
- Click to deploy. Easy to start new project.
- Secure: All config will be stored in to AWS Systems Manager.
- Concentrating: All functions base on AWS, developer does not need config or use third party service.
- Save time to develop: Separate dev and production environment. Easy to test and debug
- Monitoring: The Base system included System Monitoring as default. Easy tracking, monitoring system
- Microservice 100%: Pay on demand. No traffic, no pay

## What is SBP Features?<a name="intro5"></a>

### Authentication

- Add user sign-up, sign-in, and access control to mobile and web applications (Using Amazon Cognito).
- Social and enterprise identity federation : Facebook, twitter and google
- Easy integration with your app
- User management built-in

### Analytics

- Drop-in analytics to track user sessions, attributes, and in-app metrics (PowUsing Amazon Pinpoint).
- Aggregate, visualize, and customize data related to your customers and your engagements.

### Notifications

- Integrate push notifications with analytics and targeting built-in.
- AWS SNS and Amazon Pinpoint
- Support both web and mobile(android, iOS)
- Replace Firebase FCM, APN, Onesignal … (Using Amazon Pinpoint + SNS).

### Storage

- Manage user content securely in the cloud (Using Amazon S3).
- Built-in upload/download APIs
- Integrated with your user pool. Role base supported

### System Monitoring

- AWS X-Ray
- AWS CloudWatch

# Setup<a name="setup"></a>

## Prerequisites<a name="setup1"></a>
There are a few prerequisites you need to install and configure:

  - [Initial Setup](#initial-setup)
    - [Install Node.js and NPM](#install-nodejs-and-npm)
    - [Installing the Serverless Framework](#installing-the-serverless-framework)
    - [Configuring your AWS Account with the `aws-cli`](#configuring-your-aws-account-with-the-aws-cli)

If you already have these prerequisites setup you can skip ahead to deploy the BASE package.

### Install Node.js and NPM

- Follow these [installation instructions](https://nodejs.org/en/download/).
- At the end, you should be able to run `node -v` from your command line and get a result like this...

```sh
$ node -v
vx.x.x
```

- You should also be able to run `npm -v` from your command line and should see...

```sh
$ npm -v
x.x.x
```

### Installing the Serverless Framework

Open up a terminal and type `npm install -g serverless` to install Serverless.

```bash
npm install -g serverless
```

Once the installation process is done you can verify that Serverless is installed successfully by running the following command in your terminal:

```bash
serverless
```

To see which version of serverless you have installed run:

```bash
serverless --version
```

### Configuring your AWS Account with the `aws-cli`

To set them up through the `aws-cli` [install it first](http://docs.aws.amazon.com/cli/latest/userguide/installing.html) then run `aws configure` [to configure the aws-cli and credentials](http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html):

```bash
$ aws configure
AWS Access Key ID [None]: AKIAIOSFODNN7EXAMPLE
AWS Secret Access Key [None]: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
Default region name [None]: us-west-2
Default output format [None]: ENTER
```

Credentials are stored in INI format in `~/.aws/credentials`, which you can edit directly if needed. You can change the path to the credentials file via the AWS_SHARED_CREDENTIALS_FILE environment variable. Read more about that file in the [AWS documentation](http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html#cli-config-files)

## Create and Deploy your First App<a name="setup2"></a>

Now that you’ve completed your setup, let’s create and deploy a serverless Service from the BASE.

### Create a new Service from out git

```sh
# Create a new Serverless service/project
$ git clone https://github.com/skglobal-jsc/AWS-Source-Base-Package.git your-service-name

# Change into the newly created directory
$ cd your-service-name/Base/Starter

# Install local development dependencies:
npm install

```

### Pre-configuration

  - Update your config in file serveress.yml
  - Config Social signin (facebook, goolge). File `configs/auth.json`

### Deploy the Service

Use this command to deploy your service for the first time and after you make changes to your Functions, Events or Resources in `serverless.yml` and want to deploy all changes within your Service at the same time.

```bash
serverless deploy -v
```

## Runtime-configuration<a name="setup3"></a>

### Config push notification

  - Create 2 platform applications. 1 for FCM, 1 for APN. [View doc](https://docs.aws.amazon.com/sns/latest/dg/sns-mobile-application-as-subscriber.html)
  - Update file `configs/auth.json` with 2 ARN which created above

```
{
  "applicationTopic": "${self:service}-${self:provider.stage}-App",
  "FCM_APP_ARN": "MY_FCM_APP_ARN",
  "APN_APP_ARN": "MY_APN_APP_ARN"
}
```

### Re-deploy a-gain
```
serverless deploy -v
```

After deploy successful, a config file will be created at `output/aws-exports.json`. Use this file for configuring at the front-end side. Beside that, all information is written to stdout

```
{
  OAuthRedirectURI: 'https://skg-sample-dev.auth.us-west-2.amazoncognito.com/oauth2/idpresponse',
  AppClientSecret: 'u1fakp0dp9uoatv6lcadrq7dnplaasscsk6gqh5q3dj3bld9v5s',
  AppClientID: '26hdkes4aj2othmq4jm6rqjnpg',
  HostedUIDomain: 'skg-sample-dev.auth.us-west-2.amazoncognito.com',
  OAuthMetadata: '{"AllowedOAuthFlows":["code"],"AllowedOAuthScopes":["phone","email","openid","profile","aws.cognito.signin.user.admin"],"CallbackURLs":["http://localhost:3000/user/signed-in/","myapp://"],"LogoutURLs":["http://localhost:3000/","myapp://"]}',
  ServerlessDeploymentBucketName: 'skg-sample-dev-serverlessdeploymentbucket-190t35b8yxme4',
  UserPoolName: 'skg-sample_userpool_dev',
  AppClientIDMobile: '1jfik0fdvhp7d990vu5d5esn9',
  UserPoolId: 'us-west-2_XWSj44NrT',
  AppClientIDWeb: '5agj5h3pe20jvjth8iotqeg96j',
  PinpointId: '09790c212b034e98a890cb1fe47e8e01',
  PinpointRegion: 'us-west-2',
  SkgsampledevapiGraphQlApiUrl: 'https://wskrnncm2fathgx53h2mmxfw6a.appsync-api.us-west-2.amazonaws.com/graphql',
  S3Region: 'us-west-2',
  ProjectRegion: 'us-west-2',
  IdentityPoolId: 'us-west-2:0f101b0b-e44a-431a-bc71-cdeebd33170b',
  CognitoRegion: 'us-west-2',
  S3BucketName: 'skg-sample-dev-s3bucket-1ehvnvw76o0aj',
  PinpointAppName: 'skg-sample_dev',
  IdentityPoolName: 'IdentityPool_oS2xgGboYq44',
  ServiceEndpoint: 'https://i0nbmgnrmb.execute-api.us-west-2.amazonaws.com/dev'
}

```

## More configuration<a name="setup4"></a>


### Configs/auth.json Reference

```json
{

  "allowUnauthenticatedIdentities": true,
  "autoVerifiedAttributes": ["email"],
  "mfaConfiguration": "OFF",
  "mfaTypes": ["SMS Text Message"],
  "smsAuthenticationMessage": "Your authentication code is {####}",
  "smsVerificationMessage": "Your verification code is {####}",
  "emailVerificationSubject": "Your verification code",
  "emailVerificationMessage": "Your verification code is {####}",
  "defaultPasswordPolicy": false,
  "passwordPolicyMinLength": 8,
  "passwordPolicyCharacters": [],
  "requiredAttributes": ["email"],
  "userpoolClientGenerateSecret": true,
  "userpoolClientRefreshTokenValidity": 30,
  "userpoolClientWriteAttributes": ["email"],
  "userpoolClientReadAttributes": ["email"],
  "userpoolClientSetAttributes": false,
  "usernameAttributes": ["email"],
  "hostedUIDomainName": "${self:service}",
  "authProvidersUserPool": ["Facebook", "Google"], // Allow login by facebook and google
  // Update facebook and google setting here.
  // FacebookClientId & FacebookClientSecret
  // GoogleClientId & GoogleClientSecret
  // CallbackURLs && LogoutURLs
  "hostedUIProviderCreds": "[{\"ProviderName\":\"Facebook\",\"client_id\":\"FacebookClientId\",\"client_secret\":\"FacebookClientSecret\"},{\"ProviderName\":\"Google\",\"client_id\":\"GoogleClientId\",\"client_secret\":\"GoogleClientSecret\"}]",
  "hostedUIProviderMeta": "[{\"ProviderName\":\"Facebook\",\"authorize_scopes\":\"email,public_profile\",\"AttributeMapping\":{\"email\":\"email\",\"username\":\"id\",\"name\":\"name\",\"picture\":\"cover\"}},{\"ProviderName\":\"Google\",\"authorize_scopes\":\"openid email profile\",\"AttributeMapping\":{\"email\":\"email\",\"username\":\"sub\",\"name\":\"name\",\"picture\":\"picture\"}}]",
  "oAuthMetadata": "{\"AllowedOAuthFlows\":[\"code\"],\"AllowedOAuthScopes\":[\"phone\",\"email\",\"openid\",\"profile\",\"aws.cognito.signin.user.admin\"],\"CallbackURLs\":[\"http://localhost:3000/user/signed-in/\",\"myapp://\"],\"LogoutURLs\":[\"http://localhost:3000/\",\"myapp://\"]}"
}

```

### Configs/push.json Reference

```json
{
  "applicationTopic": "${self:service}-${self:provider.stage}-App",

  // Create 2 platform applications. 1 for FCM, 1 for APN.
  // Update ARN for this
  "FCM_APP_ARN": "MY_FCM_APP_ARN",
  "APN_APP_ARN": "MY_APN_APP_ARN"
}

```

### Serverless.yml Reference

```yml
# serverless.yml
service: skg-sample # change service name
frameworkVersion: ">=v1.46.0 <2.0.0"
package:
  individually: true # Enables individual packaging for specific function.
  excludeDevDependencies: true
  exclude:
    - coverage/**
    - .circleci/**

plugins:
  - serverless-webpack
  - serverless-scriptable-plugin
  - serverless-appsync-plugin
  - serverless-iam-roles-per-function
  - serverless-plugin-aws-alerts
  - serverless-stage-manager
  - serverless-offline

provider:
  name: aws
  runtime: nodejs10.x
  stackName: ${self:service}-${self:provider.stage}
  stage: ${opt:stage, 'dev'}
  profile: work
  region: ${opt:region, 'us-west-2'} # Overwrite the default region used. Default is us-east-1
  versionFunctions: false
  memorySize: 1024 # Overwrite the default memory size. Default is 1024
  tracing:
    apiGateway: true
    lambda: true
  logs:
    restApi: true
    websocket: true
  iamRoleStatements:
    - Effect: "Allow"
      Action:
        - "xray:PutTraceSegments"
        - "xray:PutTelemetryRecords"
      Resource:
        - "*"
    - Effect: "Allow"
      Action:
        - "sns:*"
      Resource:
        - Ref: ApplicationTopic
        - Fn::Join:
            - ""
            - - "arn:aws:sns:"
              - !Ref "AWS::Region"
              - ":"
              - !Ref "AWS::AccountId"
              - ":app/*"
custom:
  auth: ${file(./configs/auth.json)}
  s3: ${file(./configs/storage.json)}
  pinpoint: ${file(./configs/pinpoint.json)}
  push: ${file(./configs/push.json)}

  scriptHooks:
    #   after:package:compileEvents: scripts/add-pinpoint-id.js
    after:deploy:finalize:
      - scripts/envSetup.js
    remove:remove:
      - scripts/clean.js
  # https://www.npmjs.com/package/serverless-stage-manager
  stages:
    - dev
    - prod
  # https://github.com/ACloudGuru/serverless-plugin-aws-alerts
  alerts:
    stages:
      - dev
      - prod
    dashboards: true
    alarms:
      - functionThrottles
      - functionErrors
      - functionInvocations
      - functionDuration
  serverless-offline:
    port: 4000
    noAuth: true
  serverless-iam-roles-per-function:
    defaultInherit: true
  webpack:
    webpackConfig: ./webpack.config.js
    includeModules:
      forceExclude:
        - aws-sdk
  appSync:
    - name: ${self:service}-${self:provider.stage}-api
      schema:
        - functions/cognito/schema.graphql
      authenticationType: AMAZON_COGNITO_USER_POOLS # API_KEY is also supported
      userPoolConfig:
        awsRegion: ${self:provider.region} # required # region
        defaultAction: ALLOW
        userPoolId:
          Ref: CognitoUserPoolAppUserPool
      mappingTemplatesLocation: functions/mapping-templates
      logConfig:
        loggingRoleArn: {Fn::GetAtt: [AppSyncLoggingServiceRole, Arn]}
        level: ALL
      mappingTemplates:
        - type: Query
          field: allGroups
          kind: PIPELINE
          request: cognito/Query.allGroups.request.vtl
          response: Pipeline.common.response.vtl
          functions:
            - ResolverFunction
        - type: Query
          field: allUsers
          kind: PIPELINE
          request: cognito/Query.allUsers.request.vtl
          response: Pipeline.common.response.vtl
          functions:
            - ResolverFunction
        - type: User
          field: groups
          kind: PIPELINE
          request: cognito/User.groups.request.vtl
          response: Pipeline.common.response.vtl
          functions:
            - ResolverFunction
        - type: Mutation
          field: createUser
          kind: PIPELINE
          request: cognito/Mutation.createUser.request.vtl
          response: Pipeline.common.response.vtl
          functions:
            - ResolverFunction
        - type: Mutation
          field: deleteUser
          kind: PIPELINE
          request: cognito/Mutation.deleteUser.request.vtl
          response: Pipeline.common.response.vtl
          functions:
            - ResolverFunction
      dataSources:
        - type: AWS_LAMBDA
          name: LambdaGraphqlResolver
          description: "Admin only DataSource"
          config:
            functionName: CognitoApi
            serviceRoleArn: {Fn::GetAtt: [AppSyncLambdaServiceRole, Arn]}
      functionConfigurations:
        - dataSource: LambdaGraphqlResolver
          name: ResolverFunction
          request: "Function.common.request.vtl"
          response: "Function.common.response.vtl"
functions:
  - CognitoApi:
      handler: functions/cognito/handler.main
      environment:
        USER_POOL_ID: {Ref: CognitoUserPoolAppUserPool}
      iamRoleStatements:
        - Effect: "Allow"
          Action:
            - "cognito-idp:*"
          Resource:
            - Fn::Join:
                - ""
                - - "arn:aws:cognito-idp:"
                  - Ref: "AWS::Region"
                  - ":"
                  - Ref: "AWS::AccountId"
                  - ":userpool/*"
  - ${file(functions/push/functions.yml)}
resources:
  # Conditions
  - ${file(conditions.yml)}

  # base IAM Roles
  - ${file(roles/cognito-role.yml)}

  # appsync-role
  - ${file(roles/appsync-role.yml)}

  # resources
  - ${file(resources/sns.yml)}
  - ${file(resources/auth.yml)}
  - ${file(resources/storage.yml)}
  - ${file(resources/pinpoint.yml)}

```

# How to add your Biz parts<a name="sample"></a>
## Demonstrate with Simple Sample<a name="sample1"></a>

### Feature Support in this repository

![alt text](https://raw.githubusercontent.com/buithaibinh/AWS-Source-Base-Package/master/docs/functions.png "Feature Support in this repository")

## Backend implementation<a name="sample2"></a>

### Quick Start

```
cd samples/app-backend
yarn deploy
```

Keep output file `aws-exports.json` as reference in front-end app


## Frontend implementation<a name="sample3"></a>
- Biz parts description
- Source code setup
- Step by Step
## Mobile implementation<a name="sample4"></a>
- [Tutorial for iOS base project](Mobile/iOS/README.md)
- [Tutorial for Android base project](Mobile/Android/README.md)
Note:
- Repo structure
  - Base
    - Starter
    - Sample Biz Folder
  - Frontend
    - VueJS branch
      - Starter
      - Sample Biz Folder
    - Flutter branch
      - Starter
      - Sample Biz Folder
  - Mobile
    - IOS branch
      - Starter
      - Sample Biz Folder
    - Android branch
      - Starter
      - Sample Biz Folder
    - Flutter branch
      - Starter
      - Sample Biz Folder
