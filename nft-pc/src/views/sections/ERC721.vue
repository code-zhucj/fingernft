<template>
  <!-- 创建-详细界面 -->
  <div class="collection--wrapper">
    <div class="inner">
      <div class="item-page main-wrapper">
        <div class="collection-detail">
          <div class="collection-left">
            <div>
              <div class="go-back" @click="goBack()">
                <span class="iconfont icon-leftarrow"></span>
                <span class="text">{{ $t("erc721.goBack") }}</span>
              </div>
            </div>
            <div class="title">{{ $t("erc721.title") }}</div>

            <el-form
              :model="createForm"
              class="create-form"
              label-position="top"
            >
              <el-form-item :class="['upload-file', { err: errorForm.file }]">
                <div class="form-item-label">{{ $t("erc721.uploadFile") }}</div>
                <el-upload
                  ref="uploadFile"
                  class="upload-pic"
                  v-if="!tempImg && !temAnim"
                  accept="image/gif,image/png,image/jpeg,image/webp,audio/midi,audio/mpeg, audio/webm, audio/mp4, audio/mp3"
                  :limit="1"
                  :auto-upload="false"
                  :on-change="
                    (file, fileList) => {
                      uploadChange(file, fileList, 'uploadFile');
                    }
                  "
                  name="image"
                  :showFileList="false"
                >
                  <template #tip>
                    <div class="upload-tip">{{ $t("erc721.text1") }}</div>
                  </template>
                  <el-button size="small" class="choose-btn">
                    {{ $t("erc721.chooseFile") }}</el-button
                  >
                </el-upload>
                <div
                  class="preview-pic upload-pic"
                  v-else-if="tempImg && !temAnim"
                >
                  <div class="preview-pic-left">
                    <img
                      id="preview-image"
                      class="img"
                      :src="tempImg"
                      @load="onLoadPreview"
                    />
                  </div>
                  <div class="preview-pic-right">
                    <span class="upload-tip"
                      >{{ $t("erc721.measurement") }}:
                      {{ previewImage.width }} ×
                      {{ previewImage.height }}px</span
                    >
                    <el-button
                      class="delete-btn"
                      size="small"
                      type="primary"
                      @click="deletePic"
                      plain
                    >
                      {{ $t("erc721.deletePicture") }}
                    </el-button>
                  </div>
                </div>
                <div
                  class="preview-pic upload-pic audio-section"
                  v-else-if="temAnim"
                >
                  <audio
                    controls
                    controlsList="nodownload"
                    v-if="animFile.type.substring(0, 5) == 'audio'"
                  >
                    <source :src="temAnim" type="audio/ogg" />
                    <source :src="temAnim" type="audio/mpeg" />
                    <source :src="temAnim" type="audio/wav" />
                  </audio>
                  <video
                    controls
                    disablePictureInPicture
                    controlslist="nodownload"
                    v-if="animFile.type.substring(0, 5) == 'video'"
                  >
                    <source :src="temAnim" type="video/mp4" />
                    <source :src="temAnim" type="video/ogg" />
                    <source :src="temAnim" type="video/webm" />
                  </video>
                  <el-button
                    class="delete-btn"
                    size="small"
                    type="primary"
                    @click="deleteAV"
                    plain
                  >
                    {{ $t("erc721.deleteAV") }}
                  </el-button>
                </div>
                <div
                  class="form-error-tip error-position"
                  v-if="errorForm.file && !temAnim"
                >
                  {{ errorForm.file }}
                </div>
              </el-form-item>
              <el-form-item
                :class="['upload-file', { err: errorForm.file }]"
                v-if="temAnim"
              >
                <div class="form-item-label">
                  {{ $t("erc721.chooseCover") }}
                </div>
                <el-upload
                  ref="coverFile"
                  class="upload-pic"
                  v-if="temAnim && !tempImg"
                  accept="image/gif,image/png,image/jpeg,image/webp"
                  :limit="1"
                  :auto-upload="false"
                  :on-change="
                    (file, fileList) => {
                      uploadChange(file, fileList, 'coverFile');
                    }
                  "
                  name="image"
                  :showFileList="false"
                >
                  <template #tip>
                    <div class="upload-tip">{{ $t("erc721.chooseImage") }}</div>
                  </template>
                  <el-button size="small" class="choose-btn">
                    {{ $t("erc721.chooseFile") }}</el-button
                  >
                </el-upload>
                <div class="preview-pic upload-pic" v-if="tempImg && temAnim">
                  <div class="preview-pic-left">
                    <img
                      id="preview-image"
                      class="img"
                      :src="tempImg"
                      @load="onLoadPreview"
                    />
                  </div>
                  <div class="preview-pic-right">
                    <span class="upload-tip"
                      >{{ $t("erc721.measurement") }}:
                      {{ previewImage.width }} ×
                      {{ previewImage.height }}px</span
                    >
                    <el-button
                      class="delete-btn"
                      size="small"
                      type="primary"
                      @click="deletePic"
                      plain
                    >
                      {{ $t("erc721.deletePicture") }}
                    </el-button>
                  </div>
                </div>
                <div
                  class="form-error-tip error-position"
                  v-if="errorForm.file"
                >
                  {{ errorForm.file }}
                </div>
              </el-form-item>
              <el-form-item :class="['put-on-sale', { err: errorForm.price }]">
                <div class="put-on-title">
                  <span>{{ $t("erc721.pos") }}</span>
                  <el-switch class="label-switch" v-model="createForm.onSale">
                  </el-switch>
                </div>
                <el-input
                  class="input-with-select"
                  v-if="createForm.onSale"
                  type="number"
                  placeholder="0.0"
                  :min="0"
                  v-model="createForm.price"
                >
                  <template #suffix>
                    <el-select
                      class="paytoken-selects"
                      v-model="createForm.payAddress"
                    >
                      <el-option
                        v-for="(token, i) in payTokens"
                        :key="i"
                        :label="token.symbol"
                        :value="token.address"
                      >
                      </el-option>
                    </el-select>
                  </template>
                </el-input>

                <el-input v-if="createForm.onSale"
                          type="number"
                          placeholder="1"
                          :min="1"
                          v-model="createForm.quantity">
                  <template #suffix>
                    <el-tag  effect="plain" type="info" >{{$t("dialog.putOnSale")}}{{$t("dialog.enterQuantity")}}</el-tag>
                  </template>
              </el-input>

                <div
                  class="form-error-tip error-position"
                  v-if="errorForm.price"
                >
                  {{ errorForm.price }}
                </div>
                <div class="sale-introduce" v-if="createForm.onSale">
                  <div class="form-tip">
                    <span class="text">{{ $t("erc721.serviceFee") }}</span>
                    <span class="bfont m-left-5"> {{ serverFee }}%</span>
                  </div>
                  <div class="form-tip">
                    <span class="text">{{ $t("erc721.ywr") }} </span>
                    <span class="bfont m-left-5" v-if="payToken">
                      {{ profit }} {{ payToken.symbol }}</span
                    >
                  </div>
                </div>
              </el-form-item>

              <el-form-item class="choose-collection">
                <div class="form-item-label">
                  {{ $t("erc721.chooseCollection") }}
                </div>
                <div class="collection-group">
                  <div
                    class="collection-item"
                    v-for="(collection, i) in contracts"
                    :key="i"
                    :class="createForm.contract == i ? 'active' : ''"
                    @click="selectCollection(i)"
                  >
                    <div class="a">{{ collection.name }}</div>
                    <div class="b">(ERC-721)</div>
                  </div>
                </div>
              </el-form-item>
              <el-form-item
                :class="['production-name', { err: errorForm.name }]"
              >
                <div class="form-item-label">{{ $t("erc721.name") }}</div>
                <el-input
                  class="production-input"
                  :placeholder="$t('erc721.placeholder1')"
                  v-model="createForm.name"
                >
                </el-input>
                <div
                  class="form-error-tip error-position"
                  v-if="errorForm.name"
                >
                  {{ errorForm.name }}
                </div>
              </el-form-item>
              <el-form-item class="production-description">
                <div class="form-item-label">
                  <span class="c">{{ $t("erc721.description") }}</span>
                  <span class="d">({{ $t("erc721.optional") }})</span>
                </div>

                <el-input
                  class="production-input"
                  :placeholder="$t('erc721.placeholder2')"
                  v-model="createForm.description"
                >
                </el-input>
              </el-form-item>
              <el-form-item
                :class="[
                  'production-classification',
                  { err: errorForm.category },
                ]"
              >
                <template #label>
                  <div class="production-title">
                    <span class="c">{{ $t("erc721.classification") }}</span>
                    <span class="d">({{ $t("erc721.optional") }})</span>
                  </div>
                </template>
                <el-select
                  class="classification-select"
                  v-model="createForm.category"
                  :placeholder="$t('erc721.placeholder3')"
                  size="100"
                >
                  <el-option
                    v-for="category in categorys"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                  >
                  </el-option>
                </el-select>
                <div
                  class="form-error-tip error-position"
                  v-if="errorForm.category"
                >
                  {{ errorForm.category }}
                </div>
              </el-form-item>
              <el-form-item
                :class="['production-royalties', { err: errorForm.royalties }]"
              >
                <div class="form-item-label">{{ $t("erc721.royalties") }}</div>
                <el-input
                  class="production-input"
                  placeholder="10"
                  type="number"
                  :min="0"
                  v-model="createForm.royalties"
                >
                  <template #suffix>
                    <div class="input-suffix">%</div>
                  </template>
                </el-input>
                <div class="form-tip">
                  {{ $t("erc721.suggested") }}: 2%, 5%, 8%
                </div>
                <div
                  class="form-error-tip error-position"
                  v-if="errorForm.royalties"
                >
                  {{ errorForm.royalties }}
                </div>
              </el-form-item>
              <el-form-item class="production-properties">
                <div class="form-item-title">{{ $t("erc721.properties") }}</div>
                <div
                  class="property-group"
                  v-for="(property, i) in createForm.properties"
                  :key="i"
                >
                  <div class="property left">
                    <el-input
                      v-model="property.key"
                      :placeholder="$t('erc721.placeholder4')"
                      @input="propertyInput"
                    >
                    </el-input>
                  </div>
                  <div class="property right">
                    <el-input
                      v-model="property.value"
                      :placeholder="$t('erc721.placeholder5')"
                      @input="propertyInput"
                    >
                    </el-input>
                  </div>
                </div>
              </el-form-item>
              <div class="submit-section">
                <el-button
                  width="50%"
                  @click="onSubmit('createForm')"
                  class="submit-btn"
                  type="primary"
                >
                  {{ $t("erc721.createItem") }}
                </el-button>
              </div>
              <div class="form-error-tip error-position" v-if="errorForm.all">
                {{ errorForm.all }}
              </div>
            </el-form>
          </div>

          <div
            id="preview"
            class="detail-right"
          >
            <div class="pre-view">
              <div class="preview-title">{{ $t("erc721.preview") }}</div>
              <div class="pre-box">
                <div class="pre-box-inner">
                  <div class="cover-padding">
                    <div class="cover" v-if="tempImg">
                      <el-image
                        class="cover-image"
                        :src="tempImg"
                        fit="contain"
                      ></el-image>
                    </div>
                    <div class="no-cover" v-else>
                      {{ $t("erc721.text2") }}
                    </div>
                  </div>
                  <div class="descript">
                    <div class="d">
                      <div class="d-left">
                        <div class="avatar">
                          <avatar
                            class="bflex cpointer"
                            :imageUrl="$filters.fullImageUrl(user.avatar)"
                            :address="user.coinbase"
                            :imgHeight="18"
                            :imgWidth="18"
                            shape="circular"
                          >
                          </avatar>
                        </div>
                        <span class="bfont cpointer" v-if="createForm.name">
                          {{ createForm.name }}</span
                        >
                        <span class="cpointer c9" v-else>{{
                          $t("erc721.notSetName")
                        }}</span>
                      </div>
                    </div>
                    <div class="d" v-if="createForm.onSale">
                      <div class="price bfont">
                        {{ createForm.price || 0 }} {{ payToken.symbol }}
                      </div>
                      <div class="stock">1 of 1</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- 预览功能按钮，待完善 -->
  <div class="pre-btn">
    <el-button class="pre-btn-a">{{ $t("erc721.preview") }}</el-button>
  </div>

  <mint-dialog
    :show="showMint"
    @close="showMint = false"
    @confirm="confirm"
    ref="mintDialog"
    :file="file"
    :animFile="animFile"
    :nft="nft"
  >
  </mint-dialog>
