package com.example.chit_chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDBRef: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom:String? = null

    private var receiverPublicKey: String? = null
    private var senderPublicKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDBRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name

        // Fetch keys
        mDBRef.child("user").child(receiverUid!!).child("publicKey").get().addOnSuccessListener {
            receiverPublicKey = it.value as String?
        }
        mDBRef.child("user").child(senderUid!!).child("publicKey").get().addOnSuccessListener {
            senderPublicKey = it.value as String?
        }

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        //logic for adding data to recyclerView
        mDBRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for(postSnapshot in snapshot.children){

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        //adding the message to database
        sendButton.setOnClickListener{

            val message = messageBox.text.toString()

            if (senderPublicKey != null && receiverPublicKey != null) {
                // Encrypt for sender (Alice)
                val encryptedForSender = EncryptionUtil.encryptHybrid(message, senderPublicKey!!)
                // Encrypt for receiver (Bob)
                val encryptedForReceiver = EncryptionUtil.encryptHybrid(message, receiverPublicKey!!)

                val senderMessageObject = Message(encryptedForSender, senderUid)
                val receiverMessageObject = Message(encryptedForReceiver, senderUid)

                mDBRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(senderMessageObject).addOnSuccessListener {
                        mDBRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(receiverMessageObject)
                    }
            } else {
                // Fallback to unencrypted if keys are missing
                val messageObject = Message(message, senderUid)
                mDBRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDBRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
            }

            messageBox.setText("")
        }
    }
}
