

#ifndef _CUTILS_H
#define _CUTILS_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL
Java_cn_nlifew_linovel_utils_CUtils_getStringOffset(JNIEnv*, jclass, jstring, jint);

JNIEXPORT jint JNICALL
Java_cn_nlifew_linovel_utils_CUtils_getStringUTFOffset(JNIEnv*, jclass, jstring, jint);

#ifdef __cplusplus
};
#endif
#endif