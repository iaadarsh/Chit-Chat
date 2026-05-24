package com.example.chit_chat

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var publicKey: String? = null

    constructor(){}

    constructor(name: String?, email: String?, uid: String?, publicKey: String?){
        this.name = name
        this.email = email
        this.uid = uid
        this.publicKey = publicKey
    }
}