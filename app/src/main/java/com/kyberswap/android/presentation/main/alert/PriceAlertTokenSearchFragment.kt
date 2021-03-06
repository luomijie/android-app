package com.kyberswap.android.presentation.main.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.widget.textChanges
import com.kyberswap.android.AppExecutors
import com.kyberswap.android.R
import com.kyberswap.android.databinding.FragmentTokenSearchBinding
import com.kyberswap.android.domain.SchedulerProvider
import com.kyberswap.android.domain.model.Alert
import com.kyberswap.android.domain.model.Token
import com.kyberswap.android.domain.model.Wallet
import com.kyberswap.android.presentation.base.BaseFragment
import com.kyberswap.android.presentation.helper.Navigator
import com.kyberswap.android.presentation.main.balance.GetBalanceState
import com.kyberswap.android.util.di.ViewModelFactory
import com.kyberswap.android.util.ext.hideKeyboard
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PriceAlertTokenSearchFragment : BaseFragment() {
    private lateinit var binding: FragmentTokenSearchBinding

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private var wallet: Wallet? = null

    private var alert: Alert? = null

    private var currentSearchString = ""

    private var tokenList = mutableListOf<Token>()


    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)
            .get(PriceAlertTokenSearchViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wallet = arguments?.getParcelable(WALLET_PARAM)
        alert = arguments?.getParcelable(ALERT_PARAM)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTokenSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.rvToken.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )

        val tokenAdapter =
            PriceAlertTokenSearchAdapter(appExecutors) { token ->

                viewModel.saveToken(wallet, token, alert)
            }

        binding.rvToken.adapter = tokenAdapter
        wallet?.address?.let { viewModel.getTokenList(it) }

        viewModel.getTokenListCallback.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { state ->
                showProgress(state == GetBalanceState.Loading)
                when (state) {
                    is GetBalanceState.Success -> {
                        tokenList.clear()
                        tokenList.addAll(state.tokens)
                        updateFilterListToken(currentSearchString, tokenAdapter)

                    }
                    is GetBalanceState.ShowError -> {
                        showError(
                            state.message ?: getString(R.string.something_wrong)
                        )
                    }
                }
            }
        })

        viewModel.compositeDisposable.add(
            binding.edtSearch.textChanges()
                .skipInitialValue()
                .debounce(
                    250,
                    TimeUnit.MILLISECONDS
                )
                .map {
                    return@map it.trim().toString().toLowerCase(Locale.getDefault())
                }.observeOn(schedulerProvider.ui())
                .subscribe { searchedText ->
                    currentSearchString = searchedText
                    updateFilterListToken(currentSearchString, tokenAdapter)
                })

        binding.imgBack.setOnClickListener {
            activity!!.onBackPressed()
        }


        viewModel.saveAlertTokenState.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { state ->
                showProgress(state == SaveAlertTokenBalanceState.Loading)
                when (state) {
                    is SaveAlertTokenBalanceState.Success -> {
                        onSelectionComplete()
                    }
                    is SaveAlertTokenBalanceState.ShowError -> {
                        showError(
                            state.message ?: getString(R.string.something_wrong)
                        )
                    }
                }
            }
        })


    }

    private fun onSelectionComplete() {
        hideKeyboard()
        activity?.onBackPressed()
    }

    private fun updateFilterListToken(
        searchedText: String?,
        alertTokenAdapter: PriceAlertTokenSearchAdapter
    ) {
        if (searchedText.isNullOrEmpty()) {
            alertTokenAdapter.submitFilterList(tokenList)
        } else {
            alertTokenAdapter.submitFilterList(
                getFilterTokenList(
                    currentSearchString,
                    tokenList
                )
            )
        }
    }


    private fun getFilterTokenList(searchedString: String, tokens: List<Token>): List<Token> {
        return tokens.filter { token ->
            token.tokenSymbol.toLowerCase(Locale.getDefault()).contains(searchedString) or
                token.tokenName.toLowerCase(Locale.getDefault()).contains(searchedString)
        }
    }

    override fun onDestroyView() {
        viewModel.compositeDisposable.clear()
        super.onDestroyView()
    }


    companion object {
        private const val WALLET_PARAM = "wallet_param"
        private const val ALERT_PARAM = "alert_param"
        fun newInstance(wallet: Wallet?, alert: Alert?) =
            PriceAlertTokenSearchFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(WALLET_PARAM, wallet)
                    putParcelable(ALERT_PARAM, alert)

                }
            }
    }
}