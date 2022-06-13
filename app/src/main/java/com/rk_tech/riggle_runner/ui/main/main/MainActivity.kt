package com.rk_tech.riggle_runner.ui.main.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.databinding.ActivityMainBinding
import com.rk_tech.riggle_runner.ui.base.BaseActivity
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.main.completed.CompletedFragment
import com.rk_tech.riggle_runner.ui.main.neworder.NewOrderFragment
import com.rk_tech.riggle_runner.ui.main.pending.PendingOrdersFragment
import com.rk_tech.riggle_runner.ui.main.settings.SettingsFragment
import com.rk_tech.riggle_runner.utils.BackStackManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val mainViewModel: MainViewModel by viewModels()
    /*private lateinit var adapter: MainAdapter*/

    companion object {
        fun newIntent(activity: Activity): Intent {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black_theme)
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_main
    }

    override fun getViewModel(): BaseViewModel {
        return mainViewModel
    }

    override fun onCreateView() {
        binding.type = 1
        mainViewModel.onClick.observe(this, Observer {
            when (it?.id) {
                R.id.llOne -> {
                    binding.type = 1
                    changeFragment(PendingOrdersFragment.TAG)
                }
                R.id.llTwo -> {
                    binding.type = 2
                    changeFragment(NewOrderFragment.TAG)
                }
                R.id.llThree -> {
                    binding.type = 3
                    changeFragment(SettingsFragment.TAG)
                }
                R.id.llTFour -> {
                    binding.type = 4
                    changeFragment(CompletedFragment.TAG)
                }
            }
        })
        /*setupUI()
        setupObserver()*/
        BackStackManager.getInstance(this).clear()
        changeFragment(PendingOrdersFragment.TAG)
    }

    private fun changeFragment(tag: String?) {
        tag?.let {
            when (it) {
                PendingOrdersFragment.TAG -> {
                    BackStackManager.getInstance(this)
                        .pushFragments(
                            R.id.flContainer,
                            it,
                            PendingOrdersFragment.newInstance(),
                            true
                        )
                }
                NewOrderFragment.TAG -> {
                    BackStackManager.getInstance(this)
                        .pushFragments(R.id.flContainer, it, NewOrderFragment.newInstance(), true)
                }
                SettingsFragment.TAG -> {
                    BackStackManager.getInstance(this)
                        .pushFragments(R.id.flContainer, it, SettingsFragment.newInstance(), true)
                }
                CompletedFragment.TAG -> {
                    BackStackManager.getInstance(this)
                        .pushFragments(R.id.flContainer, it, CompletedFragment.newInstance(), true)
                }
            }
        }
    }

    open fun addSubFragment(tag: String, fragment: Fragment) {
        BackStackManager.getInstance(this)
            .pushSubFragments(R.id.flContainer, tag, fragment, true)
    }

    override fun onBackPressed() {
        if (isPopupBack()) {
            super.onBackPressed()
        }
    }

    private fun isPopupBack(): Boolean {
        return BackStackManager.getInstance(this).removeFragment(R.id.flContainer, true)
    }

    /*private fun setupUI() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter(arrayListOf())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    private fun setupObserver() {
        mainViewModel.users.observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { users -> renderList(users) }
                    recyclerView.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun renderList(users: List<User>) {
        adapter.addData(users)
        adapter.notifyDataSetChanged()
    }*/

}
