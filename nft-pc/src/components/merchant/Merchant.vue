<template>
  <div class="login-container" v-if="visible" @click="closeLoginForm">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form"
             auto-complete="on" label-position="left" @click.stop>
      <div class="inner">
        <div class="title-container">

        </div>
        <el-form-item prop="username">
        <span class="svg-container">
          <i class="el-icon-user-solid"></i>
        </span>
          <el-input v-model="loginForm.username" name="username" type="text" tabindex="1"
                    auto-complete="on" :placeholder="$t('placeholder.manager')"/>
        </el-form-item>

        <el-form-item prop="password">
        <span class="svg-container">
          <i class="el-icon-lock"></i>
        </span>
          <el-input v-model="loginForm.password" :type="passwordType" name="password"
                    auto-complete="on" tabindex="2" show-password :placeholder="$t('placeholder.password')"
                    @keyup.enter.native="submitForm"/>
        </el-form-item>
        <div v-if="!loginOrRegister">
          <el-form-item prop="checkPassword">
            <span class="svg-container">
              <i class="el-icon-lock"></i>
            </span>
            <el-input v-model="loginForm.checkPassword" :type="passwordType" name="checkPassword"
                      auto-complete="on" tabindex="3" show-password :placeholder="$t('placeholder.confirmPassword')"
                      @keyup.enter.native="submitForm"/>
          </el-form-item>
        </div>

        <el-form-item prop="code">
        <span class="svg-container">
          <i class="el-icon-lock"></i>
        </span>
          <el-input v-model="loginForm.code" auto-complete="off" name="code" tabindex="2"
                    :placeholder="$t('merchant.verificationCode')" class="width-60" @keyup.enter.native="submitForm"/>
          <div class="login-code">
            <img :src="codeImg" @click="getCode">
          </div>
        </el-form-item>

        <div v-if="loginOrRegister">
          <div class="register-link">
            <el-link @click="onChangeIsRegister">{{ $t('placeholder.toRegister') }}</el-link>
          </div>
          <el-button :loading="loading" type="primary" class="login-btn"
                     @click.native.prevent="handleLogin">{{ $t('merchant.login') }}
          </el-button>
        </div>
        <div v-else>
          <div class="register-link">
            <el-link @click="onChangeIsRegister">
              {{ $t('placeholder.toLogin') }}
            </el-link>
          </div>
          <el-button :loading="loading" type="primary" class="login-btn"
                     @click.native.prevent="register">{{ $t('merchant.register') }}
          </el-button>
        </div>
      </div>
    </el-form>

  </div>
</template>