</template>
<script>
import MintDialog from "@/components/dialogs/Mint";
import BigNumber from "bignumber.js";

export default {
  name: "ERC721",
  components: {
    MintDialog,
  },
  data: function () {
    let paytoken = this.$store.getters.defaultSalePayToken();
    return {
      previewImage: {
        width: 0,
        height: 0,
      },
      createForm: {
        price: "",
        payAddress: paytoken.address,
        onSale: true,
        contract: 0,
        royalties: 2,
        name: "",
        description: "",
        category: "",
        quantity: "1",
        properties: [
          {
            key: "",
            value: "",
          },
        ],
      },
      errorForm: {
        all: "",
        file: "",
        price: "",
        category: "",
        royalties: "",
        name: "",
      },
      temAnim: null,
      tempImg: null,
      file: null,
      contracts: [],
      nft: {},
      showMint: false,
      animFile: null,
    };
  },
  created() {
    this.init();
  },
  computed: {
    user: function () {
      return this.$store.state.user;
    },
    serverFee: function () {
      var fee = this.$store.state.config.sellerFee;
      if (!fee) return 0;
      return fee / 100;
    },
    payTokens() {
      return this.$store.state.payTokens;
    },
    payToken() {
      let payToken = this.$store.getters.payToken(this.createForm.payAddress);
      return payToken;
    },
    categorys() {
      return this.$store.state.categorys;
    },
    profit: function () {
      var price = this.createForm.price;
      var quantity = this.createForm.quantity;
      price = this.$tools.str2num(price);
      quantity = this.$tools.str2num(quantity);
      if (!price) return 0;
      if (!quantity) return 0;
      price = new BigNumber(this.createForm.price);
      quantity = new BigNumber(this.createForm.quantity);
      let fee = new BigNumber(this.serverFee);
      fee = fee.multipliedBy(price);
      fee = fee.dividedBy(new BigNumber(100));
      let profit = price.minus(fee).multipliedBy(quantity).toFixed();
      return profit;
    },
    config: function () {
      return this.$store.state.config;
    },
  },
  watch: {
    category(v) {
      this.initCategory();
    },
  },
  methods: {
    deleteAV() {
      this.temAnim = null;
      this.audioFile = null;
      this.tempImg = null;
      this.file = null;
    },
    onLoadPreview(e, e1) {
      var img = document.getElementById("preview-image");
      this.previewImage = {
        height: img.naturalHeight,
        width: img.naturalWidth,
      };
    },
    goBack() {
      this.$router.go(-1);
    },
    init() {
      let data = {
        address: this.user.coinbase,
      };
      this.$api("user.listcontract", data).then((res) => {
        if (this.$tools.checkResponse(res)) {
          let that = this;
          this.contracts = res.data.filter(function (contract) {
            return that.$sdk.keyAssetType(contract.type) == "ERC721";
          });
        }
      });
      this.$store.dispatch("payTokens");
      this.$store.dispatch("categorys");
      this.initCategory();
    },
    initCategory() {
      if (this.categorys.length) {
        this.createForm.category = this.categorys[0].id;
      }
    },
    selectCollection(i) {
      this.createForm.contract = i;
    },
    propertyInput(e) {
      let emptyC = 0;
      for (var i = 0; i < this.createForm.properties.length; i++) {
        var property = this.createForm.properties[i];
        if (!property.key || !property.value) emptyC += 1;
      }
      if (emptyC != 1) {
        this.createForm.properties = this.createForm.properties.filter(
          function (property) {
            return property.key || property.value;
          }
        );
        this.createForm.properties.push({ key: "", value: "" });
      }
    },
    fullProperties(properties) {
      var _properties = [];
      for (var i = 0; i < properties.length; i++) {
        var key = properties[i].key.trim();
        var value = properties[i].value.trim();
        var trait_type = key;
        if (!key || !value) continue;
        _properties.push({ key, trait_type, value });
      }
      return _properties;
    },
    uploadChange(file, fileList, name) {
      let size = file.raw.size;
      if (size > 30 * 1024 * 1024) {
        this.errorForm.file = this.$t("form.limitFile");
        if (name == "uploadFile") {
          this.$refs.uploadFile.clearFiles();
        } else {
          this.$refs.coverFile.clearFiles();
        }
        return;
      }
      if (
        file.raw.type.indexOf("video") != -1 ||
        file.raw.type.indexOf("audio") != -1
      ) {
        this.animFile = file.raw;
        var event = event || window.event;
        var file = event.target.files[0];
        var reader = new FileReader();
        var _this = this;
        reader.onload = function (e) {
          _this.temAnim = e.target.result;
        };
        reader.readAsDataURL(file);
      } else if (file.raw.type.indexOf("image") != -1) {
        this.file = file.raw;
        var event = event || window.event;
        var file = event.target.files[0];
        var reader = new FileReader();
        var _this = this;
        reader.onload = function (e) {
          _this.tempImg = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    },
    deletePic() {
      this.tempImg = null;
      this.file = null;
    },
    checkForm() {
      this.errorForm = {
        all: "",
        file: "",
        price: "",
        category: "",
        royalties: "",
        name: "",
      };
      let result = true;
      if (!this.tempImg) {
        this.errorForm.file = this.$t("form.noFile");
        result = false;
      }
      if (this.createForm.onSale) {
        if (!this.$tools.str2num(this.createForm.price)) {
          this.errorForm.price = this.$t("form.noPrice");
          result = false;
        } else if (
          !this.$tools.minPriceLimit(this.createForm.price, this.payToken)
        ) {
          this.errorForm.price = this.$t("form.minPriceLimit");
          result = false;
        }
      }

      if (!this.createForm.name) {
        this.errorForm.name = this.$t("form.noName");
        result = false;
      }
      if (!this.createForm.category) {
        this.errorForm.category = this.$t("form.noCategory");
        result = false;
      }
      if (!result) {
        this.errorForm.all = this.$t("form.validateForm");
      }
      return result;
    },
    onSubmit() {
      if (!this.payToken) {
        this.errorForm.all = "No Select PayType";
        return;
      }
      if (!this.checkForm()) return;
      let contract = this.contracts[this.createForm.contract];
      if (!contract) {
        this.errorForm.all = "No Select Contract";
        return;
      }
      var royalties = this.createForm.royalties;
      royalties = this.$tools.str2num(royalties);
      royalties = !royalties ? 0 : royalties;
      if (royalties > 10) {
        this.errorForm.royalties = "royalties cannot greater than 10%";
        this.errorForm.all = "royalties cannot greater than 10%";
        return;
      }

      this.nft = {
        payToken: this.payToken,
        contract: contract,
        type: 3,
        form: {
          ...this.createForm,
          royalties: this.$tools.decimal(royalties * 100, 0),
          properties: this.fullProperties(this.createForm.properties),
        },
        audioFile: this.audioFile,
        file: this.file,
      };
      let that = this;
      this.showMint = true;
      setTimeout(async function () {
        await that.$refs.mintDialog.start();
      }, 100);
    },
    confirm() {
      if (!this.createForm.onSale) {
        this.$router.push({ path: "/items", query: { tab: "collectibles" } });
      } else {
        this.$router.push({ path: "/items", query: { tab: "sale" } });
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.audio-section {
  flex-direction: column !important;
  align-items: center;
  justify-content: center;
  height: fit-content !important;
  box-sizing: border-box;
  padding: 10px;
  audio {
    margin-bottom: 10px;
    width: 80%;
  }
  video {
    margin-bottom: 10px;
    width: 80%;
    height: 60%;
  }
}
.title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 30px 0 10px;
}
.go-back {
  margin-top: 10px;
}
.form-item-label {
  font-size: 16px;
  font-weight: 400;
  color: #333;
}
.upload-pic {
  height: 140px;
  border: 1px solid #eeeeee;
  border-radius: $borderRadius;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column-reverse;
  .choose-btn {
    width: 200px;
    color: #26ace1;
    background: linear-gradient(
      90deg,
      rgba(30, 188, 216, 15%),
      rgba(52, 147, 241, 15%)
    );
    border: none;
    // opacity: 0.15;
    &:hover,
    &:focus {
      background: linear-gradient(90deg, #1ebcd8, #3493f1) !important;
      border: none !important;
      color: #fff !important;
      opacity: 1;
    }
    &:active {
      background: linear-gradient(90deg, #1ebcd8, #3493f1) !important;
      border-color: none !important;
      color: #fff !important;
      opacity: 1;
    }
  }
  .delete-btn {
    width: 80%;
  }
  &.preview-pic {
    display: flex;
    flex-direction: inherit;
  }
  .preview-pic-left {
    flex: 1;
    display: flex;
    justify-content: center;
    .img {
      max-width: 80%;
      height: 85px;
      border-radius: $borderRadius;
      object-fit: cover;
    }
  }
  .preview-pic-right {
    display: flex;
    flex-direction: column;
    flex: 2;
    align-items: center;
  }
  .upload-tip {
    font-size: 15px;
    font-weight: 400;
    color: $grayColor;
    text-align: center;
  }
}
.put-on-title {
  font-size: 16px;
  // color: lighten($primaryColor, 20%);
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-tip {
  color: #999;
  font-size: 15px;
  font-weight: 400;
  .bfont {
    color: #333;
    font-weight: bold;
  }
}
.collection-group {
  display: flex;
}
.collection-item {
  margin-right: 5px;
  width: 110px;
  cursor: pointer;
  border: $border;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  line-height: 14px;
  &.active {
    width: 170px;
    height: 66px;
    border: 1px solid $primaryColor;
    border-radius: 8px;
  }
  .a {
    font-size: 14px;
    font-weight: bold;
    color: #333;
    margin-bottom: 5px;
  }
  .b {
    font-size: 14px;
    font-weight: 400;
    color: #999;
  }
}
.input-suffix {
  height: 100%;
  display: flex;
  align-items: center;
  padding-right: 5px;
  color: #999;
}

.submit-btn {
  width: 100%;
}
.property-group {
  display: flex;
  .property {
    flex: 1;
    &.left {
      padding-right: 10px;
    }
    &.right {
      padding-left: 10px;
    }
  }
}
.property-group {
  margin-bottom: 10px;
}

.collection-detail {
  position: relative;
  padding-top: 30px;
  padding-bottom: 64px;
  font-weight: 900;
  font-size: 16px;
  width: 800px;
  margin-left: 200px;
  .collection-left {
    max-width: 484px;
  }
  .create-form {
    .sale-introduce {
      line-height: 2;
    }
  }
}

.upload-file {
  .upload-label-title {
    font-size: 16px;
    font-weight: 400;
    color: #000000;
    line-height: 66px;
  }
}
.put-on-sale {
  .sale-introduce-text {
    font-size: 14px;
    font-weight: 400;
    color: #000000;
    line-height: 24px;
    opacity: 0.5;
  }
  .sale-introduce-num {
    width: 72px;
    height: 11px;
    font-size: 14px;
    font-weight: 400;
    color: #000000;
    line-height: 24px;
    margin-left: 2px;
  }
}
.unlock-once-purchased {
  .purchased-title {
    font-size: 18px;
    font-weight: bold;
    color: #000000;
    line-height: 66px;
    background: linear-gradient(0deg, #6e7cf8 0%, #709fe5 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
  .purchased-inreoduce {
    width: 337px;
    font-size: 12px;
    font-weight: 400;
    color: #000000;
    line-height: 28px;
    opacity: 0.5;
  }
  .purchased-tip {
    width: 247px;
    font-weight: 400;
    color: #000000;
    line-height: 32px;
    opacity: 0.5;
    font-size: 14px;
  }
}

.production-name {
  .production-title {
    font-size: 16px;
    font-weight: 400;
    color: #000000;
    // line-height: 66px;
  }
}
.production-description {
  .c {
    font-size: 16px;
    font-weight: 400;
    color: #000000;
  }
}
.production-classification {
  .div#el-popper-5721 {
    z-index: 2030;
    position: absolute;
    left: 700px;
    top: 202px;
    margin: 0px;
  }
  .el-select-dropdown {
    min-width: 100px;
  }
  .production-title {
    font-size: 16px;
    font-weight: 400;
    color: #000000;
    // line-height: 66px;
  }
  .classification-select {
    width: 100%;
  }
}

.production-properties {
  .properties-input {
    width: 42%;
    margin-right: 36px;
  }
}

.submit-section {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 5px;
}

.m-left-5{
  margin-left: 5px !important;
}
.detail-right {
  position: absolute;
  left: 600px;
  top: 110px;
  z-index: 100;
  .preview-title {
    font-size: 16px;
    font-weight: 400;
    color: #333;
    margin-bottom: 10px;
  }
  .pre-box {
    width: 260px;
    background: #ffffff;
    box-shadow: 0px 3px 15px 1px rgba(153, 153, 153, 0.25);
    border-radius: $borderRadius;

    .preview-none {
      width: 146px;
      height: 35px;
      font-size: 14px;
      font-weight: 400;
      color: #1d1e22;
      line-height: 24px;
      opacity: 0.7;
      text-align: center;
    }
    .pre-detail {
      width: 202px;
      height: 304px;
      background: #ffffff;
      box-shadow: 0px 3px 15px 1px rgba(153, 153, 153, 0.25);
      border-radius: 12px;
      .pre-introduction {
        width: 202px;
        position: absolute;
        bottom: 0;
        height: 102px;
        padding-left: 12px;
        .flex {
          display: flex;
          align-items: center;
        }
        .user-name {
          padding-top: 20px;
          resize: none;
          overflow: hidden;
          background: border-box;
          width: 80%;
          height: 16px;
          font-size: 16px;
          font-weight: bold;
          color: #1d1e22;
          border: none;
          text-overflow: ellipsis;
        }
        .nft-symbol {
          max-width: 60%;
          resize: none;
          overflow: hidden;
          min-width: 10px;
          height: 15px;
          font-size: 12px;
          font-weight: 400;
          color: #1d1e22;
          line-height: 18px;
          margin-left: 2px;
        }
      }
      .user-header {
        display: flex;
        position: absolute;
        z-index: 111;
        margin-left: 9px;
        margin-top: 10px;
        .user-box {
          display: flex;
          flex-direction: row;
          position: relative;
        }
        .top-imgs {
          .user-header-left {
            width: 24px;
            height: 24px;
            border-radius: 100%;
            border: 1px solid #ffffff;
            display: flex;
            flex-direction: row;
          }
        }
      }
      .pre-img {
        width: 202px;
        height: 202px;
        border-radius: 12px;
        object-fit: cover;
      }
    }
  }
  .pre-box-inner {
    --coverWidth: 100%;
  }
  .cover-padding {
    position: relative;
    padding-bottom: calc(var(--coverWidth) / 1.33);
  }
  .cover {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    cursor: pointer;
    border-radius: $borderRadius;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .no-cover {
    position: absolute;
    margin-top: -7px;
    top: 50%;
    left: 0;
    width: 100%;
    line-height: 14px;
    cursor: pointer;
    border-radius: $borderRadius;
    text-align: center;
    color: #999;
    font-size: 14px;
  }
  .descript {
    padding: 5px 0;
    font-size: 12px;
  }
  .d {
    padding: 5px;
    display: flex;
    .d-left {
      display: flex;
      flex: 1;
      align-items: center;
    }
    .d-top {
      width: 50%;
      flex: 0 !important;
    }
    .avatar {
      margin-right: 5px;
    }
    .price {
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
      font-size: 12px;
      font-weight: 400;
      color: #1d1e22;
      margin-right: 5px;
    }
    .stock {
      display: flex;
      flex: 1;
      margin-left: 4px;
      margin-right: 6px;
      white-space: nowrap;
      font-size: 12px;
      font-weight: 400;
      color: #999;
      text-align: center;
    }
  }

  .e {
    width: 29px;
    height: 10px;
    font-size: 12px;
    font-weight: 400;
    color: #1d1e22;
    line-height: 12px;
    margin-left: 10px;
    opacity: 0.6;
  }
}
.error-position {
  display: flex;
  justify-content: flex-end;
}
.pre-btn {
  display: none;
}
</style>
