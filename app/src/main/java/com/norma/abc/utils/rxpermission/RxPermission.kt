package com.norma.abc.utils.rxpermission

import android.annotation.TargetApi
import android.os.Build
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.functions.Function
import io.reactivex.subjects.PublishSubject
import java.util.ArrayList
import kotlin.Boolean as Boolean1

class RxPermission(@NonNull fm: FragmentManager) {

    internal var mRxPermissionsFragment: RxPermissionFragment

    internal val isMarshmallow: Boolean1
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    init {
        mRxPermissionsFragment = getRxPermissionsFragment(fm)
    }

    private fun getRxPermissionsFragment(fm: FragmentManager): RxPermissionFragment {
        var rxPermissionsFragment: RxPermissionFragment? = findRxPermissionsFragment(fm)

        if (rxPermissionsFragment==null) {
            rxPermissionsFragment = RxPermissionFragment()
            fm.beginTransaction()
                .add(rxPermissionsFragment, TAG)
                .commitAllowingStateLoss()
            fm.executePendingTransactions()
        }

        return rxPermissionsFragment
    }

    private fun findRxPermissionsFragment(fm: FragmentManager): RxPermissionFragment? {
        return fm.findFragmentByTag(TAG) as? RxPermissionFragment
    }

    fun setLogging(logging: Boolean1) {
        mRxPermissionsFragment.setLogging(logging)
    }

