package com.kyberswap.android.presentation.main.balance.address

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.kyberswap.android.AppExecutors
import com.kyberswap.android.R
import com.kyberswap.android.databinding.FragmentBalanceAddressBinding
import com.kyberswap.android.presentation.base.BaseFragment
import com.kyberswap.android.presentation.helper.Navigator
import com.kyberswap.android.presentation.splash.GetWalletState
import com.kyberswap.android.util.di.ViewModelFactory
import javax.inject.Inject


class BalanceAddressFragment : BaseFragment() {

    private lateinit var binding: FragmentBalanceAddressBinding

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(BalanceAddressViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBalanceAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getSelectedWallet()
        viewModel.getSelectedWalletCallback.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { state ->
                when (state) {
                    is GetWalletState.Success -> {
                        if (binding.wallet?.address != state.wallet.address) {
                            binding.wallet = state.wallet
                            binding.imgAddress.setImageBitmap(generateBarcode())
                        }
                    }
                    is GetWalletState.ShowError -> {

                    }
                }
            }
        })

        binding.tvCopy.setOnClickListener {
            val clipboard =
                context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("Copy", binding.wallet?.address)
            clipboard!!.primaryClip = clip
            showAlert(getString(R.string.address_copy))
        }
        binding.tvShare.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, binding.wallet?.address)
                type =
                    MIME_TYPE_TEXT
            }
            startActivity(sendIntent)
        }

        binding.imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun generateBarcode(): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(
                binding.wallet?.address, BarcodeFormat.QR_CODE, resources.getDimensionPixelSize(
                    R.dimen.bar_code_dimen
                ), resources.getDimensionPixelSize(
                    R.dimen.bar_code_dimen
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val MIME_TYPE_TEXT = "text/plain"
        fun newInstance() =
            BalanceAddressFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
