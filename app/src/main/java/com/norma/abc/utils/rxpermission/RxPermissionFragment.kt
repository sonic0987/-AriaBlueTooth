package com.norma.abc.utils.rxpermission

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import io.reactivex.subjects.PublishSubject
import java.util.HashMap


class RxPermissionFragment : Fragment() {

    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private val mSubjects = HashMap<String, PublishSubject<Permission>>()
    private var mLogging: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @TargetApi(Build.VERSION_CODES.M)
    internal fun requestPermissions(@NonNull permissions: Array<String>) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != PERMISSIONS_REQUEST_CODE) return

        val shouldShowRequestPermissionRationale = BooleanArray(permissions.size)

        for (i in permissions.indices) {
            shouldShowRequestPermissionRationale[i] =
                shouldShowRequestPermissionRationale(permissions[i])
        }

        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale)
    }

    internal fun onRequestPermissionsResult(
        permissions: Array<String>,
        grantResults: IntArray,
        shouldShowRequestPermissionRationale: BooleanArray
    ) {
        var i = 0
        val size = permissions.size
        while (i < size) {
            log("onRequestPermissionsResult  " + permissions[i])
            // Find the corresponding subject
            val subject = mSubjects[permissions[i]]
            if (subject == null) {
                // No subject found
                Log.e(
                    RxPermission.TAG,
                    "RxPermissions.onRequestPermissionsResult invoked but didn't find the corresponding permission request."
                )
                return
            }
            mSubjects.remove(permissions[i])
            val granted = grantResults[i] == PackageManager.PERMISSION_GRANTED
            subject!!.onNext(
                Permission(
                    permissions[i],
                    granted,
                    shouldShowRequestPermissionRationale[i]
                )
            )
            subject!!.onComplete()
            i++
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    internal fun isGranted(permission: String): Boolean {
        return activity?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    internal fun isRevoked(permission: String): Boolean? {
        return activity?.packageManager?.isPermissionRevokedByPolicy(permission, activity!!.packageName)
    }

    fun setLogging(logging: Boolean) {
        mLogging = logging
    }

    fun getSubjectByPermission(@NonNull permission: String): PublishSubject<Permission>? {
        return mSubjects[permission]
    }

    fun containsByPermission(@NonNull permission: String): Boolean {
        return mSubjects.containsKey(permission)
    }

    fun setSubjectForPermission(@NonNull permission: String, @NonNull subject: PublishSubject<Permission>): PublishSubject<Permission>? {
        return mSubjects.put(permission, subject)
    }

    internal fun log(message: String) {
        if (mLogging) {
            Log.d(RxPermission.TAG, message)
        }
    }

    companion object {

        private val PERMISSIONS_REQUEST_CODE = 42
    }

}
