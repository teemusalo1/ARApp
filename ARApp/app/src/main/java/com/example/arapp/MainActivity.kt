package com.example.arapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Point
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.widget.Button
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class MainActivity : AppCompatActivity() {
    private lateinit var arFrag: ArFragment
    private var viewRenderable: ViewRenderable? = null
    private var modelRenderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var btn = findViewById<Button>(R.id.button)
        arFrag = supportFragmentManager.findFragmentById(
            R.id.sceneform_fragment) as ArFragment
        val renderableFuture = ViewRenderable.builder()
         .setView(this, R.layout.view_renderable)
         .build()
         renderableFuture.thenAccept{viewRenderable = it}
         arFrag.setOnTapArPlaneListener { hitResult: HitResult?, _, _ ->
             if (viewRenderable == null) {
             return@setOnTapArPlaneListener
             }
             //Creates a new anchor at the hit location
             val anchor = hitResult!!.createAnchor()
             //Creates a new anchorNode attaching it to anchor
             val anchorNode = AnchorNode(anchor)
             // Add anchorNode as root scene node's child
             anchorNode.setParent(arFrag.arSceneView.scene)
             // Can be selected, rotated...
             val viewNode = TransformableNode(arFrag.transformationSystem)
             // Add viewNode as anchorNode's child
             viewNode.setParent(anchorNode)
             viewNode.renderable = viewRenderable
             // Sets this as the selected node in the TransformationSystem
            viewNode.select()
             btn.setOnClickListener {
                 add3dObject()
                 Log.d("paska", "meneeekö tämä")
             }
             arFrag = supportFragmentManager.findFragmentById(
                     R.id.sceneform_fragment) as ArFragment

             val uri = Uri.parse("https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/CesiumMan/glTF/CesiumMan.gltf")
                  val renderableFuture = ModelRenderable.builder().setSource(this, RenderableSource.builder().setSource(this, uri, RenderableSource.SourceType.GLTF2)
                      .setScale(0.8f) // Scale the original to 20%.
                      .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                      .build())
              .setRegistryId("CesiumMan").build()
              renderableFuture.thenAccept { modelRenderable = it }
              renderableFuture.exceptionally {
                 Log.e("TAG", "renderableFuture error: ${it.localizedMessage}")
                  null
                  }
             }

    }
    private fun getScreenCenter(): Point {
         // find the root view of the activity
         val vw = findViewById<android.view.View>(android.R.id.content)
         // returns center of the screen as a Point object
         return Point(vw.width / 2, vw.height / 2)
         }
    private fun add3dObject() {
         val frame = arFrag.arSceneView.arFrame
         if (frame != null && modelRenderable != null) {
             val pt = getScreenCenter()
             // get list of HitResult of the given location in the camera view
             val hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                 val trackable = hit.trackable
                 if (trackable is Plane) {
                     val anchor = hit!!.createAnchor()
                     val anchorNode = AnchorNode(anchor)
                     anchorNode.setParent(arFrag.arSceneView.scene)
                     val mNode = TransformableNode(arFrag.transformationSystem)
                     mNode.setOnTapListener{ hitTestResult: HitTestResult, motionEvent: MotionEvent ->


                         val button = findViewById<Button>(R.id.button)
                         button.visibility = INVISIBLE

                     }
                     mNode.setParent(anchorNode)
                     mNode.renderable = modelRenderable
                             mNode.select()
                     break
                    }
                 }
             }

    }}