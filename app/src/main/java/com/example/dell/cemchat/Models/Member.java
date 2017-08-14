package com.example.dell.cemchat.Models;

import java.io.Serializable;

/**
 * Created by DELL on 5/2/2017.
 */

public class Member implements Serializable {
   private String Uid;
    private Boolean mem ;

    public Member(String uid, Boolean mem) {
         this.Uid=uid;
        this.mem = mem;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }

    public Boolean getMem() {
        return mem;
    }

    public void setMem(Boolean mem) {
        this.mem = mem;
    }
}
