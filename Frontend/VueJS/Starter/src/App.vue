<template>
  <div id="app">
    <h1>SKG - Sample SPA using AWS Cognito and AWS App Sync</h1>
    <router-view/>
  </div>
</template>
<script>
export default {
  name: 'Root'
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
.nav {
  padding: 15px;
}
</style>
