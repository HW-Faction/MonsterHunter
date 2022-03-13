package monster.hunter.armor.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import monster.hunter.armor.R
import monster.hunter.armor.adapter.ArmorAdapter
import monster.hunter.armor.model.ArmorModel
import monster.hunter.armor.repository.MainRepository
import monster.hunter.armor.util.Constants.Companion.TAG
import monster.hunter.armor.util.Resource
import monster.hunter.armor.viewmodel.MainViewModel
import monster.hunter.armor.viewmodel.MainViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val armorList: MutableLiveData<ArrayList<ArmorModel>> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newsRepository = MainRepository()
        val viewModelProviderFactory = MainViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)

        viewModel.armors.observe(this, { resource ->
            Log.d(TAG, " viewModel.armors.observe")
            when (resource) {
                is Resource.Success -> {
                    armorList.value = resource.data
                    recycler_view.apply {
                        adapter = ArmorAdapter(armorList.value!!)
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        progress_circular.visibility = View.GONE
                        recycler_view.visibility = View.VISIBLE
                    }
                }
                is Resource.Error -> {
                    progress_circular.visibility = View.GONE
                    recycler_view.visibility = View.GONE
                    Log.d(TAG, "armors:: error: " + resource.message)
                    Toast.makeText(
                        this,
                        "An Error occured: " + resource.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    progress_circular.visibility = View.VISIBLE
                    recycler_view.visibility = View.GONE
                    Log.d(TAG, "armors: loading...")
                }
            }
        })

        /*  viewModel.searchArmor("Leather")
          viewModel.searchedArmors.observe(this, { resource ->
              Log.d(TAG, " viewModel.searched.observe")
              when(resource) {
                  is Resource.Success -> {
                      resource.data?.forEach {
                          Log.d(TAG, "searched: $it")
                      }
                  }
                  is Resource.Error -> {
                      Log.d(TAG, "searched:: error: " + resource.message)
                  }
                  is Resource.Loading -> {
                      Log.d(TAG, "searched: loading...")
                  }
              }
          })*/


        search_et.doOnTextChanged { text, _, _, _ ->
            searchArmors("" + text)
        }
    }

    private fun searchArmors(text: String?) {
        val arrayList = ArrayList<ArmorModel>()

        if (text?.isEmpty() == true) {
            if (armorList.value?.isNotEmpty() == true) {
                recycler_view.apply {
                    adapter = ArmorAdapter(armorList.value!!)
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    progress_circular.visibility = View.GONE
                    recycler_view.visibility = View.VISIBLE
                }
            }
        } else {
            if (armorList.value?.isNotEmpty() == true) {
                text.let {
                    armorList.value!!.forEach {
                        if (it.name.contains(text.toString(), ignoreCase = false) ||
                            it.type.toString().contains(text.toString(), ignoreCase = false)
                        ) {
                            arrayList.add(it)
                        }
                    }
                }

                if (arrayList.isEmpty()) {
                    Log.d(TAG, "searched arrayList is empty")
                    Toast.makeText(this, "No item found", Toast.LENGTH_SHORT).show()
                    arrayList.clear()
                    recycler_view.apply {
                        Log.d(TAG, "searched arrayList($text): $arrayList")
                        adapter = ArmorAdapter(arrayList)
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        progress_circular.visibility = View.GONE
                        recycler_view.visibility = View.VISIBLE
                    }

                } else {
                    recycler_view.apply {
                        Log.d(TAG, "searched arrayList($text): $arrayList")
                        adapter = ArmorAdapter(arrayList)
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        progress_circular.visibility = View.GONE
                        recycler_view.visibility = View.VISIBLE
                    }
                }
            }

        }
    }
}