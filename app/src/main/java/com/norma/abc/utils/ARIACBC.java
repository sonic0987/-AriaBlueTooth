package com.norma.abc.utils;

import androidx.lifecycle.MutableLiveData;

public class ARIACBC {
	public static final int ENC = 1;
	public static final int DEC = 0;
	
	public static final int ARIA_BLOCK_SIZE = 16;
	public static final int ARIA_MAXNR = 16;
	public static final int ARIA_WORD_SIZE = 4;	
	
	private int 			encrypt;
	private byte[] 			ivec;
	private byte[] 			rk;
	int[] 					nr;
	private byte[] 			cbc_buffer;
	private int[] 			buffer_length;
	private byte[] 			cbc_last_block;
	private int[] 			last_block_flag;	
	
	public ARIACBC() {
		this.ivec = new byte[ARIA_BLOCK_SIZE];
		this.rk = new byte[ARIA_MAXNR * (ARIA_MAXNR + 1)];
		this.nr = new int[1];
		this.cbc_buffer = new byte[ARIA_BLOCK_SIZE];
		this.buffer_length = new int[1];
		this.cbc_last_block = new byte[ARIA_BLOCK_SIZE];
		this.last_block_flag = new int[1];
	}

	static {
		System.loadLibrary("ariacbc");
	}
	
	protected static native void ariaEncryptInit(byte[] user_key, int bits, byte[] rk, int[] nr);
	private native void ariaDecryptInit(byte[] user_key, int bits, byte[] rk, int[] nr);
	private native int internalAriaCBCProcessEnc(int encrypt, byte[] rk, int[] nr, byte[] ivec, byte[] cbc_buffer, int[] buffer_length, byte[] inputText, int inputOffset, int inputTextLen, byte[] outputText, int outputOffset);
	private native int internalAriaCBCProcessDec(int encrypt, byte[] rk, int[] nr, byte[] ivec, byte[] cbc_buffer, int[] buffer_length, byte[] cbc_last_block, int[] last_block_flag, byte[] inputText, int inputOffset, int inputTextLen, byte[] outputText, int outputOffset);
	private native int internalAriaProcessBlocks(byte[] rk, int[] nr, byte[] ivec, byte[] cbc_buffer, byte[] outputText, int outputTextLen);
	protected static native void processBlock(byte[] in, int nr, byte[] rk, byte[] out, int out_index);
	
	public int init(int encrypt, int bits, byte[] user_key, byte[] iv) {
		if(user_key == null || iv == null) {
			return 0;
		}

		if(encrypt == ARIACBC.ENC) {
			ariaEncryptInit(user_key, bits, this.rk, this.nr);
		} else {
			ariaDecryptInit(user_key, bits, this.rk, this.nr);
		}
		
		System.arraycopy(iv, 0, this.ivec, 0, ARIA_BLOCK_SIZE);
		this.encrypt = encrypt;
		this.last_block_flag[0] = this.buffer_length[0] = 0;
		
		return 1;
	}
	
	public int process(byte[] inputText, int inputOffset, int inputTextLen, byte[] outputText, int outputOffset) {
		if(inputText == null || outputText == null) {
			return -1;
		}
		
		if(inputTextLen <= 0) {
			return -1;
		}
		
		if(inputOffset < 0 || inputOffset >= inputText.length || outputOffset < 0 || outputOffset >= outputText.length) {
			return -1;
		}
		
		int outputTextLen = 0;
		
		if(this.encrypt == ENC) {
			outputTextLen = internalAriaCBCProcessEnc(this.encrypt, this.rk, this.nr, this.ivec, this.cbc_buffer, buffer_length, inputText, inputOffset, inputTextLen, outputText, outputOffset);
			
			return outputTextLen;
		} else {
			outputTextLen = internalAriaCBCProcessDec(this.encrypt, this.rk, this.nr, this.ivec, this.cbc_buffer, buffer_length, this.cbc_last_block, last_block_flag, inputText, inputOffset, inputTextLen, outputText, outputOffset);

			return outputTextLen;
		}
	}
	private MutableLiveData<byte[]> a = new MutableLiveData<>();
	public int close(byte[] outputText, int outputTextLen) {
		if(outputText == null) {
			return 0;
		}
		
		int i, padLen;
		
		if(this.encrypt == ENC) {
			padLen = ARIA_BLOCK_SIZE - (this.buffer_length[0]);
			
			for(i = this.buffer_length[0]; i < ARIA_BLOCK_SIZE; i++) {
				this.cbc_buffer[i] = (byte)padLen;
			}
				
			if(internalAriaProcessBlocks(this.rk, this.nr, this.ivec, this.cbc_buffer, outputText, outputTextLen) == -1) {
				return -1;
			}
			
			outputTextLen = ARIA_BLOCK_SIZE;
		} else {
			padLen = ARIA_BLOCK_SIZE - this.cbc_last_block[ARIA_BLOCK_SIZE - 1];

			if (padLen > ARIA_BLOCK_SIZE) {
				return -1;
			}

			if (padLen > 1) {
				i = this.cbc_last_block[ARIA_BLOCK_SIZE - 1];

				while (i > 0) {
					if (this.cbc_last_block[ARIA_BLOCK_SIZE - 1] != this.cbc_last_block[ARIA_BLOCK_SIZE - i]) {
						return -1;
					}

					i--;
				}
			}

			for (i = 0; i < padLen; i++) {
				outputText[outputTextLen + i] = this.cbc_last_block[i];
			}

			outputTextLen = padLen;
		}

		for (i = 0; i < rk.length; i++) {
			this.rk[i] = 0;
		}
		
		return outputTextLen;
	}
	
