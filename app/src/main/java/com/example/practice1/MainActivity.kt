package com.example.practice1

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.practice1.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    // 뷰 바인딩
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!
    lateinit var navController :NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)
    }

    // 종료시
    override fun onDestroy() {
        // 바인딩 정리
        mBinding = null
        super.onDestroy()
    }
}

//권한처리는 따로 안해도 됨. 기본인 인터넷만 사용함
//    fun checkPermission() {
//        val internetPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
//        if (internetPermission == PackageManager.PERMISSION_GRANTED) {
//        }
//        else {
//            requestPermission()
//        }
//    }
//
//    fun requestPermission() {
//        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.INTERNET), 99)
//    }