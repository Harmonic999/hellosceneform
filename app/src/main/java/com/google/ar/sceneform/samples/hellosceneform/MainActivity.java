
package com.google.ar.sceneform.samples.hellosceneform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Locale;
import java.util.Objects;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "test_" + MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private static final String FIRST_NODE_NAME = "first_node";
    private static final String SECOND_NODE_NAME = "second_node";

    private static final Color formColor = new Color(0, 0, 0, 0.5f);

    private ArFragment arFragment;

    private AnchorNode firstAnchorNode;
    private AnchorNode secondAnchorNode;

    private Node firstNode;
    private Node secondNode;

    private Node lineNode;
    private Node labelNode;

    private ViewRenderable tvLabelRenderable;
    private TextView tvLabel;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        if (arFragment != null) {
            arFragment.setOnTapArPlaneListener(
                    (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                        if (firstAnchorNode == null) {
                            firstAnchorNode = createAndDrawNode(hitResult, FIRST_NODE_NAME);
                        } else if (secondAnchorNode == null) {

                            ViewRenderable.builder()
                                    .setView(this, R.layout.label_layout)
                                    .build().thenAccept(viewRenderable -> {
                                viewRenderable.setShadowCaster(false);
                                viewRenderable.setShadowReceiver(false);
                                tvLabel = viewRenderable.getView().findViewById(R.id.tv_label);
                                tvLabelRenderable = viewRenderable;
                            });

                            secondAnchorNode = createAndDrawNode(hitResult, SECOND_NODE_NAME);
                            lineNode = createAndDrawLineBetweenTwoPositions(
                                    firstAnchorNode.getWorldPosition(),
                                    secondAnchorNode.getWorldPosition()
                            );

                            secondAnchorNode.addTransformChangedListener((node, node1) -> updateLine(
                                    lineNode,
                                    firstNode.getWorldPosition(),
                                    secondNode.getWorldPosition()));
                        }
                    });
        }

    }

    public AnchorNode createAndDrawNode(HitResult hitResult, String name) {

        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        drawSphere(anchorNode, name);
        return anchorNode;
    }

    private void drawSphere(AnchorNode anchorNode, String name) {
        MaterialFactory.makeTransparentWithColor(getApplicationContext(), formColor)
                .thenAccept(
                        material -> {

                            ModelRenderable model = ShapeFactory.makeSphere(0.015f, Vector3.zero(), material);
                            model.setShadowCaster(false);
                            model.setShadowReceiver(false);

                            Node node = new Node();

                            node.setParent(anchorNode);
                            node.setRenderable(model);
                            node.setName(name);

                            if (FIRST_NODE_NAME.equals(name)) {
                                firstNode = node;
                            } else if (SECOND_NODE_NAME.equals(name)) {
                                secondNode = node;
                            }

                        });
    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        String openGlVersionString =
                ((ActivityManager) Objects
                        .requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE)))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    private Node createAndDrawLineBetweenTwoPositions(Vector3 pos1, Vector3 pos2) {

        final Vector3 difference = Vector3.subtract(pos1, pos2);
        final Vector3 directionFromTopToBottom = difference.normalized();
        final Quaternion rotationFromAToB =
                Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());

        Node node = new Node();
        MaterialFactory.makeTransparentWithColor(getApplicationContext(), formColor)
                .thenAccept(
                        material -> {

                            ModelRenderable model = ShapeFactory.makeCube(
                                    new Vector3(.01f, .01f, difference.length()),
                                    Vector3.zero(), material);

                            model.setShadowCaster(false);
                            model.setShadowReceiver(false);

                            node.setParent(firstAnchorNode);
                            node.setRenderable(model);
                            node.setWorldPosition(Vector3.add(pos1, pos2).scaled(.5f));
                            node.setWorldRotation(rotationFromAToB);
                        }
                );
        return node;
    }

    private void updateLine(Node node, Vector3 pos1, Vector3 pos2) {
        final Vector3 difference = Vector3.subtract(pos1, pos2);
        final Vector3 directionFromTopToBottom = difference.normalized();
        final Quaternion rotationFromAToB =
                Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());

        MaterialFactory.makeTransparentWithColor(getApplicationContext(), formColor)
                .thenAccept(
                        material -> {

                            ModelRenderable model = ShapeFactory.makeCube(
                                    new Vector3(.01f, .01f, difference.length()),
                                    Vector3.zero(), material);

                            model.setShadowCaster(false);
                            model.setShadowReceiver(false);
                            node.setRenderable(model);
                            node.setWorldPosition(Vector3.add(pos1, pos2).scaled(.5f));
                            node.setWorldRotation(rotationFromAToB);

                            if (labelNode == null && tvLabelRenderable != null) {
                                labelNode = new Node();
                                labelNode.setParent(node);
                                labelNode.setRenderable(tvLabelRenderable);
                                labelNode.setWorldPosition(node.getWorldPosition());
                                updateDifferenceInMetersLabelState(labelNode, difference.length());
                            } else if (tvLabelRenderable != null) {
                                updateDifferenceInMetersLabelState(labelNode, difference.length());
                            }
                        }
                );
    }

    private void updateDifferenceInMetersLabelState(Node labelNode, float difference) {
        Scene scene = labelNode.getScene();
        if (scene != null) {
            Vector3 cameraPosition = scene.getCamera().getWorldPosition();
            Vector3 direction = Vector3.subtract(cameraPosition, labelNode.getWorldPosition());
            Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
            labelNode.setWorldRotation(lookRotation);
        }
        labelNode.setLocalPosition(new Vector3(0, 0.04f, 0));
        tvLabel.setText(String.format(Locale.US, "%.2f", difference) + "m");
    }
}
