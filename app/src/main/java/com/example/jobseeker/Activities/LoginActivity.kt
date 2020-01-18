package com.example.jobseeker.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.jobseeker.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {

        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001

    }

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().getReference("Client")

        signInBtn.setOnClickListener {

            signIn()

        }

    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if (currentUser != null) {

            val intent = Intent(this, UserMainActivity::class.java)

            startActivity(intent)

            finish()

        }

    }

    private fun signIn() {

        val signInIntent = googleSignInClient.signInIntent

        startActivityForResult(
            signInIntent,
            RC_SIGN_IN
        )


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {

                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)

            } catch (e: ApiException) {

                Log.w(TAG, "Google sign in failed", e)

            }

        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show()

                    val userListener = object : ValueEventListener {

                        override fun onDataChange(ds: DataSnapshot) {

                            checkNewUser(user!!, ds)

                        }

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                    }

                    database.addListenerForSingleValueEvent(userListener)

                } else {

                    Log.w(TAG, "signInWithCredentials:failure", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()

                }

            }

    }

    private fun checkNewUser(currentUser: FirebaseUser, ds: DataSnapshot) {

        var isNewUser = true

        for (ds in ds.children) {

            val uid = ds.child("uid").value.toString()

            if (uid == currentUser.uid) {

                isNewUser = false

                break

            }

        }

        if (isNewUser) {

            val intent = Intent(this, SignupActivity::class.java)

            startActivity(intent)

            finish()

        } else {

            val intent = Intent(this, UserMainActivity::class.java)

            startActivity(intent)

            finish()


        }

    }


}