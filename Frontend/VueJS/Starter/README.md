# SKG - VueJS starter project

## Project setup
```sh
$ git clone https://github.com/skglobal-jsc/AWS-Source-Base-Package.git
$ cd Frontend/VueJS/Starter
$ npm i | yarn install
```
Start the dev server

```sh
$ npm run dev | yarn dev
```
Once the development server is started you should be able to reach the demo page (eg. `http://localhost:3000`)


## Development

### Prerequisites

Please make sure all information in this file have been correct before run project.<br>
File `aws-exports.js`.
```
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
```
File `app-sync.js`.
```
const defaultApp = {
  ApiUrl: 'https://YOUR_APP_ID.appsync-api.ap-southeast-2.amazonaws.com/graphql',
  Region: 'YOUR_REGION',
  AuthMode: 'AMAZON_COGNITO_USER_POOLS'
}
```

### Source struture
<p align="left"><img src="https://user-images.githubusercontent.com/26228049/64222383-1589a980-cefa-11e9-939b-9c97a967fdf3.png" alt="SKG" width="250" align="center"/></p>

### Implementation

*** `Authetication`

```
Step 1:
- Create authenticator folder inside components include files:
---------------------
  autheticator.vue
  comfirm-sign-up.vue
  forgot-password.vue
  sign-in.vue
  sign-up.vue
---------------------
An example implement source code which we can reference Sample Project
```
<img width="185" alt="Screen Shot 2019-09-04 at 10 38 50 AM" src="https://user-images.githubusercontent.com/26228049/64224134-9fd50c00-cf00-11e9-9a8d-36a3b0838b82.png">

```
Step 2:
- In App.vue, import authenticator component.

<template>
  <div id="app">
    <h1>SKG - Sample SPA using AWS Cognito and AWS App Sync</h1>
    ...
      <authenticator />
    ...
  </div>
</template>
export default {
  components: {
    Authenticator: () => import('@/components/authenticator/authenticator.vue')
  },
}
```
```
Step 3:
- In SignIn.vue, implement subscription.

+ import lib

import { timer } from 'rxjs'
import { getInfo } from '@/api/auth'
import SignIn from '@/components/authenticator/sign-in.vue'

+ handle subcription

export default {
  ...
  mounted () {
    this.$subscribeTo(timer(2000), () => {
      getInfo().then((user) => {
        if (user) {
          this.visible = false
          this.$router.push({ path: '/' })
        } else {
          this.closable = true
          this.loadding = false
          this.$router.go(0)
        }
      })
    })
  },
  beforeDestroy () {
    this.$router.go(0)
  }
  ...
}
```

*** `Storage`
```
Step 1:
- Create the-avatar.vue inside components
Please make sure assigned the following functions:

<template>
<a-avatar class="avatar" :size="120" :src="tempUrl" icon="user" />
</template>
<script>
import Storage from '@aws-amplify/storage'
export default {
  ...
  data () {
    return {
      tempUrl: ''
    }
  },
  methods: {
    getS3Image () {
      const key = 'avatar'
      const level = 'protected'
      Storage.get(key, { level })
        .then((url) => { this.tempUrl = url })
        .catch(err => this.$message.error('Cannot get user avatar'))
    },
    uploadS3 ({ data, file, onError, onProgress, onSuccess }) {
      const key = 'avatar'
      const level = 'protected'

      Storage.put(key, file, {
        level,
        contentType: file.type,
        cacheControl: '31536000', // 1 year
        progressCallback: ({ total, loaded }) => {
          onProgress({ percent: Math.round((loaded / total) * 100) }, file)
        }
      })
        .then(({ key }) => ({ key, level }))
        .then((response) => { onSuccess(response, file) })
        .catch(onError)
    }
  }
  ...
}
</script>
```
<img width="164" alt="Screen Shot 2019-09-04 at 11 03 49 AM" src="https://user-images.githubusercontent.com/26228049/64224987-b6309700-cf03-11e9-8dbb-bdd287312d26.png">

