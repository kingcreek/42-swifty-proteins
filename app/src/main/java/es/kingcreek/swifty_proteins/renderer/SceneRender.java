package es.kingcreek.swifty_proteins.renderer;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.Scene;

import es.kingcreek.swifty_proteins.interfaces.AtomCallback;

public class SceneRender {

    private final String TAG = "SceneRender";
    private SceneView sceneView;
    // Listener for rotation/traslation
    private DragTransformableNode dragTransformableNode;
    // Listener to detect clicks on atoms
    private TransformationSystem transformationSystem;
    // Listener to perfom zoom
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1f;
    private Node rootNode;
    AtomCallback atomCallback;
    private Scene.OnUpdateListener onUpdateListener;

    private final String ROOT_NODE_NAME = "root";
    private final Vector3 ROOT_NODE_POSITION = new Vector3(0f, 0f, 0f);
    private final Vector3 ROOT_NODE_SCALE = new Vector3(1f, 1f, 1f);
    private final float DEFAULT_NODE_RADIUS = 15.0f;
    private final Vector3 AXIS_X = new Vector3(1.0f, 0.0f, 0.0f);
    private final float AXIS_ROTATION_ANGLE = 90f;
    private final float SPHERE_RADIUS = .15F;
    // private final Vector3 SPHERE_SCALE = new Vector3(4f, 4f, 4f);
    private final Vector3 SPHERE_SCALE = new Vector3(2f, 2f, 2f);
    private final float CYLINDER_RADIUS = .1F;
    private final Vector3 DEFAULT_CAMERA_POSITION = new Vector3(0f, 0f, 20f);
    private final float FAR_CLIP_PLANE = 50f;
    private final float MIN_CAMERA_SCALE = 1.0F;
    private final float MAX_CAMERA_SCALE = 5.0F;
    private final String HYDROGEN = "H";

    // Vars to detect real click on atom
    private long lastDownTime = 0;
    private float lastDownX = 0;
    private float lastDownY = 0;


