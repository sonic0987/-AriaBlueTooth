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

class SecureKeyChainDialog : AlertDialog {
    private var mainText: TextView? = null
    private var subText: TextView? = null

    private lateinit var key1: TextView
    private lateinit var key2: TextView
    private lateinit var key3: TextView
    private lateinit var key4: TextView
    private lateinit var key5: TextView
    private lateinit var key6: TextView
    private lateinit var key7: TextView
    private lateinit var key8: TextView
    private lateinit var key9: TextView
    private lateinit var key10: TextView

    private lateinit var key11: TextView
    private lateinit var key12: TextView
    private lateinit var key13: TextView
    private lateinit var key14: TextView
    private lateinit var key15: TextView
    private lateinit var key16: TextView
    private lateinit var key17: TextView
    private lateinit var key18: TextView
    private lateinit var key19: TextView
    private lateinit var key20: TextView

    private lateinit var key21: TextView
    private lateinit var key22: TextView
    private lateinit var key23: TextView
    private lateinit var key24: TextView
    private lateinit var key25: TextView
    private lateinit var key26: TextView
    private lateinit var key27: TextView
    private lateinit var key28: TextView
    private lateinit var key29: TextView
    private lateinit var key30: TextView

    private lateinit var key31: TextView
    private lateinit var key32: TextView

    private lateinit var okBtn: View
    private lateinit var cancelBtn: View

    constructor(context: Context) : super(context)

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener) : super(context, cancelable, cancelListener)


    class Builder(mContext: Context) {
        private lateinit var v: View
        private lateinit var dialog: SecureKeyChainDialog

        init {
            init(mContext)
        }

        private fun init(ctx: Context) {
            this.v = LayoutInflater.from(ctx).inflate(R.layout.layout_securekeydialog, null)
            dialog = SecureKeyChainDialog(ctx, R.style.Theme_AppCompat_Light_Dialog_Alert)

            viewInit()
        }

        private fun viewInit() {
            dialog.key1 = v.findViewById(R.id.k_1)
            dialog.key2 = v.findViewById(R.id.k_2)
            dialog.key3 = v.findViewById(R.id.k_3)
            dialog.key4 = v.findViewById(R.id.k_4)
            dialog.key5 = v.findViewById(R.id.k_5)
            dialog.key6 = v.findViewById(R.id.k_6)
            dialog.key7 = v.findViewById(R.id.k_7)
            dialog.key8 = v.findViewById(R.id.k_8)
            dialog.key9 = v.findViewById(R.id.k_9)
            dialog.key10= v.findViewById(R.id.k_10)

            dialog.key11 = v.findViewById(R.id.k_11)
            dialog.key12 = v.findViewById(R.id.k_12)
            dialog.key13 = v.findViewById(R.id.k_13)
            dialog.key14 = v.findViewById(R.id.k_14)
            dialog.key15 = v.findViewById(R.id.k_15)
            dialog.key16 = v.findViewById(R.id.k_16)
            dialog.key17 = v.findViewById(R.id.k_17)
            dialog.key18 = v.findViewById(R.id.k_18)
            dialog.key19 = v.findViewById(R.id.k_19)
            dialog.key20= v.findViewById(R.id.k_20)

            dialog.key21 = v.findViewById(R.id.k_21)
            dialog.key22 = v.findViewById(R.id.k_22)
            dialog.key23 = v.findViewById(R.id.k_23)
            dialog.key24 = v.findViewById(R.id.k_24)
            dialog.key25 = v.findViewById(R.id.k_25)
            dialog.key26 = v.findViewById(R.id.k_26)
            dialog.key27 = v.findViewById(R.id.k_27)
            dialog.key28 = v.findViewById(R.id.k_28)
            dialog.key29 = v.findViewById(R.id.k_29)
            dialog.key30= v.findViewById(R.id.k_30)

            dialog.key31 = v.findViewById(R.id.k_31)
            dialog.key32= v.findViewById(R.id.k_32)

            dialog.okBtn = v.findViewById(R.id.submit)
            dialog.cancelBtn = v.findViewById(R.id.cancel)
        }

        private fun buildConfigure() {
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setView(v)
        }

        fun setByteKey(array: ByteArray): Builder {
            dialog.key1.text = Utils.toHexSingle(array[0])
            dialog.key2.text = Utils.toHexSingle(array[1])
            dialog.key3.text = Utils.toHexSingle(array[2])
            dialog.key4.text = Utils.toHexSingle(array[3])
            dialog.key5.text = Utils.toHexSingle(array[4])
            dialog.key6.text = Utils.toHexSingle(array[5])
            dialog.key7.text = Utils.toHexSingle(array[6])
            dialog.key8.text = Utils.toHexSingle(array[7])
            dialog.key9.text = Utils.toHexSingle(array[8])
            dialog.key10.text = Utils.toHexSingle(array[9])

            dialog.key11.text = Utils.toHexSingle(array[10])
            dialog.key12.text = Utils.toHexSingle(array[11])
            dialog.key13.text = Utils.toHexSingle(array[12])
            dialog.key14.text = Utils.toHexSingle(array[13])
            dialog.key15.text = Utils.toHexSingle(array[14])
            dialog.key16.text = Utils.toHexSingle(array[15])
            dialog.key17.text = Utils.toHexSingle(array[16])
            dialog.key18.text = Utils.toHexSingle(array[17])
            dialog.key19.text = Utils.toHexSingle(array[18])
            dialog.key20.text = Utils.toHexSingle(array[19])

            dialog.key21.text = Utils.toHexSingle(array[20])
            dialog.key22.text = Utils.toHexSingle(array[21])
            dialog.key23.text = Utils.toHexSingle(array[22])
            dialog.key24.text = Utils.toHexSingle(array[23])
            dialog.key25.text = Utils.toHexSingle(array[24])
            dialog.key26.text = Utils.toHexSingle(array[25])
            dialog.key27.text = Utils.toHexSingle(array[26])
            dialog.key28.text = Utils.toHexSingle(array[27])
            dialog.key29.text = Utils.toHexSingle(array[28])
            dialog.key30.text = Utils.toHexSingle(array[29])

            dialog.key31.text = Utils.toHexSingle(array[30])
            dialog.key32.text = Utils.toHexSingle(array[31])

            return this
        }

        fun setOnButtonClickListener(listener: OnClickListener): Builder {
            dialog.okBtn.setOnClickListener { listener.submit(dialog) }
            dialog.cancelBtn.setOnClickListener {listener.cancel(dialog) }
            dialog.setOnCancelListener { listener.cancel(dialog) }
            return this
        }

        fun build(): SecureKeyChainDialog? {
            buildConfigure()
            return dialog
        }
    }

    interface OnClickListener {
        fun submit(dialog: SecureKeyChainDialog)
        fun cancel(dialog: SecureKeyChainDialog)
    }
}