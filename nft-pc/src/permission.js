import router from './router'
import { getLocalStorage } from "@/util/local-storage.js"
import store from "./store";


router.beforeEach(async(to, from, next) => {
  const items = getLocalStorage("connected");
  const isLoginMerchant = getLocalStorage("isLoginMerchant").isLoginMerchant;
  if(items.connected){
    if(isLoginMerchant){
      next();
    } else if(to.meta.loginMerchant){
      next(from.path);
    } else {
      next();
    }
  }else{
    if(to.meta.auth){
      next(`/connect?redirect=${to.path}`);
    }else{
      if(isLoginMerchant){
        next();
      } else if(to.meta.loginMerchant){
        next(from.path);
      } else {
        next();
      }
    }
  }
});