	public int CBC_128ENCRYPT(byte[] user_key, byte[] iv, byte[] inputText, int inputOffset, int inputTextLen, byte[] outputText, int outputOffset) {
		int padLen = 0;
		int outputTextLen = 0;

		if(this.init(ENC, 128, user_key, iv) == 0)
		{
			return 0;
		}

		outputTextLen = this.process(inputText, inputOffset, inputTextLen, outputText, outputOffset);
		if(outputTextLen < 0)
		{
			return 0;
		}

		padLen = this.close(outputText, outputTextLen);
		if(padLen < 0)
		{
			return 0;
		}

		return outputTextLen + padLen;
	}

	public int CBC_128DECRYPT(byte[] user_key, byte[] iv, byte[] inputText, int inputOffset, int inputTextLen, byte[] outputText, int outputOffset) {
		int padLen = 0;
		int outputTextLen = 0;

		if(this.init(DEC, 128, user_key, iv) == 0)
		{
			return 0;
		}

		outputTextLen = this.process(inputText, inputOffset, inputTextLen, outputText, outputOffset);
		if(outputTextLen < 0)
		{
			return 0;
		}

		padLen = this.close(outputText, outputTextLen);
		if(padLen < 0)
		{
			return 0;
		}

		return outputTextLen + padLen;
	}
	
	public int CBC_192ENCRYPT(byte[] user_key, byte[] iv, byte[] inputText, int inputOffset, int inputTextLen, byte[] outputText, int outputOffset) {
		int padLen = 0;
		int outputTextLen = 0;

		if(this.init(ENC, 192, user_key, iv) == 0)
		{
			return 0;
		}

		outputTextLen = this.process(inputText, inputOffset, inputTextLen, outputText, outputOffset);
		if(outputTextLen < 0)
		{
			return 0;
		}

		padLen = this.close(outputText, outputTextLen);
		if(padLen < 0)
		{
			return 0;
		}

		return outputTextLen + padLen;
	}

	public int CBC_192DECRYPT(byte[] user_key, byte[] iv, byte[] inputText, int inputOffset, int inputTextLen, byte[] outputText, int outputOffset) {
		int padLen = 0;
		int outputTextLen = 0;

		if(this.init(DEC, 192, user_key, iv) == 0)
		{
			return 0;
		}

		outputTextLen = this.process(inputText, inputOffset, inputTextLen, outputText, outputOffset);
		if(outputTextLen < 0)
		{
			return 0;
		}

		padLen = this.close(outputText, outputTextLen);
		if(padLen < 0)
		{
			return 0;
		}

		return outputTextLen + padLen;
	}
	
	public int CBC_256ENCRYPT(byte[] user_key, byte[] iv, byte[] inputText, int inputOffset, int inputTextLen, byte[] outputText, int outputOffset) {
		int padLen = 0;
		int outputTextLen = 0;

		if(this.init(ENC, 256, user_key, iv) == 0)
		{
			return 0;
		}

		outputTextLen = this.process(inputText, inputOffset, inputTextLen, outputText, outputOffset);
		if(outputTextLen < 0)
		{
			return 0;
		}

		padLen = this.close(outputText, outputTextLen);
		if(padLen < 0)
		{
			return 0;
		}

		return outputTextLen + padLen;
	}
	
	public int CBC_256DECRYPT(byte[] user_key, byte[] iv, byte[] inputText, int inputOffset, int inputTextLen, byte[] outputText, int outputOffset) {
		int padLen = 0;
		int outputTextLen = 0;

		if(this.init(DEC, 256, user_key, iv) == 0)
		{
			return 0;
		}

		outputTextLen = this.process(inputText, inputOffset, inputTextLen, outputText, outputOffset);
		if(outputTextLen < 0)
		{
			return 0;
		}

		padLen = this.close(outputText, outputTextLen);
		if(padLen < 0)
		{
			return 0;
		}

		return outputTextLen + padLen;
	}
	
	public int getOutputSize(int inputLen) {
		return this.getOutputSize(this.encrypt, inputLen);
	}
	
	public int getOutputSize(int enc, int inputLen) {
		int outputLen = 0, padLen;
		
		if(enc == ENC) {
			padLen = ARIA_BLOCK_SIZE - inputLen % ARIA_BLOCK_SIZE;
			if(padLen == ARIA_BLOCK_SIZE) {
				outputLen = inputLen + ARIA_BLOCK_SIZE;
			} else {
				outputLen = inputLen + padLen;
			}
		} else {
			outputLen = inputLen;
		}
		
		return outputLen ;
	}	
}
