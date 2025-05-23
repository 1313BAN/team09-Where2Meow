<template>
  <nav>
    <router-link to="/">Home</router-link> | <router-link to="/posts">Posts</router-link> |
    <router-link to="/about">About</router-link>
  </nav>
</template>

<style>
nav {
  padding: 30px;
  background-color: #f1f1f1;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>