```
Step 2:
- In Users.vue, import the-avatar.vue component.

<template>
  <section class="users">
    <h1>This is an user list page</h1>
    ...
    <avatar :avatar="avatar" />
    ...
  </section>
</template>
<script>
export default {
  components: {
    Avatar: () => import('@/components/tools/the-avatar.vue')
  }
}
</script>
```


*** `Realtime system`
```
Step 1:
- Implement list all user. In Users.vue import these helper functions

import gql from 'graphql-tag'
import { allUsers } from '@/api/graphql/queries'
import { deleteUser } from '@/api/graphql/mutation'
import { onUpdateUser } from '@/api/graphql/subscriptions'
import { getInfo } from '@/api/auth'
```
```
Step 2:
- Repare layout list user base ant design ui

<template>
  <section class="users">
    <a-row :gutter="16">
      <a-col :xs="18">
        <a-list
          :grid="{ gutter: 16, xs: 1 }"
          :data-source="users"
          :loading="isLoading"
        >
          <a-list-item slot="renderItem" slot-scope="user">
            <p>
              {{ user.Username }} | {{ user.UserStatus }} | {{ getEmail(user.Attributes) }} | {{ userAdmin(user.groups) ? 'ADMIN' : 'USER' }}
            </p>
          </a-list-item>
        </a-list>
        <div v-if="nextToken" class="products__content--pagination">
          <a-button :loading="isLoading" @click="loadMore">
            Load more
          </a-button>
        </div>
      </a-col>
    </a-row>
  </section>
</template>
```
```
Step 3:
- Binding data to template

export default {
  data () {
    return {
      users: [],
      nextToken: null,
      isLoading: false,
      userInfo: {
        username: '',
        attributes: {},
        groups: [],
        idToken: null
      }
    }
  },
  computed: {
    email() {
      return this.userInfo.attributes.email
    },
    isAdmin() {
      return !!this.userInfo.groups.find(item => item.toLocaleLowerCase().includes('admin'))
    }
  },
  methods: {
    async getUserInfo() {
      const userInfo =  await getInfo()
      if(!userInfo) this.$router.push({ path: '/' })
      else Object.assign(this.userInfo, userInfo)
    },
    async fetchAllUsers (params = {}) {
      try {
        this.isLoading = true
        const { data } = await this.$apollo.query({
          query: gql`${allUsers}`,
          variables: { ...params, limit: 5 }
        })
        const { allUsers: { items, nextToken } } = data
        this.users = this.users.concat(items)
        this.nextToken = nextToken

        this.isLoading = false
      } catch (error) {
        this.isLoading = false
        this.$message.error(error.message || 'Cannot load all users !!!')
      }
    },
    loadMore () {
      this.fetchAllUsers({ nextToken: this.nextToken })
    },
    getEmail (attribute) {
      const email = attribute.find(item => item.Name === 'email')
      return email ? email.Value : 'NOT FOUND'
    },
    userAdmin (groups) {
      return !!groups.find(item => item.GroupName === 'Admin')
    }
  }
}
```
```
Step 4:
- Implement realtime system

export default {
  ...
  async created () {
    await this.getUserInfo()
    this.fetchAllUsers()

    this.$subscribeTo(
      this.$apollo.subscribe({ query: gql(onUpdateUser) }),
      (res) => {
        const { data: { onUpdateUser: user } } = res
        this.users = this.users.filter(item => item.Username !== user.Username)
      }
    )
  },
  mounted() {
    this.$bus.$on('change_users', user => {
        this.users = this.users.filter(item => item.Username !== user.Username)
    })
  },
  beforeDestroy() {
    this.$bus.$off('change_users')
  },
  methods: {
    ...
    async handleDelete (userName) {
      try {
        await this.$apollo.mutate({
          mutation: gql`${deleteUser}`,
          variables: { userName }
        })
        this.$message.success('Delete complete')
      } catch (error) {
        this.$message.error(error.message || 'Cannot delete users !!!')
      }
    }
    ...
  }
  ...
}
```


