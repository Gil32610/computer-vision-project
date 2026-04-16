package com.project.ar

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.HitResult
import com.project.ar.data.Point3D
import com.project.ar.data.MeasurementRequest
import com.project.ar.network.RetrofitClient
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: ArSceneView
    private lateinit var distanceDisplay: TextView

    private val points = mutableListOf<Point3D>()
    private val markerNodes = mutableListOf<ArNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        distanceDisplay = findViewById(R.id.distanceDisplay)
        sceneView = findViewById(R.id.sceneView)

        sceneView.configureSession { _, config ->
    config.planeFindingMode =
        com.google.ar.core.Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
}

        sceneView.planeRenderer.isVisible = true

        sceneView.onTapAr = { hitResult, _ ->
        handleTap(hitResult)
}
    }

    private fun handleTap(hitResult: HitResult) {
    // 1. Reset logic if we already have a measurement
    if (points.size >= 2) {
        markerNodes.forEach { it.detachFromScene(sceneView) }
        markerNodes.clear()
        points.clear()
        distanceDisplay.text = "Distance: -- cm"
    }

    val pose = hitResult.hitPose
    val newPoint = Point3D(pose.tx(), pose.ty(), pose.tz())
    points.add(newPoint)

    // 2. Create the AR Anchor
    val anchor = hitResult.createAnchor()
    val arNode = ArNode(sceneView.engine, anchor)

    // 3. Create and Load the Model inside a Coroutine
    val modelNode = ModelNode(sceneView.engine)
    
    lifecycleScope.launch {
    modelNode.loadModelGlb(
        context = this@MainActivity,
        glbFileLocation = "models/sphere.glb",
        autoAnimate = false,
        scaleToUnits = 0.05f 
    )
    
    modelNode.centerModel()
    // Lift the sphere by 1mm to 5mm to stop the flickering against the floor
    modelNode.position = io.github.sceneview.math.Position(y = 0.005f) 
    
    arNode.addChild(modelNode)
}

    // 4. Add the anchor node to the scene immediately
    sceneView.addChild(arNode)
    markerNodes.add(arNode)

    Toast.makeText(this, "Point ${points.size} set", Toast.LENGTH_SHORT).show()

    if (points.size == 2) {
        sendToBackend(points[0], points[1])
    }
}

    private fun sendToBackend(p1: Point3D, p2: Point3D) {
        lifecycleScope.launch {
            try {
                val request = MeasurementRequest(p1, p2)
                val response = RetrofitClient.instance.getDistance(request)

                distanceDisplay.text = "Distance: ${response.distanceCm} cm"

            } catch (e: Exception) {
                distanceDisplay.text = "Error: API Offline"
                Toast.makeText(
                    this@MainActivity,
                    "Check your FastAPI server!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}