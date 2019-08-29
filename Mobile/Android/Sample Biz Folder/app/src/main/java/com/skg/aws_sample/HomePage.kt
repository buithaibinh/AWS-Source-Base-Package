package com.skg.aws_sample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.amplify.generated.graphql.AllUsersQuery
import com.amazonaws.amplify.generated.graphql.DeleteUserMutation
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.SignOutOptions
import com.amazonaws.mobile.client.results.UserCodeDeliveryDetails
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.skg.aws_sample.adapter.UserListAdapter
import com.skg.aws_sample.amazon_aws.OnUpdateUserSubscription1
import com.skg.aws_sample.app.App
import com.skg.aws_sample.net_work.NetworkClient
import com.skg.aws_sample.net_work.onSuccess
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File


class HomePage : AppCompatActivity(), View.OnClickListener {
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    //Recycler View
    private lateinit var adapterAllUser: RecyclerView.Adapter<*>
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var mAdapter: UserListAdapter
    //subscription
    private var subscriptionListUser: AppSyncSubscriptionCall<OnUpdateUserSubscription1.Data>? = null
    //result on pick photo
    private val RESULT_LOAD_IMAGE = 1
    private val REQUEST_PERMISSION = 1
    //data listUser
    private lateinit var mData: ArrayList<AllUsersQuery.Item>
    //current position when click on item in the list user
    private var currentIndex: Int = -1
    //data of item  when click on item in the list user
    var dataItemClicked: AllUsersQuery.Item? = null
    private var buttonHardBackClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        tvToolBar.text = getString(R.string.home_page)
        progressBar.visibility = View.VISIBLE
        btnReport.visibility = View.VISIBLE

        checkPermission()

        setUpAdapter()

        getListUser()

