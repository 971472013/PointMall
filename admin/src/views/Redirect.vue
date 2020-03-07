<template>
  <div />
</template>

<script>

export default {
  name: 'Redirect',
  created() {
    this.$router.push({ path: '/' })
  }
}
</script>
