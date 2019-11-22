package com.norma.abc.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

import com.norma.abc.R

/**
 * Created by dev.oni on 3/21/19.
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

class NotificationDialog : AlertDialog {
    private var title: TextView? = null
    private var message: TextView? = null

    private lateinit var okBtn: View

    constructor(context: Context) : super(context)

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener) : super(context, cancelable, cancelListener)


    class Builder(mContext: Context) {
        private lateinit var v: View
        private lateinit var dialog: NotificationDialog

        init {
            init(mContext)
        }

        private fun init(ctx: Context) {
            this.v = LayoutInflater.from(ctx).inflate(R.layout.layout_notificationdialog, null)
            dialog = NotificationDialog(ctx, R.style.Theme_AppCompat_Light_Dialog_Alert)

            viewInit()
        }

        private fun viewInit() {
            dialog.title = v.findViewById(R.id.title)
            dialog.message = v.findViewById(R.id.message)

            dialog.okBtn = v.findViewById(R.id.submit)
        }

        private fun buildConfigure() {
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setView(v)
        }

        fun setTitle(text: String): Builder {
            dialog.title?.text = text
            return this
        }
        fun setMessage(text: String): Builder {
            dialog.message?.text = text
            return this
        }

        fun setOnButtonClickListener(listener: OnClickListener): Builder {
            dialog.okBtn.setOnClickListener { listener.submit(dialog) }
            dialog.setOnCancelListener { listener.submit(dialog) }
            return this
        }

        fun build(): NotificationDialog? {
            buildConfigure()
            return dialog
        }
    }

    interface OnClickListener {
        fun submit(dialog: NotificationDialog)
    }
}