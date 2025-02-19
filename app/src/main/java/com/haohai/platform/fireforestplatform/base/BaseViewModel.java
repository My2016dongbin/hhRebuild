package com.haohai.platform.fireforestplatform.base;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haohai.platform.fireforestplatform.event.LoadingEvent;
import com.haohai.platform.fireforestplatform.utils.HhLog;

public class BaseViewModel extends ViewModel {
    public final MutableLiveData<String> msg = new MutableLiveData<>();
    public final MutableLiveData<LoadingEvent> loading = new MutableLiveData<>();
    public final MutableLiveData<Object> loginAgain = new MutableLiveData<>();
    public final MutableLiveData<String> phoneChange = new MutableLiveData<>();
    public final MutableLiveData<String> update = new MutableLiveData<>();
    public final MutableLiveData<Tips> tips = new MutableLiveData<>();

    public void showLoginErrorDialog(){
        HhLog.e("弹出提示框");
    }

    public class Tips{
        private String title;
        private String hint;
        private String buttonText;

        public Tips(String hint) {
            title = "温馨提示";
            this.hint = hint;
            buttonText = "我知道了";
        }

        public Tips(String hint, String buttonText) {
            title = "温馨提示";
            this.hint = hint;
            this.buttonText = buttonText;
        }

        public Tips(String title, String hint, String buttonText) {
            this.title = title;
            this.hint = hint;
            this.buttonText = buttonText;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getHint() {
            return hint;
        }

        public void setHint(String hint) {
            this.hint = hint;
        }

        public String getButtonText() {
            return buttonText;
        }

        public void setButtonText(String buttonText) {
            this.buttonText = buttonText;
        }
    }
}

