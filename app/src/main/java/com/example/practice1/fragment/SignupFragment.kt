package com.example.practice1.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.practice1.R
import com.example.practice1.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment(), View.OnClickListener{
    //뷰 바인딩
    private var mBinding: FragmentSignupBinding? = null
    private val binding get() = mBinding!!
    lateinit var navController: NavController
    private var firebaseauth : FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentSignupBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        firebaseauth = Firebase.auth

        binding.btnSignup.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
    }

    // 클릭시 처리
    override fun onClick(v: View?) {
        when(v?.id){
            // 회원가입 버튼
            R.id.btn_signup -> {
                createAccount(binding.signupEmail.text.toString(), binding.signupPassward.toString())
            }
            // 뒤로가기 버튼
            R.id.btn_back -> {
                navController.popBackStack()
            }
        }
    }

    // 계정 생성
    private fun createAccount(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseauth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
                // 성공 시
                if (it.isSuccessful) {
                    Toast.makeText(activity, "계정 생성 완료.", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                // 실패 시
                else {
                    Toast.makeText(activity, "계정 생성 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}