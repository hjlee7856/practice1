package com.example.practice1.fragment

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.practice1.databinding.FragmentQuizeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class QuizeFragment : Fragment() {
    //뷰 바인딩
    private var mBinding: FragmentQuizeBinding? = null
    private val binding get() = mBinding!!
    lateinit var navController: NavController
    var option = -1
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var quizeanswer = "정답"
    var usercheck = "오답"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentQuizeBinding.inflate(inflater, container, false)
        option = arguments?.getInt("index")?:-1
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        setQuize(option)

        binding.btnSubmit.setOnClickListener{
            submitQuize(option)
        }

        binding.answer.setOnKeyListener{v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                submitQuize(option)
            }
            false
        }
    }

    private fun setQuize(option: Int) {
        // 문제 데이터 불러오기
        db.collection("Quize").document("Q${option}")
            .get()
            .addOnSuccessListener { result ->
                val resourceId = binding.root.context.resources.getIdentifier(result["quizeimage"] as String, "drawable", binding.root.context.packageName)
                binding.qimage.setImageResource(resourceId)
                binding.quize.setText(result["quizetext"] as String)
                binding.hint.setText(result["quizehint"] as String)
                quizeanswer = result["quizeanswer"] as String
            }

        // 유저 데이터 불러오기
        db.collection("User").document(auth.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                if(option > 0) {
                    if (result["answer_${option - 1}"] != null) {
                        usercheck = result["answer_${option - 1}"] as String
                    }
                    else {
                        Toast.makeText(activity, "이전 문제를 먼저 풀어주세요.", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                }
            }
    }

    private fun submitQuize(option: Int) {
        // 정답 처리하기
        if(quizeanswer == binding.answer.text.toString()) {
            val data = hashMapOf(
                "answer_${option}" to "success",
            )
            // 정답 저장하기
            db.collection("User").document(auth.uid.toString())
                .set(data, SetOptions.merge())
                .addOnSuccessListener { result ->
                    Toast.makeText(activity, "정답입니다.", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
        }
        else {
            Toast.makeText(activity, "오답입니다", Toast.LENGTH_SHORT).show()
        }
    }
}