        setCurrentUser()

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sendMessage.setOnClickListener(this)
        sendMessageAll.setOnClickListener(this)
        removeUser.setOnClickListener(this)
        imgAvatar.setOnClickListener(this)
        btnReport.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                print("onDoing")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                print("onChange")
            }
        })
        subscriptionListUser()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun onTapLogOut(v: View) {
        AWSMobileClient.getInstance()
            .signOut(SignOutOptions.builder().invalidateTokens(true).build(), object : Callback<Void> {
                override fun onResult(result: Void?) {
                    this@HomePage.runOnUiThread {
                        val intent = Intent(this@HomePage, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(Intent(intent))
                        this@HomePage.finish()
                    }
                }

                override fun onError(e: Exception?) {
                    this@HomePage.runOnUiThread {
                        Toast.makeText(this@HomePage, "Error + ${e?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }


    private fun setUpAdapter() {
        viewManager = LinearLayoutManager(this)
        mAdapter = UserListAdapter()
        mAdapter.onClickItem = this
        adapterAllUser = mAdapter
        recyclerView = findViewById<RecyclerView>(R.id.listUser).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = adapterAllUser
        }
    }

    private fun setCurrentUser() {
        AWSMobileClient.getInstance().getUserAttributes(object : Callback<Map<String, String>> {
            override fun onResult(result: Map<String, String>?) {

                this@HomePage.runOnUiThread {
                    if (result != null && result["picture"] != null) {
                        Glide.with(this@HomePage)
                            .load(result["picture"])
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .into(imgAvatar)

                    } else {
                        Glide.with(this@HomePage)
                            .load(R.drawable.amazon)
                            .circleCrop()
                            .into(imgAvatar)
                    }
                    tvName.text = result!!["email"]
                }
            }

            override fun onError(e: java.lang.Exception?) {
                print(e?.message)
            }
        })
        try {
            val role = AWSMobileClient.getInstance().tokens.idToken.getClaim("cognito:groups")
            if (role != null && role.contains("Admin")) {
                removeUser.visibility = View.VISIBLE
            } else {
                removeUser.visibility = View.GONE
            }
        } catch (e: java.lang.Exception) {
            print(e.message)
            removeUser.visibility = View.GONE
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private fun getListUser() {
        App.instance.awsAppSyncClient.query(AllUsersQuery.builder().build())
            ?.responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
            ?.enqueue(getListUser)
    }

    private val getListUser = object : GraphQLCall.Callback<AllUsersQuery.Data>() {
        override fun onFailure(e: ApolloException) {
            print(e.message)
        }

        override fun onResponse(response: Response<AllUsersQuery.Data>) {
            this@HomePage.runOnUiThread {
                print(response)
                mData = arrayListOf()
                mData.addAll(response.data()?.allUsers()?.items()!!)
                if (AWSMobileClient.getInstance().username.contains("Google") || AWSMobileClient.getInstance().username.contains(
                        "Facebook"
                    )
                ) {
                    mData.remove(mData.find { it.Username() == AWSMobileClient.getInstance().username })
                } else {
                    mData.remove(mData.find { it.Username() == AWSMobileClient.getInstance().tokens.idToken.getClaim("sub") })
                }
                mAdapter.setData(mData)
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.itemUserList -> {
                val itemPosition = recyclerView.getChildLayoutPosition(view)
                print(itemPosition)
                if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    if (currentIndex == itemPosition) {
                        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    } else {
                    }
                }
                currentIndex = itemPosition
                dataItemClicked = mData[itemPosition]
            }

            R.id.sendMessage -> {
                NetworkClient.instance.service.sendHello(
                    mapOf(
                        "message" to "Hello",
                        "userInfo" to mapOf(
                            "id" to dataItemClicked?.Attributes()?.findLast { it.Name() == "sub" }?.Value(),
                            "email" to dataItemClicked?.Attributes()?.findLast { it.Name() == "email" }?.Value()
                        )
                    )
                ).onSuccess {
                    print("success")
                }
            }

            R.id.sendMessageAll -> {
                NetworkClient.instance.service.sendHelloAll(
                    mapOf(
                        "message" to "Hello"
                    )
                ).onSuccess {
                    print("success")
                }
            }

            R.id.removeUser -> {
                if (dataItemClicked != null) {
                    deleteUser(dataItemClicked!!.Username())
                }
            }

            R.id.imgAvatar -> {
                choosePhoto()
            }

            R.id.btnReport -> {
                App.instance.pinpointManager.sessionClient.startSession()
                logEvent()
            }
        }

    }

    private fun logEvent() {
        val event = App.instance.pinpointManager.analyticsClient.createEvent("EventName")
            .withAttribute("Report", "ByNamAnh")
            .withMetric("Count", Math.random())
        App.instance.pinpointManager.analyticsClient.recordEvent(event)
        App.instance.pinpointManager.analyticsClient.submitEvents()
        btnReport.visibility = View.GONE
    }

    private fun deleteUser(userName: String) {
        val deleteUser = DeleteUserMutation.builder()
            .userName(userName)
            .build()
        App.instance.awsAppSyncClient.mutate(deleteUser)?.enqueue(deleteUserCallback)
    }

    private val deleteUserCallback = object : GraphQLCall.Callback<DeleteUserMutation.Data>() {
        override fun onFailure(e: ApolloException) {
            print(e.message)
        }

        override fun onResponse(response: Response<DeleteUserMutation.Data>) {
            print(response)

        }

    }

    private fun subscriptionListUser() {
        val subscription = OnUpdateUserSubscription1.builder().build()
        subscriptionListUser = App.instance.awsAppSyncClient.subscribe(subscription)
        subscriptionListUser?.execute(subscriptionCallBack)
    }

    private val subscriptionCallBack = object : AppSyncSubscriptionCall.Callback<OnUpdateUserSubscription1.Data> {
        override fun onFailure(e: ApolloException) {
            print(e.message)
        }

        override fun onResponse(response: Response<OnUpdateUserSubscription1.Data>) {
            print(response)
            this@HomePage.runOnUiThread {
                if (response.data()?.onUpdateUser()?.UserCreateDate() != null) {
                    val newData: AllUsersQuery.Item = AllUsersQuery.Item(
                        response.data()?.onUpdateUser()?.__typename()!!,
                        response.data()?.onUpdateUser()?.Username()!!,
                        response.data()?.onUpdateUser()?.Attributes()?.map {
                            AllUsersQuery.Attribute(
                                it.__typename(),
                                it.Name(),
                                it.Value()
                            )
                        },
                        response.data()?.onUpdateUser()?.UserCreateDate(),
                        response.data()?.onUpdateUser()?.UserStatus(),
                        response.data()?.onUpdateUser()?.Enabled(),
                        response.data()?.onUpdateUser()?.UserStatus(),
                        response.data()?.onUpdateUser()?.groups()?.map {
                            AllUsersQuery.Group(
                                it.__typename(),
                                it.GroupName(),
                                it.UserPoolId(),
                                it.Description(),
                                it.Precedence(),
                                it.CreationDate(),
                                it.LastModifiedDate()
                            )
                        }
                    )
                    mData.add(newData)
                } else {
                    val dataDelete = mData.filter {
                        it.Username() == response.data()?.onUpdateUser()?.Username()
                    }
                    if (dataDelete.isNotEmpty()) {
                        mData.remove(dataDelete[0])
                    }
                }
                mAdapter.setData(mData)
            }

        }

        override fun onCompleted() {
            print("OnCompleted")
        }

    }

    private fun uploadAvatar(fileUri: String) {
        val transferUtility = TransferUtility.builder()
            .context(applicationContext)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(
                AmazonS3Client(
                    AWSMobileClient.getInstance().credentials,
                    Region.getRegion(Regions.AP_SOUTHEAST_2)
                )
            )
            .defaultBucket("skg-dev-s3bucket-mbz2y336iyll")
            .build()

        val pathImage = "protected/${AWSMobileClient.getInstance().identityId}/filename"

        val uploadObserver =
            transferUtility.upload(
                pathImage,
                File(fileUri), CannedAccessControlList.PublicRead
            )

        uploadObserver.setTransferListener(object : TransferListener {
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                print("doing")
            }

            override fun onStateChanged(id: Int, state: TransferState?) {
                if (TransferState.COMPLETED == state) {
                    updateAttribute(pathImage)
                } else if (TransferState.FAILED == state) {
                    //hide progressbar when update fail
                    progressBar.visibility = View.GONE
                }
            }

            override fun onError(id: Int, ex: java.lang.Exception?) {
                print("Error")
            }
        })

    }

    fun updateAttribute(pathImage: String) {
        AWSMobileClient.getInstance()
            .updateUserAttributes(mapOf("picture" to "https://skg-dev-s3bucket-mbz2y336iyll.s3-ap-southeast-2.amazonaws.com/$pathImage"),
                object : Callback<List<UserCodeDeliveryDetails>> {
                    override fun onResult(result: List<UserCodeDeliveryDetails>?) {
                        this@HomePage.runOnUiThread {
                            //reset avatar
                            Glide.with(this@HomePage)
                                .load("https://skg-dev-s3bucket-mbz2y336iyll.s3-ap-southeast-2.amazonaws.com/$pathImage")
                                .circleCrop()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(imgAvatar)
                            //hide progressbar when update success and reload avatar
                            progressBar.visibility = View.GONE
                        }
                    }

                    override fun onError(e: java.lang.Exception?) {
                        print(e)
                    }
                })
    }

    private fun choosePhoto() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectImage = data.data
            val filePathColumn: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectImage!!, filePathColumn, null, null, null)
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
            val picturePath = cursor?.getString(columnIndex!!)
            cursor?.close()
            uploadAvatar(picturePath!!)
            //show progressbar when start update
            progressBar.visibility = View.VISIBLE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return if (buttonHardBackClick) {
                this.finishAffinity()
                true
            } else {
                buttonHardBackClick = true
                Toast.makeText(this,"Please click again to exit app",Toast.LENGTH_LONG).show()
                false
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (subscriptionListUser != null) {
            subscriptionListUser!!.cancel()
        }
        App.instance.pinpointManager.sessionClient.stopSession()
    }
}