    /**
     * Map emitted items from the source observable into `true` if permissions in parameters
     * are granted, or `false` if not.
     *
     *
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    fun <T> ensure(vararg permissions: String): ObservableTransformer<T, kotlin.Boolean> {
        return ObservableTransformer { o ->
            request(o, *permissions)
                // Transform Observable<Permission> to Observable<Boolean>
                .buffer(permissions.size)
                .flatMap(Function<List<Permission>, ObservableSource<kotlin.Boolean>> { permissions ->
                    if (permissions.isEmpty()) {
                        // Occurs during orientation change, when the subject receives onComplete.
                        // In that case we don't want to propagate that empty list to the
                        // subscriber, only the onComplete.
                        return@Function Observable.empty()
                    }
                    // Return true if all permissions are granted.
                    for (p in permissions) {
                        if (!p.granted) {
                            return@Function Observable.just(false)
                        }
                    }
                    Observable.just(true)
                })
        }
    }

    /**
     * Map emitted items from the source observable into [Permission] objects for each
     * permission in parameters.
     *
     *
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    fun <T> ensureEach(vararg permissions: String): ObservableTransformer<T, Permission> {
        return ObservableTransformer { o -> request(o, *permissions) }
    }

    /**
     * Map emitted items from the source observable into one combined [Permission] object. Only if all permissions are granted,
     * permission also will be granted. If any permission has `shouldShowRationale` checked, than result also has it checked.
     *
     *
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    fun <T> ensureEachCombined(vararg permissions: String): ObservableTransformer<T, Permission> {
        return ObservableTransformer { o ->
            request(o, *permissions)
                .buffer(permissions.size)
                .flatMap { permissions ->
                    if (permissions.isEmpty()) {
                        Observable.empty()
                    } else Observable.just(Permission(permissions))
                }
        }
    }

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    fun request(vararg permissions: String): Observable<Boolean1> {
        return Observable.just(TRIGGER).compose(ensure<Any>(*permissions))
    }

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    fun requestEach(vararg permissions: String): Observable<Permission> {
        return Observable.just(TRIGGER).compose(ensureEach<Any>(*permissions))
    }

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    fun requestEachCombined(vararg permissions: String): Observable<Permission> {
        return Observable.just(TRIGGER).compose(ensureEachCombined<Any>(*permissions))
    }

    private fun request(
        trigger: Observable<*>,
        vararg permissions: String
    ): Observable<Permission> {
        require(!(permissions == null || permissions.isEmpty())) { "RxPermissions.request/requestEach requires at least one input permission" }
        return oneOf(trigger, pending(*permissions))
            .flatMap { requestImplementation(*permissions) }
    }

    private fun pending(vararg permissions: String): Observable<Any> {
        for (p in permissions) {
            if (!mRxPermissionsFragment.containsByPermission(p)) {
                return Observable.empty<Any>()
            }
        }
        return Observable.just(TRIGGER)
    }

    private fun oneOf(trigger: Observable<*>?, pending: Observable<*>): Observable<*> {
        return if (trigger == null) {
            Observable.just(TRIGGER)
        } else Observable.merge(trigger, pending)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestImplementation(vararg permissions: String): Observable<Permission> {
        val list = ArrayList<Observable<Permission>>(permissions.size)
        val unrequestedPermissions = ArrayList<String>()

        // In case of multiple permissions, we create an Observable for each of them.
        // At the end, the observables are combined to have a unique response.
        for (permission in permissions) {
            mRxPermissionsFragment.log("Requesting permission $permission")
            if (isGranted(permission)) {
                // Already granted, or not Android M
                // Return a granted Permission object.
                list.add(Observable.just(Permission(permission, true, false)))
                continue
            }

            if (isRevoked(permission)) {
                // Revoked by a policy, return a denied Permission object.
                list.add(Observable.just(Permission(permission, false, false)))
                continue
            }

            var subject = mRxPermissionsFragment.getSubjectByPermission(permission)
            // Create a new subject if not exists
            if (subject == null) {
                unrequestedPermissions.add(permission)
                subject = PublishSubject.create()
                mRxPermissionsFragment.setSubjectForPermission(permission, subject)
            }

            list.add(subject)
        }

        if (!unrequestedPermissions.isEmpty()) {
            val unrequestedPermissionsArray = unrequestedPermissions.toTypedArray()
            requestPermissionsFromFragment(unrequestedPermissionsArray)
        }
        return Observable.concat(Observable.fromIterable(list))
    }

    /**
     * Invokes AppCompatActivity.shouldShowRequestPermissionRationale and wraps
     * the returned value in an observable.
     *
     *
     * In case of multiple permissions, only emits true if
     * AppCompatActivity.shouldShowRequestPermissionRationale returned true for
     * all revoked permissions.
     *
     *
     * You shouldn't call this method if all permissions have been granted.
     *
     *
     * For SDK &lt; 23, the observable will always emit false.
     */
    fun shouldShowRequestPermissionRationale(
        activity: AppCompatActivity,
        vararg permissions: String
    ): Observable<Boolean1> {
        return if (!isMarshmallow) {
            Observable.just(false)
        } else Observable.just(
            shouldShowRequestPermissionRationaleImplementation(
                activity,
                *permissions
            )
        )
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun shouldShowRequestPermissionRationaleImplementation(
        activity: AppCompatActivity,
        vararg permissions: String
    ): Boolean1 {
        for (p in permissions) {
            if (!isGranted(p) && !activity.shouldShowRequestPermissionRationale(p)) {
                return false
            }
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.M)
    internal fun requestPermissionsFromFragment(permissions: Array<String>) {
        mRxPermissionsFragment.log(
            "requestPermissionsFromFragment " + TextUtils.join(
                ", ",
                permissions
            )
        )
        mRxPermissionsFragment.requestPermissions(permissions)
    }

    /**
     * Returns true if the permission is already granted.
     *
     *
     * Always true if SDK &lt; 23.
     */
    fun isGranted(permission: String): Boolean1 {
        return !isMarshmallow || mRxPermissionsFragment.isGranted(permission)
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     *
     *
     * Always false if SDK &lt; 23.
     */
    fun isRevoked(permission: String): Boolean1 {
        return isMarshmallow && mRxPermissionsFragment.isRevoked(permission)!!
    }

    internal fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray) {
        mRxPermissionsFragment.onRequestPermissionsResult(
            permissions,
            grantResults,
            BooleanArray(permissions.size)
        )
    }

    companion object {

        internal val TAG = "RxPermissions"
        internal val TRIGGER = Any()
    }

}