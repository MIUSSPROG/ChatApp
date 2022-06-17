package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.presentation.screens.contact.ContactsFragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null
    var auth: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHost.navController
        auth = Firebase.auth.currentUser
        prepareRootNavController(auth!=null, navController)
    }

    private fun prepareRootNavController(isSignedIn: Boolean, navController: NavController) {
        val graph = navController.navInflater.inflate(getMainNavigationGraphId())
        graph.setStartDestination( if (isSignedIn) {
            getContactsDestination()
        } else {
            getSignInDestination()
        })
        navController.graph = graph
    }

    override fun onDestroy() {
        navController = null
        super.onDestroy()

    }

    override fun onSupportNavigateUp(): Boolean = (navController?.navigateUp() ?: false) || super.onSupportNavigateUp()

    private fun getMainNavigationGraphId(): Int = R.navigation.nav_graph

    private fun getContactsDestination(): Int = R.id.contactsFragment

    private fun getSignInDestination(): Int = R.id.signInFragment
}