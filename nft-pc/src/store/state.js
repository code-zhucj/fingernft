import settings from '@/settings';

export default {
  webLoading: false,
  currentRoute: null,
  currentView: null,
  token: null,
  connected: false,
  isLogin: false,
  heartbeatTimer: null,
  notice_unread: 0,
  config: {
    buyerFee: 0,
    ipfsUrl: "",
    networkId: "",
    sellerFee: "",
  },
  web3: {
    address: null,
    coinbase: null,
    error: null,
    instance: null,
    isInjected: false,
    walletType: "",
    networkId: null
  },
  ethBalance: '0',
  erc20Balance: {},
  merchant: {
    isLoginMerchant: false,
    merchantUsername: "",
  },
  user: {
    coinbase: "",
    avatar: "",
    brief: "",
    nickname: "",
    shortUrl: "",
    loginType: "",
    bannerUrl:"",
    id:"",
  },
  payTokens: [],
  defalutPayToken: null,
  categorys: [],
  logs: [],
  logTimer: null,
  ...settings,
};
