package com.project.ar

import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.math.Position

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.project.ar.data.*
import com.project.ar.network.RetrofitClient
import io.github.sceneview.ar.ArSceneView
import com.google.ar.core.HitResult // Added this import
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: ArSceneView
    private val points = mutableListOf<Point3D>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById<ArSceneView>(R.id.sceneView).apply {
            planeRenderer.isVisible = true 
            
            onTapAr = { hitResult: HitResult, _ ->
                val pose = hitResult.hitPose
                val newPoint = Point3D(pose.tx(), pose.ty(), pose.tz())
                
                // --- FIXED VISUAL MARKER ---
                // Pass the sceneView's engine to the Node
                val node = ArNode(engine).apply {
                    // In 0.10.0, use worldPosition or set the anchor
                    anchor = hitResult.createAnchor()
                }
                addChild(node)
                // ---------------------------

                points.add(newPoint)
                Toast.makeText(this@MainActivity, "Point ${points.size} set", Toast.LENGTH_SHORT).show()

                if (points.size == 2) {
                    sendToBackend(points[0], points[1])
                    points.clear() 
                }
            }
        }
    }

    private fun sendToBackend(p1: Point3D, p2: Point3D) {
        lifecycleScope.launch {
            try {
                val request = MeasurementRequest(p1, p2)
                val response = RetrofitClient.instance.getDistance(request)
                
                Toast.makeText(this@MainActivity, 
                    "Distance: ${response.distanceCm} cm", 
                    Toast.LENGTH_LONG).show()
                
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Connection Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}