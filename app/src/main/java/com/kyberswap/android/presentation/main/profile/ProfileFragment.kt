package com.kyberswap.android.presentation.main.profile


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kyberswap.android.AppExecutors
import com.kyberswap.android.R
import com.kyberswap.android.databinding.FragmentProfileBinding
import com.kyberswap.android.domain.SchedulerProvider
import com.kyberswap.android.domain.model.SocialInfo
import com.kyberswap.android.domain.model.UserInfo
import com.kyberswap.android.presentation.base.BaseFragment
import com.kyberswap.android.presentation.helper.DialogHelper
import com.kyberswap.android.presentation.helper.Navigator
import com.kyberswap.android.presentation.main.MainActivity
import com.kyberswap.android.presentation.main.MainPagerAdapter
import com.kyberswap.android.util.di.ViewModelFactory
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.models.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var dialogHelper: DialogHelper

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private var fromLimitOrder: Boolean = false

    private val handler by lazy { Handler() }

    private val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.server_client_id))
            .build()
    }

    private val callbackManager by lazy {
        CallbackManager.Factory.create()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
    }

    private val twitterAuthClient by lazy {
        TwitterAuthClient()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    result?.accessToken?.let { meRequest(it) }
        

                override fun onCancel() {
        

                override fun onError(error: FacebookException?) {
                    Timber.e(error?.localizedMessage)
                    error?.printStackTrace()
        
    )

        binding.tvSignUp.setOnClickListener {
            navigator.navigateToSignUpScreen(
                (activity as MainActivity).getCurrentFragment()
            )

        binding.btnLogin.setOnClickListener {
            viewModel.login(edtEmail.text.toString(), edtPassword.text.toString())


        viewModel.loginCallback.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { state ->
                showProgress(state == LoginState.Loading)
                when (state) {
                    is LoginState.Success -> {
                        if (state.login.success) {
                            if (state.login.confirmSignUpRequired) {
                                navigator.navigateToSignUpConfirmScreen(
                                    currentFragment,
                                    state.socialInfo
                                )
                     else {
                                navigateToProfileDetail(state.login.userInfo)
                                if (fromLimitOrder) {
                                    moveToLimitOrder()
                        
                    
                 else {
                            showAlert(state.login.message)
                
            
                    is LoginState.ShowError -> {
                        showAlert(state.message ?: getString(R.string.something_wrong))
                        Toast.makeText(
                            activity,
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()
            
        
    
)


        viewModel.resetPasswordCallback.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { state ->
                showProgress(state == ResetPasswordState.Loading)
                when (state) {
                    is ResetPasswordState.Success -> {
                        showAlert(state.status.message)
            
                    is ResetPasswordState.ShowError -> {
                        showAlert(state.message ?: getString(R.string.something_wrong))
            
        
    
)

        binding.imgBack.setOnClickListener {
            activity?.onBackPressed()


        binding.imgFacebook.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))


        binding.imgGooglePlus.setOnClickListener {
            val googleSignInClient = GoogleSignIn.getClient(this.activity!!, gso)
            val account = GoogleSignIn.getLastSignedInAccount(this.activity)
            if (account == null) {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
     else {
                googleSignInClient.signOut().addOnCompleteListener {
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
        
    


        binding.imgTwitter.setOnClickListener {

            val twitterSession = TwitterCore.getInstance().sessionManager.activeSession
            if (twitterSession != null) {
                TwitterCore.getInstance().sessionManager.clearActiveSession()
    
            twitterAuthClient.authorize(activity, object : Callback<TwitterSession>() {
                override fun success(result: com.twitter.sdk.android.core.Result<TwitterSession>?) {
                    getTwitterUserProfileWthTwitterCoreApi(result?.data)
        

                override fun failure(exception: TwitterException?) {
                    exception?.printStackTrace()
                    Toast.makeText(
                        context,
                        exception?.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
        

    )


        binding.tvForgotPassword.setOnClickListener {
            dialogHelper.showResetPassword {
                viewModel.resetPassword(it)
    


    }

    private fun moveToLimitOrder() {
        if (activity is MainActivity) {
            handler.post {
                activity!!.bottomNavigation.currentItem = MainPagerAdapter.LIMIT_ORDER
    

    }

    private fun navigateToProfileDetail(userInfo: UserInfo?) {
        navigator.navigateToProfileDetail(
            currentFragment,
            userInfo
        )
    }

    private fun meRequest(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            accessToken
        ) { me, response ->
            if (response?.error != null) {
                showAlert(response.error.errorMessage)
     else {
                val email = me?.optString("email")
                val name = me?.optString("name")
                val id = me?.optString("id")
                val profileUrl = "https://graph.facebook.com/$id/picture?type=large"
                val socialInfo = SocialInfo(
                    LoginType.FACEBOOK,
                    name,
                    accessToken.token,
                    profileUrl,
                    email
                )
                viewModel.login(socialInfo)
    


        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id, name, email, gender, birthday, picture.type(large)"
        )
        request.parameters = parameters
        request.executeAsync()
    }


    private fun getTwitterUserProfileWthTwitterCoreApi(
        session: TwitterSession?
    ) {
        TwitterCore.getInstance().getApiClient(session).accountService
            .verifyCredentials(true, true, false)
            .enqueue(object : Callback<User>() {
                override fun success(result: com.twitter.sdk.android.core.Result<User>?) {
                    val name = result?.data?.name

                    val profileImageUrl = result?.data?.profileImageUrl?.replace("_normal", "")
                    val socialInfo = SocialInfo(
                        LoginType.TWITTER,
                        name,
                        session?.authToken?.token,
                        profileImageUrl,
                        null,
                        false,
                        session?.authToken?.token,
                        session?.authToken?.secret
                    )


                    viewModel.login(socialInfo)
        

                override fun failure(exception: TwitterException) {
                    Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_SHORT).show()
        
    )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        twitterAuthClient.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            account?.let {
                val socialInfo = SocialInfo(
                    LoginType.GOOGLE,
                    account.displayName,
                    account.idToken,
                    account.photoUrl.toString(),
                    account.email
                )
                viewModel.login(socialInfo)
    

 catch (e: ApiException) {
            e.printStackTrace()
            Timber.e(e.localizedMessage)

    }

    fun fromLimitOrder(fromLimitOrder: Boolean) {
        this.fromLimitOrder = fromLimitOrder
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    companion object {
        private const val RC_SIGN_IN = 1000
        fun newInstance() =
            ProfileFragment()
    }


}
