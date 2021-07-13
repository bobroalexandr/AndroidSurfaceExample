#include <string.h>
#include <jni.h>
#include <android/log.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>
#include <stdbool.h>
#include <dlfcn.h>
#include <stdio.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_example_surfaces_Surfaces_patch(JNIEnv* env, jobject thiz, jobject sur)
{
    ANativeWindow* s = NULL;
    if (sur != 0) {
        s = ANativeWindow_fromSurface(env, sur);
        int32_t format = ANativeWindow_getFormat(s);
        int32_t rotation = ANativeWindow_setBuffersTransform(s, ANATIVEWINDOW_TRANSFORM_MIRROR_VERTICAL);

//                int32_t transform = ANativeWindow_setBuffersGeometry(s, 500, 700, AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM);
        __android_log_print(ANDROID_LOG_INFO, "TEST!", "Calling setBuffersTransform %d, %d", format, rotation);
    }
}
