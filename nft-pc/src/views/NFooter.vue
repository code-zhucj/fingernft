<template>
  <div id="home">
    <HeaderTemplate/>
    <BodyTemplate class="main-wrapper" :current-view="currentView" />
  </div>
</template>

<script>
  import HeaderTemplate from "./layout/HeaderTemplate";
  import BodyTemplate from "./layout/BodyTemplate";
export default {
  name: "NFooter",
  components: {
    HeaderTemplate,
    BodyTemplate,
  },
  props: {
    currentView: {
      type: Object,
      default: null
    }
  },
  watch: {
    $route(newRoute) {

    },
  },
  methods: {}
};
</script>

<style lang="scss">
#home {
  width: 100%;
}
</style>
