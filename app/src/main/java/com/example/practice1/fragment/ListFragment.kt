package com.example.practice1.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.practice1.R
import com.example.practice1.databinding.FragmentListBinding
import com.example.practice1.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.find


class ListFragment : Fragment() {
    //뷰 바인딩
    private var mBinding: FragmentListBinding? = null
    private val binding get() = mBinding!!
    lateinit var navController: NavController
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // 테스트용
    var titlelist = arrayListOf<ListViewItem>(
        ListViewItem("home", "test", "test"),
        ListViewItem("home", "test2", "test2"),
        ListViewItem("home", "test3", "test3")
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentListBinding.inflate(inflater, container, false)

        listviewsetting()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    // 리스트뷰 세팅
    fun listviewsetting(){
        val titleAdaper = ListViewAdapter(binding.root.context, titlelist)
        var uaind = 0
        var qind = 0

        // 유저 데이터 불러오기
        db.collection("User").document(auth.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                while (result["answer_${uaind}"] != null){
                        uaind++
                    }

                // 문제 데이터 불러오기
                db.collection("Quize")
                    .get()
                    .addOnSuccessListener { result ->
                        titlelist.clear()
                        for(document in result){
                            if(qind++ < uaind) {
                                val item = ListViewItem(
                                    document["quizeimage"] as String,
                                    document["quizetext"] as String + " (완료)",
                                    document["quizehint"] as String
                                )
                                titlelist.add(item)
                            }
                            else {
                                val item = ListViewItem(
                                    document["quizeimage"] as String,
                                    document["quizetext"] as String,
                                    document["quizehint"] as String
                                )
                                titlelist.add(item)
                            }
                        }
                        qind = 0
                        titleAdaper.notifyDataSetChanged()
                        binding.listView.adapter = titleAdaper
                    }
            }

        // 리스트 선택시
        binding.listView.setOnItemClickListener{parent, view, position, id ->
            navigateWithIndex(position)
        }
    }

    // 리스트뷰 어댑터
    class ListViewAdapter (val context: Context, val titleList: ArrayList<ListViewItem>) : BaseAdapter() {
        override fun getCount(): Int {
            return titleList.size
        }

        override fun getItem(position: Int): Any {
           return titleList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = LayoutInflater.from(context).inflate(R.layout.custom_list_item, null)

            val image_title = view.findViewById<ImageView>(R.id.image_title)
            val text_title = view.findViewById<TextView>(R.id.text_title)
            val text_sub_title = view.findViewById<TextView>(R.id.text_sub_title)

            val titlelist = titleList[position]
            val resourceId = context.resources.getIdentifier(titlelist.icon, "drawable", context.packageName)
            image_title.setImageResource(resourceId)
            text_title.text = titlelist.title
            text_sub_title.text = titlelist.subTitle

            return view
        }
    }

    fun navigateWithIndex(index : Int){
        val bundle = bundleOf("index" to index)
        navController.navigate(R.id.action_listFragment_to_quizeFragment, bundle)
    }
}