<template>
  <el-dialog
    :model-value="visible"
    :show-close="false"
    :close-on-click-modal="false"
    @close="$emit('close')"
    @closed="closed"
    custom-class="cel-dialog"
    destroy-on-close
  >
    <template #title>
      <div class="cel-dialog-title">
        <div class="left">
          <span v-if="uri">{{ uri.name }}</span>
        </div>
        <div class="right" @click="$emit('close')">
          <img class="close-img" src="@/assets/create-img/pop_shut.png " />
        </div>
      </div>
    </template>
    <div class="cel-dialog-body" v-if="!confirm">
      <div class="all-error" v-if="formError.all">{{ formError.all }}</div>
      <div class="process">
        <div class="step-info">
          <div class="text">
            <span>{{ $t("dialog.transferCollection") }}</span>
          </div>
        </div>
        <div class="input-group">
          <div class="input-info">
            <div class="tip">
              <span class="text">{{ $t("dialog.transferAddress") }}</span>
            </div>
            <el-input
              class="cel-dialog-input"
              placeholder="0x"
              v-model="form.address"
            >
            </el-input>
            <div class="input-error" v-if="formError.address">
              {{ formError.address }}
            </div>
            <div class="tip">
              <span class="text">{{ $t("dialog.enterQuantity") }}</span>
            </div>
            <el-input
                class="cel-dialog-input"
                placeholder="1"
                type="number"
                v-model="form.quantity"
            >
            </el-input>
            <div class="input-error" v-if="formError.quantity">
              {{ formError.quantity }}
            </div>
          </div>
        </div>
        <div class="button-group">
          <div class="process-btn">
            <el-button @click="$emit('close')">{{
              $t("dialog.cancel")
            }}</el-button>
          </div>
          <div class="process-btn">
            <el-button @click="onSubmit" type="primary" :loading="confirm">{{
              $t("dialog.transfer")
            }}</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="cel-dialog-body" v-else>
      <div class="all-error" v-if="error.all">{{ error.all }}</div>
      <div class="process">
        <div class="step-info">
          <div class="text">
            <span>{{ $t("dialog.transferCollection") }}</span>
          </div>
          <span
            v-if="step.transfer != 1"
            :class="step.transfer == 2 ? 'finish' : ''"
            class="step iconfont icon-seleted"
          ></span>
          <div class="step loading" v-else>
            <img class="loading-animation" src="@/assets/img/loading.png" />
          </div>
        </div>
        <div class="process-btn">
          <el-button
            @click="onTransfer"
            type="primary"
            v-if="step.transfer == 0"
            >{{ $t("dialog.transfer") }}</el-button
          >
          <el-button disabled type="info" v-else-if="step.transfer == 1">{{
            $t("dialog.inProgress")
          }}</el-button>
          <el-button disabled type="info" v-else>{{
            $t("dialog.done")
          }}</el-button>
        </div>
        <div class="process-error" v-if="error.transfer">
          {{ error.transfer }}
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  data: function () {
    return {
      visible: this.show,
      confirm: false,
      form: {
        address: "",
        quantity: "1",
      },
      formError: {
        all: "",
        address: "",
        quantity: "",
      },
      step: {
        transfer: 0,
      },
      error: {
        all: "",
        transfer: "",
      },
    };
  },
  props: {
    show: {
      type: Boolean,
      default: false,
    },
    nft: {
      type: Object,
      default: {},
    },
    asset: {
      type: Object,
      default: {},
    },
    uri: {
      type: Object,
      default: null,
    },
  },
  watch: {
    show(val) {
      this.visible = this.show;
    },
  },
  computed: {
    user() {
      return this.$store.state.user;
    },
  },
  methods: {
    checkForm() {
      let error = false;
      if (!this.form.address) {
        this.formError.address = this.$t("form.noAddress");
        error = true;
      }
      if (!this.form.quantity) {
        this.formError.quantity = this.$t("form.noQuantity");
        error = true;
      }
      console.log(this.form.address, this.$store.state.user.coinbase, "ii");
      if (
        this.form.address.toLowerCase() ==
        this.$store.state.user.coinbase.toLowerCase()
      ) {
        this.formError.address = this.$t("form.own");
        error = true;
      }
      return !error;
    },
    onSubmit() {
      if (!this.checkForm()) return;
      this.confirm = true;
      let that = this;
      setTimeout(async function () {
        await that.onTransfer();
      }, 100);
    },
    async onTransfer() {
      this.step.transfer = 1;
      let asset = {
        address: this.nft.address,
        tokenId: this.nft.tokenId,
        type: this.nft.type,
        to: this.form.address,
        quantity: this.form.quantity,
      };
      let result = await this.transferToken(asset);
      if (result.error) {
        this.error.transfer = result.error;
        this.step.transfer = 0;
      } else {
        this.error.transfer = "";
        this.step.transfer = 2;
        this.$emit("confirm");
      }
    },
    async transferToken(asset) {
      if (asset.type == 3) {
        return await this.$sdk.transferAsset(
          asset,
          this.user.coinbase,
          asset.to
        );
      } else if (asset.type == 2) {
        return await this.$sdk.transferAsset1155(
          asset,
          this.user.coinbase,
          asset.to
        );
      }
    },
    close() {
      this.$emit("close");
    },
    closed() {
      this.confirm = false;
      this.form = {
        address: "",
      };
      this.formError = {
        all: "",
        address: "",
      };
      this.step = {
        transfer: 0,
      };
      this.error = {
        all: "",
        transfer: "",
      };
    },
  },
};
</script>
<style lang="scss" scoped>
</style>

