package com.o.visitormanagement.model

data class User(var uid : String, var phoneNumber : String, var photoDownloadUrl : String, var vistCount : Int) {
    constructor() : this("", "","", 0)
}