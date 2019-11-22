/*!
 * \file aria.h
 * \brief ARIA 암호 알고리즘 ( 관련표준 :  KS X 1213:2004 )
 * \author
 * Copyright (c) 2011 by \<KISA\>
 */

#ifndef HEADER_ARIA_H
#define HEADER_ARIA_H

#include <jni.h>

#define ARIA_ENCRYPT	1			/*!< ARIA의 암호화*/
#define ARIA_DECRYPT	0			/*!< ARIA의 복호화*/

#define ARIA_BLOCK_SIZE	16			/*!< ARIA의 BLOCK_SIZE*/

#define ARIA128 128
#define ARIA192 192
#define ARIA256 256

//--------------------------------------------------
#define ARIA128_BLOCK_SIZE		ARIA_BLOCK_SIZE		
#define ARIA128_KEY_SIZE		16					
#define ARIA192_KEY_SIZE		24					
#define ARIA256_KEY_SIZE		32
#define ARIA128_IV_SIZE			ARIA_BLOCK_SIZE		

#ifdef  __cplusplus
extern "C" {
#endif

#define ARIA_MAXKB	32
#define ARIA_MAXNR	16
#define ARIA_WORD_SIZE  4

void GEN_ROUND_KEY2(unsigned char *rk, unsigned int *word1, unsigned int *word2, int n);
void KISA_ARIA_encrypt_init(const unsigned char *userkey, int keyBits, unsigned char *rk, int *nr);
void KISA_ARIA_process_block(const unsigned char *in, unsigned char *out, unsigned char *rk, int *nr);
void internal_aria_process_blocks(unsigned char *rk, int *nr, int encrypt, unsigned char *ivec, unsigned char *in, unsigned int inl, unsigned char *out);
int internal_aria_cbc_process_enc(int encrypt, unsigned char *rk, int *nr, unsigned char *ivec, unsigned char *cbc_buffer, int *buffer_length, unsigned char *in, int inLen, unsigned char *out, int *outLen);


JNIEXPORT void JNICALL Java_com_example_android_bluetoothchat_ARIACBC_processBlock(JNIEnv* env, jobject thiz, jbyteArray inputText, jint nr, jbyteArray rk_, jbyteArray out_, jint outputOffset);
JNIEXPORT void JNICALL Java_com_example_android_bluetoothchat_ARIACBC_ariaEncryptInit(JNIEnv* env, jobject thiz, jbyteArray user_key, jint keyBits, jbyteArray rk_, jintArray nr_);
JNIEXPORT void JNICALL Java_com_example_android_bluetoothchat_ARIACBC_ariaDecryptInit(JNIEnv* env, jobject thiz, jbyteArray user_key, jint keyBits, jbyteArray rk_, jintArray nr_);
JNIEXPORT jint JNICALL Java_com_example_android_bluetoothchat_ARIACBC_internalAriaProcessBlocks(JNIEnv* env, jobject thiz, jbyteArray rk_, jintArray nr_, jbyteArray ivec_, jbyteArray cbc_buffer_, jbyteArray outputText, jint outputTextLen);
JNIEXPORT jint JNICALL Java_com_example_android_bluetoothchat_ARIACBC_internalAriaCBCProcessEnc(JNIEnv* env, jobject thiz, jint encrypt, jbyteArray rk_, jintArray nr_, jbyteArray ivec_, jbyteArray cbc_buffer_, jintArray buffer_length_, jbyteArray inputText, jint inputOffset, jint inLen, jbyteArray outputText, jint outputOffset);
JNIEXPORT jint JNICALL Java_com_example_android_bluetoothchat_ARIACBC_internalAriaCBCProcessDec(JNIEnv* env, jobject thiz, jint encrypt, jbyteArray rk_, jintArray nr_, jbyteArray ivec_, jbyteArray cbc_buffer_, jintArray buffer_length_, jbyteArray cbc_last_block_, jintArray last_block_flag_, jbyteArray inputText, jint inputOffset, jint inLen, jbyteArray outputText, jint outputOffset);

#ifdef  __cplusplus
}
#endif
#endif /* HEADER_ARIA_H */
