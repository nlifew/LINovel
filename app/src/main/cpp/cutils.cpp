

#include "cutils.h"
#include <string.h>

JNIEXPORT int JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved)
{
    return JNI_VERSION_1_4;
}


extern "C" JNIEXPORT jint JNICALL
Java_cn_nlifew_linovel_utils_CUtils_getStringOffset(JNIEnv *env, jclass type, jstring _s, jint offset)
{
    int result = -1;
    const char *str = env->GetStringUTFChars(_s, nullptr);
    if (str == nullptr || offset < 0 || offset >= strlen(str)) {
        goto bail;
    }
    result = 0;
    for (int i = 0; i < offset;) {
        int ch = str[i] & 0xff;
        if (ch <= 127) {            // 单字节  0xxxxxxxx
            i += 1;
        } else if (ch <= 223) {     // 双字节  110xxxxx 10xxxxxx
            i += 2;
        } else if (ch <= 239) {     // 三字节  1110xxxx 10xxxxxx 10xxxxxx
            i += 3;
        } else if (ch <= 247) {     // 四字节  11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
            i += 4;
        } else {
            // 根据 unicode 规范，unicode 的合法范围 [0, 10FFFF]
            // 也就是最多用 4 个字节表示，现在绝对不正常
            result = -1;
            goto bail;
        }
        result ++;
    }
bail:
    if (str != nullptr) {
        env->ReleaseStringUTFChars(_s, str);
    }
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_cn_nlifew_linovel_utils_CUtils_getStringUTFOffset(JNIEnv *env, jclass type, jstring _s, jint offset)
{
    if (offset < 0 || offset > env->GetStringLength(_s)) {
        return -1;
    }
    const jchar *s = env->GetStringCritical(_s, nullptr);
    if (s == nullptr) {
        return -1;
    }
    int result = 0;
    for (int i = 0; i < offset; i++) {
        if (s[i] < 0x80) {
            result += 1;
        } else if (s[i] < 0x800) {
            result += 2;
        } else if (s[i] < 0xd800 || s[i] > 0xDFFF) {
            result += 3;
        } else {
            result += 4;
            i ++;
        }
    }
    env->ReleaseStringCritical(_s, s);
    return result;
}