    private final ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_CAMERA_SCALE, Math.min(scaleFactor, MAX_CAMERA_SCALE));
            rootNode.setWorldScale(new Vector3(scaleFactor, scaleFactor, scaleFactor));
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {}
    };

    public void initSceneView(Context context, SceneView sceneView, AtomCallback atomCallback) {
        this.atomCallback = atomCallback;
        this.sceneView = sceneView;
        this.sceneView.getScene().getCamera().setWorldPosition(DEFAULT_CAMERA_POSITION);
        sceneView.getScene().getCamera().setFarClipPlane(FAR_CLIP_PLANE);

        transformationSystem = new TransformationSystem(sceneView.getContext().getResources().getDisplayMetrics(), new FootprintSelectionVisualizer());
        scaleGestureDetector = new ScaleGestureDetector(context, onScaleGestureListener);
        sceneView.getScene().addOnPeekTouchListener((hitTestResult, motionEvent) -> {
            scaleGestureDetector.onTouchEvent(motionEvent);
            transformationSystem.onTouch(hitTestResult, motionEvent);

            if (hitTestResult.getNode() != null) {
                Node node = hitTestResult.getNode();

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastDownTime = motionEvent.getEventTime();
                        lastDownX = motionEvent.getX();
                        lastDownY = motionEvent.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        long eventTime = motionEvent.getEventTime();
                        float eventX = motionEvent.getX();
                        float eventY = motionEvent.getY();

                        // Check if the touch duration is short enough to be a click
                        long timeDelta = eventTime - lastDownTime;
                        float distance = (float) Math.sqrt(Math.pow(eventX - lastDownX, 2) + Math.pow(eventY - lastDownY, 2));

                        if (timeDelta < 200 && distance < 20) {
                            if (node.getName() != null && !node.getName().isEmpty() && !node.getName().equals("cylinder") && !node.getName().equals("cylinderH")) {
                                atomCallback.onAtomClicked(node.getName());
                            }
                        }
                        break;
                }
            }
        });

        createRootNode();
    }

    public void toggleMode() {
        if (dragTransformableNode != null) {
            dragTransformableNode.toggleMode();
        }
    }

    public boolean getMode() {
        return dragTransformableNode.getMode();
    }

    private void createRootNode() {
        rootNode = getNode();//new Node();
        rootNode.setName(ROOT_NODE_NAME);
        rootNode.setWorldPosition(ROOT_NODE_POSITION);
        rootNode.setWorldScale(ROOT_NODE_SCALE);
        sceneView.getScene().addChild(rootNode);
        selectIfRequired(rootNode);
    }

    // SPHERES
    public void setSphere(Context context, Vector3 position, int color, String name) {
        MaterialFactory.makeOpaqueWithColor(context, new Color(color))
                .thenAccept(material -> {
                    addModelSphereToScene(makeSphere(material), position, name);
                });
    }

    private ModelRenderable makeSphere(Material material) {
        return ShapeFactory.makeSphere(SPHERE_RADIUS, Vector3.zero(), material);
    }

    private void addModelSphereToScene(ModelRenderable model, Vector3 position, String name) {

        Node node = new Node();
        node.setLocalPosition(position);
        node.setLocalScale(SPHERE_SCALE);
        node.setName(name);
        node.setRenderable(model);
        addNodeToScene(node);
    }

    public void toggleHydrogen(boolean active) {
        if (rootNode != null) {
            for (Node node : rootNode.getChildren()) {
                if (node.getName() != null && (node.getName().equals("H") || node.getName().equals("cylinderH"))) {
                    node.setEnabled(active);
                }
            }
        }
    }

    // CYLINDERS/STICKS
    public void setCylinder(Context context, Vector3 pointStart, Vector3 pointEnd, int color, String name) {
        MaterialFactory.makeOpaqueWithColor(context, new Color(color))
                .thenAccept(material -> {
                    // Calculate the difference between the points
                    Vector3 differencePoints = Vector3.subtract(pointEnd, pointStart);
                    // Find the length of the cylinder so that it reaches the middle of pointEnd
                    float halfLength = differencePoints.length() / 2.0f;
                    // Calculate the position of the cylinder so that its base is at pointStart
                    Vector3 cylinderPosition = Vector3.add(pointStart, differencePoints.scaled(0.25f));
                    // Create the cylinder with the corresponding length
                    ModelRenderable model = makeCylinder(halfLength, material);
                    // Add the cylinder to the scene
                    addModelCylinderToScene(model, cylinderPosition, getCylinderRotation(differencePoints), name);
                });
    }

    private ModelRenderable makeCylinder(float height, Material material) {
        return ShapeFactory.makeCylinder(CYLINDER_RADIUS, height, Vector3.zero(), material);
    }

    private void addModelCylinderToScene(ModelRenderable model, Vector3 position, Quaternion rotation, String name) {

        Node node = new Node();
        node.setLocalPosition(position);
        node.setRenderable(model);
        node.setWorldRotation(rotation);
        node.setName(name);
        addNodeToScene(node);
    }

    private Quaternion getCylinderRotation(Vector3 differencePoints) {
        Vector3 direction = differencePoints.normalized();
        Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
        Quaternion quaterRotation = Quaternion.axisAngle(AXIS_X, AXIS_ROTATION_ANGLE);
        return Quaternion.multiply(lookRotation, quaterRotation);
    }

    private void addNodeToScene(Node node) {
        if (rootNode != null) {
            rootNode.addChild(node);
        }
    }

    private void selectIfRequired(Node node) {
        if (node instanceof TransformableNode) {
            ((TransformableNode) node).select();
        }
    }

    private Node getNode() {
        if (transformationSystem != null) {
            dragTransformableNode = new DragTransformableNode(DEFAULT_NODE_RADIUS, transformationSystem);
            return dragTransformableNode;
        }
        return new Node();
    }

    public void onAtomsRendered() {
        onUpdateListener = frameTime -> {
            if (!rootNode.getChildren().isEmpty()) {
                // Remove the update listener after it's called once
                sceneView.getScene().removeOnUpdateListener(onUpdateListener);
                // Notify the callback that rendering is done
                atomCallback.onViewLoaded();
            }
        };
        // Add listener
        sceneView.getScene().addOnUpdateListener(onUpdateListener);
    }
}