<script>
export default {
  name: "Merchant",
  props: {
    show: {
      type: Boolean,
      default: false,
    }
  },
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error(this.$t('form.limitMerchant')));
      } else {
        callback();
      }
    };
    const validatePassword2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error(this.$t('placeholder.enterPasswordAgain')));
      } else if (value !== this.loginForm.password) {
        callback(new Error(this.$t('placeholder.differentPassword')));
      } else {
        callback();
      }
    };
    return {
      loginForm: {
        username: "",
        password: "",
        checkPassword: "",
        code: "",
      },
      loginRules: {
        username: [
          {required: true, message: this.$t('form.emptyMerchant'), trigger: "blur"},
        ],
        password: [
          {required: true, message: this.$t('form.emptyPassword'), trigger: "blur"},
          {validator: validatePassword, trigger: "blur"},
        ],
        checkPassword: [
          {validator: validatePassword2, trigger: 'blur'}
        ],
        code: [
          {required: true, message: this.$t('form.emptyCode'), trigger: "blur"},
        ],
      },
      passwordType: "password",
      loading: false,
      redirect: "/home/list",
      codeImg: "",
      visible: false,
      loginOrRegister: true,
    };
  },
  watch: {
    show(v) {
      this.visible = v;
      this.loginOrRegister = true;
      this.getCode();
      this.loginForm.username = "";
      this.loginForm.password = "";
      this.loginForm.checkPassword = "";
      this.loginForm.code = "";
    },
  },
  created() {
    this.getCode();
  },
  methods: {
    onChangeIsRegister() {
      this.loginOrRegister = !this.loginOrRegister;
      this.getCode();
      this.loginForm.username = "";
      this.loginForm.password = "";
      this.loginForm.checkPassword = "";
      this.loginForm.code = "";
    },
    closeLoginForm() {
      this.visible = false;
      this.loading = false;
      this.$emit('closeLoginForm', this.visible);
    },
    changeNetwork(e) {
      this.$store.commit("SET_NETWORK", e);
      this.getCode();
    },
    getCode() {
      this.$api('merchant.kaptcha').then(response => {
        this.codeImg = response.data
      })
    },
    submitForm() {
      if (this.loginOrRegister) {
        this.handleLogin();
      } else {
        this.register();
      }
    },
    register() {
      this.$refs.loginForm.validate((valid) => {
        if (valid && !this.loading) {
          this.loading = true;
          this.$store
              .dispatch("registerMerchant", this.loginForm)
              .then((res) => {
                this.loading = false;
                if (this.$tools.checkResponse(res)) {
                  this.closeLoginForm();
                } else {
                  this.getCode();
                }
              })
              .catch((response) => {
                this.getCode();
                this.$notify.error({
                  title: this.$t('merchant.loginFailed'),
                  message: this.$t('response.' + response.errno)
                });
                this.loading = false;
              });
        } else {
          return false;
        }
      });
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid && !this.loading) {
          this.loading = true;
          this.$store
              .dispatch("signLoginMerchant", this.loginForm)
              .then((res) => {
                this.loading = false;
                if (this.$tools.checkResponse(res)) {
                  this.closeLoginForm();
                  this.$message({
                    message: this.$t('merchant.loginSuccess'),
                    type: 'success'
                  });
                } else {
                  this.getCode();
                  this.$message({
                    message: this.$t('merchant.loginFailed'),
                    type: 'error',
                    center: true
                  });
                }
              })
              .catch((response) => {
                this.getCode();
                this.$notify.error({
                  title: this.$t('merchant.loginFailed'),
                  message: this.$t('response.' + response.errno)
                });
                this.loading = false;
              });
        } else {
          return false;
        }
      });
    },
  },
};
</script>

<style lang="scss">

$bg: rgba(40, 52, 67, 0);
$light_gray: #fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      height: 47px;
      caret-color: rgba(12, 12, 12, 0.98);
      color: #0c0c0c;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px #fff inset !important;
        -webkit-text-fill-color: #333 !important;
      }
    }
  }

  .el-form-item {
    border-bottom: 1px solid #f0f0f0;
    color: #454545;
    margin-bottom: 30px;
  }
}
</style>

<style lang="scss" scoped>
$bg: rgba(255, 255, 255, 0);
$dark_gray: #151414;
$light_gray: #5b5959;

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;
  background-size: cover;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  padding-top: 10%;

  .login-form {
    box-shadow: 3px 3px 3px 3px rgb(0 0 0 / 80%);
    background: rgb(249, 252, 252);
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 10px;
    margin: auto;
    overflow: hidden;
    border-radius: 20px;

    .inner {
      padding: 60px 35px;
      background: rgba(117, 101, 101, 0);
      border-radius: 20px;
    }
  }

  .login-code {
    padding-top: 5px;
    float: right;

    img {
      cursor: pointer;
      vertical-align: middle
    }
  }

  .tips {
    font-size: 14px;
    color: rgba(26, 25, 25, 0.95);
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      margin: 0px auto 20px auto;
      text-align: center;
      font-weight: bold;
    }
  }

}

.login-btn {
  width: 100%;
  margin-top: 20px;
}

.register-link {
  padding-right: 0px;
  width: 100%;

  .el-link {
    float: right;
  }
}

.width-60 {
  width: 60%;
}

</style>

