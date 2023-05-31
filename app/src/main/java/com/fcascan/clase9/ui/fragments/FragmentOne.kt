package com.fcascan.clase9.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fcascan.clase9.R
import com.fcascan.clase9.ui.viewmodel.FragmentOneViewModel
import com.google.android.material.snackbar.Snackbar

enum class LoadingState {
    LOADING,
    SUCCESS,
    FAILURE
}

const val BIG_IMG_SIZE_MULTIPLIER = 0.19f
const val SMALL_IMG_SIZE_MULTIPLIER = 1.0f

class FragmentOne : Fragment() {
    lateinit var v: View
    lateinit var imageView: ImageView
    lateinit var txtSearch: EditText
    lateinit var btnSearch: Button
    lateinit var txtId: TextView
    lateinit var txtNameEng: EditText
    lateinit var txtNameJap: EditText
    lateinit var txtNameChi: EditText
    lateinit var txtNameFra: EditText
    lateinit var txtTypes: EditText
    lateinit var txtStatsSpAttack: EditText
    lateinit var txtStatsSpDefense: EditText
    lateinit var txtStatsAttack: EditText
    lateinit var txtStatsDefense: EditText
    lateinit var txtStatsHP: EditText
    lateinit var txtStatsSpeed: EditText
    lateinit var progressBar: ProgressBar

    private lateinit var viewModel: FragmentOneViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_one, container, false)
        imageView = v.findViewById(R.id.imageView)
        txtSearch = v.findViewById(R.id.txtSearch)
        btnSearch = v.findViewById(R.id.btnSearch)
        txtId = v.findViewById(R.id.txtId)
        txtNameEng = v.findViewById(R.id.txtNameEng)
        txtNameJap = v.findViewById(R.id.txtNameJap)
        txtNameChi = v.findViewById(R.id.txtNameChi)
        txtNameFra = v.findViewById(R.id.txtNameFra)
        txtTypes = v.findViewById(R.id.txtTypes)
        txtStatsSpAttack = v.findViewById(R.id.txtStatsSpAttack)
        txtStatsSpDefense = v.findViewById(R.id.txtStatsSpDefense)
        txtStatsAttack = v.findViewById(R.id.txtStatsAttack)
        txtStatsDefense = v.findViewById(R.id.txtStatsDefense)
        txtStatsHP = v.findViewById(R.id.txtStatsHP)
        txtStatsSpeed = v.findViewById(R.id.txtStatsSpeed)
        progressBar = v.findViewById(R.id.progressBar)
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[FragmentOneViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()

        clearFields()

        btnSearch.setOnClickListener {
            viewModel.getIdFromFirebase(txtSearch.text.toString().toInt())
        }

        txtSearch.setOnKeyListener { _, _, event ->
            if(event.action == android.view.KeyEvent.ACTION_DOWN) {
                if (txtSearch.text.toString().isNotEmpty()) {
                    viewModel.getIdFromFirebase(txtSearch.text.toString().toInt())
                }
                false
            } else {
                false
            }
        }

        viewModel.pokemon.observe(viewLifecycleOwner) {
            Log.d("FragmentOne", "Pokemon: $it")
            if (it != null) {
                refreshImage(it.imageURL.toString())
                txtId.text = "#${it.id.toString()}"
                txtNameEng.setText(it.name?.get("english"))
                txtNameJap.setText(it.name?.get("japanese"))
                txtNameChi.setText(it.name?.get("chinese"))
                txtNameFra.setText(it.name?.get("french"))
                txtTypes.setText(it.type.toString().replace("[", "").replace("]", ""))
                txtStatsSpAttack.setText(it.base?.get("Sp. Attack").toString())
                txtStatsSpDefense.setText(it.base?.get("Sp. Defense").toString())
                txtStatsAttack.setText(it.base?.get("Attack").toString())
                txtStatsDefense.setText(it.base?.get("Defense").toString())
                txtStatsHP.setText(it.base?.get("HP").toString())
                txtStatsSpeed.setText(it.base?.get("Speed").toString())
            }
        }

        viewModel.screenState.observe(this) { state ->
            when(state) {
                LoadingState.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                LoadingState.SUCCESS -> {
                    progressBar.visibility = View.GONE
                }
                LoadingState.FAILURE -> {
                    Snackbar.make(v, "FAILURE", Snackbar.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                }
                else -> {
                    //Show error, hide loading
                    Snackbar.make(v, "ERROR", Snackbar.LENGTH_LONG).show()
                    clearFields()
                }
            }
        }
    }

    private fun clearFields() {
        txtId.text = "#"
        refreshImage(" ")
        txtNameEng.setText(" ")
        txtNameJap.setText(" ")
        txtNameChi.setText(" ")
        txtNameFra.setText(" ")
        txtTypes.setText(" ")
        txtStatsSpAttack.setText(" ")
        txtStatsSpDefense.setText(" ")
        txtStatsAttack.setText(" ")
        txtStatsDefense.setText(" ")
        txtStatsHP.setText(" ")
        txtStatsSpeed.setText(" ")
        progressBar.visibility = View.GONE
    }

    private fun refreshImage(imgUrl: String) {
        context?.let {
            Glide.with(it)
                .load(imgUrl)
                .error(
                    Glide.with(it)
                        .load(R.drawable.not_found)
                        .centerCrop()
                        .circleCrop()
                        .sizeMultiplier(BIG_IMG_SIZE_MULTIPLIER)
                )
                .centerCrop()
//                .circleCrop()
                .sizeMultiplier(SMALL_IMG_SIZE_MULTIPLIER)
                .into(imageView)
        }
    }
}