// import Auth from '@aws-amplify/auth'
const awsmobile = {
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

export default awsmobile
