package com.example.chit_chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSingUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSingUp = findViewById(R.id.btnSignUp)

        btnSingUp.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            signUp(name,email,password)
        }
    }

    private fun signUp(name:String, email: String, password: String){
        // logic of creating user

        // Generate keys
        val keyPair = EncryptionUtil.generateKeyPair()
        val publicKeyString = EncryptionUtil.keyToString(keyPair.public)
        val privateKeyString = EncryptionUtil.keyToString(keyPair.private)

        // Save private key locally
        val sharedPreferences = getSharedPreferences("ChatApp", MODE_PRIVATE)
        sharedPreferences.edit().putString("privateKey", privateKeyString).apply()

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Code for jumping to home

                    addUserToDatabase(name, email, mAuth.currentUser?.uid!!, publicKeyString)
                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    finish()
                    startActivity(intent)


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@SignUp, "Some error occurred", Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String, publicKey: String){
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name, email, uid, publicKey))
    }



}