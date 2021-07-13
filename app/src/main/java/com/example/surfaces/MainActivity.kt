package com.example.surfaces

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.hardware.camera2.CameraManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.surfaces.helpers.ContextHelper
import com.example.surfaces.helpers.OpenGLScene
import com.example.surfaces.helpers.PermissionHelper
import com.example.surfaces.machines.*


class MainActivity : AppCompatActivity() {
    private lateinit var recordBtn: TextView
    private lateinit var glSurfaceView: GLSurfaceView

    private val glSurfaceMachine = GLSurfaceMachine()
    private val cameraMachine = CameraMachine()
    private val encoderMachine = EncoderMachine()

    private val permission = PermissionHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gl_layout_activity)

        recordBtn = findViewById(R.id.record_btn)
        glSurfaceView = findViewById(R.id.gl_view)

        glSurfaceView.holder.setFixedSize(1080, 1920)

        glSurfaceMachine.send(
            GLSurfaceAction.Create(
                glSurfaceView,
                CanvasDrawable(),
                encoderMachine.drawingCaller
            )
        )

        encoderMachine.toggleRecordCallback = { isActive ->
            recordBtn.text = getString(
                if (isActive) R.string.record_stop
                else R.string.record_start
            )
        }

        recordBtn.setOnClickListener {
            glSurfaceMachine.send(GLSurfaceAction.Stop)
            encoderMachine.send(EncoderAction.Stop)
            cameraMachine.send(CameraAction.Stop)
//            if (encoderMachine.isActive()) {
//                encoderMachine.send(EncoderAction.Stop)
//            } else {
//                permission.checkOrAsk(WRITE_EXTERNAL_STORAGE) {
//                    encoderMachine.send(
//                        EncoderAction.Start(
//                            glSurfaceMachine.encoderProducer
//                        )
//                    )
//                }
//            }
        }

        val params = window.attributes
        params.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_SEAMLESS
        window.attributes = params
    }

    override fun onResume() {
        super.onResume()
        glSurfaceMachine.send(GLSurfaceAction.Start)
        permission.checkOrAsk(CAMERA) {
            cameraMachine.send(CameraAction.Start(
                getSystemService(Context.CAMERA_SERVICE) as CameraManager,
                glSurfaceMachine.fullscreenProducer
            ))
        }
    }

    override fun onPause() {
        super.onPause()
        glSurfaceMachine.send(GLSurfaceAction.Stop)
        encoderMachine.send(EncoderAction.Stop)
        cameraMachine.send(CameraAction.Stop)
    }

    override fun onRequestPermissionsResult(
        reqCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(reqCode, permissions, grantResults)
        permission.bindTOonReqResult(reqCode, permissions, grantResults)
    }
}