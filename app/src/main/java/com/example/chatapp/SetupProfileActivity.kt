package com.example.chatapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import com.example.chatapp.databinding.ActivitySetupProfileBinding
import com.example.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.HashMap

class SetupProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupProfileBinding
    var auth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var selectedImage: Uri? = null
    private var dialog: ProgressDialog? = null
    private var resultLauncher= registerForActivityResult(StartActivityForResult()){
        if (it.resultCode == RESULT_OK && it.data!= null && it.data!!.data != null) {
            val uri = it.data!!.data!!
//            val storage = FirebaseStorage.getInstance()
//            val time = Date().time
//            val reference = storage.reference
//                .child("Profile")
//                .child(time.toString())
//            reference.putFile(uri).addOnCompleteListener{ task ->
//                if (task.isSuccessful){
//                    reference.downloadUrl.addOnCompleteListener { uri ->
//                        val filePath = uri.toString()
//                        val obj = HashMap<String, Any>()
//                        obj["image"] = filePath
//                        database!!.reference
//                            .child("users")
//                            .child(auth!!.uid!!)
//                            .updateChildren(obj).addOnSuccessListener {  }
//                    }
//                }
//            }
            binding.imageView.setImageURI(uri)
            selectedImage = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Updating Profile...")
        dialog!!.setCancelable(false)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        binding.imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }
        binding.continueBtn2.setOnClickListener {
            val name = binding.nameBox.text.toString()
            if (name.isEmpty()){
                binding.nameBox.error = "Please type a name"
            }
            dialog!!.show()
            if (selectedImage != null){
                val reference = storage!!.reference.child("Profile")
                    .child(auth!!.uid!!)
                reference.putFile(selectedImage!!).addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        reference.downloadUrl.addOnSuccessListener { uri ->
                            val imageUri = uri.toString()
                            saveUser(imageUri)
                        }
                    }
                    else{
                        saveUser("No Image")
                    }
                }
            }
        }
    }

    private fun saveUser(imageUri: String){
        val uid = auth!!.uid!!
        val phone = auth!!.currentUser!!.phoneNumber!!
        val name = binding!!.nameBox.text.toString()
        val user = User(uid, name, phone, imageUri)
        database!!.reference
            .child("users")
            .child(uid)
            .setValue(user)
            .addOnCompleteListener {
                dialog!!.dismiss()
                val intent = Intent(this, ChatActivity::class.java)
                startActivity(intent)
                finish()
            }
    }
}