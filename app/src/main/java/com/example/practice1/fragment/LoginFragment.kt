package com.example.practice1.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.practice1.R
import com.example.practice1.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.content.Intent
import android.util.Log

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import com.example.practice1.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


class LoginFragment : Fragment(), View.OnClickListener{
    //뷰 바인딩
    private var mBinding: FragmentLoginBinding? = null
    private val binding get() = mBinding!!
    lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private var googleSigninClient: GoogleSignInClient? = null
    private val TAG: String = "GOOGLE_LOGIN"



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.btnLogin.setOnClickListener(this)
        binding.btnTosignup.setOnClickListener(this)
        binding.btnGoogle.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.DEFAULT_SIGN_IN))
            .requestEmail()
            .build()
        auth = FirebaseAuth.getInstance()
        googleSigninClient = GoogleSignIn.getClient(binding.root.context, gso)
    }

    // 로그아웃하지 않을 시 자동 로그인
    override fun onStart() {
        super.onStart()
//        moveListFragment(auth?.currentUser)
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    // 클릭이벤트 처리
    override fun onClick(v: View?) {
        when(v?.id){
            // 로그인 버튼
            R.id.btn_login -> {
                signin(binding.SigninEmail.text.toString(), binding.SigninPassword.text.toString())
            }
            // 회원가입 버튼
            R.id.btn_tosignup -> {
                navController.navigate(R.id.action_loginFragment_to_signupFragment)
            }
            // 구글로그인 버튼
            R.id.btn_google -> {
                val signInIntent = googleSigninClient?.signInIntent
                resultListener.launch(signInIntent)
            }
        }
    }

    // 로그인
    fun signin(email: String, password: String){
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener{
                // 성공
                if (it.isSuccessful) {
                    Toast.makeText(activity, "로그인에 성공 하였습니다.", Toast.LENGTH_SHORT).show()
                    moveListFragment(auth?.currentUser)
                }
                // 실패
                else {
                    Toast.makeText(activity, "로그인에 실패 하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 목록으로 이동
    fun moveListFragment(firebaseUser: FirebaseUser?) {
        val bundle = bundleOf("user" to firebaseUser)
        navController.navigate(R.id.action_loginFragment_to_listFragment, bundle)
    }

    // 구글 로그인
    val resultListener = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account)
            moveListFragment(auth?.currentUser)
            Toast.makeText(activity, "로그인에 성공 하였습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
            // ...
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        activity?.let {
            auth!!.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }
                }
        }
    }
}