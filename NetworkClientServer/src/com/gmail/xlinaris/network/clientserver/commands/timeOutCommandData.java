package com.gmail.xlinaris.network.clientserver.commands;

import java.io.Serializable;

public class timeOutCommandData implements Serializable{

        private boolean flag;

    public timeOutCommandData(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}




