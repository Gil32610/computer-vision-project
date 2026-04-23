package com.project.ar

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.HitResult
import com.project.ar.data.MeasurementRequest
import com.project.ar.data.Point3D
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

        sceneView.onTapAr = { hitResult, _ -> handleTap(hitResult) }
    }

    private fun handleTap(hitResult: HitResult) {
        // 1. Reset if a full measurement was already completed
        if (points.size >= 2) {
            markerNodes.forEach {
                sceneView.removeChild(it)
                it.destroy()
            }
            markerNodes.clear()
            points.clear()
            distanceDisplay.text = "Distance: -- cm"
        }

        // 2. Capture the coordinates
        val pose = hitResult.hitPose
        val newPoint = Point3D(pose.tx(), pose.ty(), pose.tz())
        points.add(newPoint)

        // 3. Create the physical Anchor
        val anchor = hitResult.createAnchor()
        val arNode = ArNode(sceneView.engine, anchor)
        val modelNode = ModelNode(sceneView.engine)

        // 4. Load the Sphere (Simple and Robust)
        lifecycleScope.launch {
            modelNode.loadModelGlb(
                    context = this@MainActivity,
                    glbFileLocation = "models/sphere.glb",
                    scaleToUnits = 0.05f // 5cm sphere
            )
            modelNode.centerModel()
            modelNode.position = io.github.sceneview.math.Position(y = 0.01f) // 1cm lift
            arNode.addChild(modelNode)
        }

        // 5. Add to scene
        sceneView.addChild(arNode)
        markerNodes.add(arNode)

        Toast.makeText(this, "Point ${points.size} set", Toast.LENGTH_SHORT).show()

        // 6. Only call backend when exactly two points are ready
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
                Toast.makeText(this@MainActivity, "Check your FastAPI server!", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }
}
