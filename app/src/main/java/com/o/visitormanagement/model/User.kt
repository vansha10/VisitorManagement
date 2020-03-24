package com.o.visitormanagement.model

import java.io.Serializable

data class User(var uid : String, var phoneNumber : String, var photoDownloadUrl : String, var vistCount : Int) : Serializable{
    constructor() : this("", "","", 0)
}