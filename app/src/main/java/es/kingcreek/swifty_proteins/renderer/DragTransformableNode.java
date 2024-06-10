package es.kingcreek.swifty_proteins.renderer;

import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.BaseTransformationController;
import com.google.ar.sceneform.ux.DragGesture;
import com.google.ar.sceneform.ux.DragGestureRecognizer;

public class DragTransformableNode extends TransformableNode {

    private static final double initialLat = 26.15444376319647;
    private static final double initialLon = 18.995950736105442;
    private static double lat = initialLat;
    private static double lon = initialLon;
    private static boolean isRotationMode = true; // Variable to switch between rotation and translation mode

    private final float radius;
    private final float rotationRateDegrees = 0.5f;
    private final float moveFactor = 0.01f; // Factor to control translation speed

    public DragTransformableNode(float radius, TransformationSystem transformationSystem) {
        super(transformationSystem);
        this.radius = radius;
        new DragController(this, transformationSystem.getDragRecognizer());
    }

    public float getRadius() {
        return radius;
    }

    public void toggleMode() {
        isRotationMode = !isRotationMode;
    }

    public boolean getMode() {
        return isRotationMode;
    }

    private class DragController extends BaseTransformationController<DragGesture> {

        private final DragTransformableNode transformableNode;

        public DragController(DragTransformableNode transformableNode, DragGestureRecognizer gestureRecognizer) {
            super(transformableNode, gestureRecognizer);
            this.transformableNode = transformableNode;
        }

        @Override
        public boolean canStartTransformation(DragGesture gesture) {
            return transformableNode.isSelected();
        }

        @Override
        public void onActivated(Node node) {
            super.onActivated(node);
        }

        @Override
        protected void onContinueTransformation(DragGesture gesture) {
            if (isRotationMode) {
                // Get the delta values of gesture movement
                float deltaX = gesture.getDelta().x;
                float deltaY = gesture.getDelta().y;

                // Invert the rotation amount on the Y axis
                deltaY *= -1.0f;

                // Compute the rotation angle based on gesture movement
                float rotationAmount = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY) * rotationRateDegrees;

                // Get the world space rotation of the node
                Quaternion nodeWorldRotation = transformableNode.getWorldRotation();

                // Compute the rotation axis based on the direction of gesture movement in world space
                Vector3 rotationAxis = new Vector3(-deltaY, deltaX, 0.0f).normalized();

                // Create a quaternion for the rotation
                Quaternion rotation = Quaternion.axisAngle(rotationAxis, rotationAmount);

                // Apply the rotation to the node in world space
                transformableNode.setWorldRotation(Quaternion.multiply(rotation, nodeWorldRotation));
            } else {
                // Translate the node
                float deltaX = gesture.getDelta().x * moveFactor * -1.0f;
                float deltaY = gesture.getDelta().y * moveFactor * -1.0f;

                Vector3 currentPosition = transformableNode.getWorldPosition();
                Vector3 newPosition = new Vector3(currentPosition.x - deltaX, currentPosition.y + deltaY, currentPosition.z);

                transformableNode.setWorldPosition(newPosition);
            }
        }

        @Override
        protected void onEndTransformation(DragGesture gesture) {}
    }
}
