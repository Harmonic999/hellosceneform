
package com.google.ar.sceneform.samples.hellosceneform;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

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
import com.google.ar.sceneform.samples.hellosceneform.util.SupportDevice;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "test_" + MainActivity.class.getSimpleName();

    private static final int MAX_LINES = 2;

    private static final Color formColor = new Color(0, 0, 0, 0.7f);
    private ArFragment arFragment;
    private List<Line> lines;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SupportDevice.check(this)) {
            return;
        }

        lines = new ArrayList<>();

        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        if (arFragment != null) {
            arFragment.setOnTapArPlaneListener(
                    (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

                        if (lines.isEmpty()) {
                            Line line = new Line();
                            line.setFirstAnchorNode(createAndDrawNode(hitResult, line));
                            lines.add(line);
                        } else {

                            Line lastLine = lines.get(lines.size() - 1);
                            if (lastLine.anchorsInitialized() && lines.size() < MAX_LINES) {
                                Line line = new Line();
                                line.setFirstAnchorNode(lastLine.getSecondAnchorNode());
                                line.setFirstNode(lastLine.getSecondNode());
                                lines.add(line);
                            }

                            for (int i = 0; i < lines.size(); i++) {
                                Line line = lines.get(i);
                                if (!line.anchorsInitialized()) {
                                    if (line.getSecondAnchorNode() == null) {
                                        buildAndSetViewLabel(line);
                                        AnchorNode anchorNode = createAndDrawNode(hitResult, line);
                                        line.setSecondAnchorNode(anchorNode);
                                        createAndDrawLineBetweenTwoPositions(line);

                                        anchorNode.addTransformChangedListener(
                                                (node, node1) -> updateLine(line)
                                        );
                                    }
                                }
                            }
                        }
                    });
        }
    }

    public void buildAndSetViewLabel(Line line) {
        ViewRenderable.builder()
                .setView(this, R.layout.label_layout)
                .build().thenAccept(viewRenderable -> {
            viewRenderable.setShadowCaster(false);
            viewRenderable.setShadowReceiver(false);
            line.setTvLabel(viewRenderable.getView().findViewById(R.id.tv_label));
            line.setTvLabelRenderable(viewRenderable);
        });
    }

    public AnchorNode createAndDrawNode(HitResult hitResult, Line line) {

        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        drawSphere(anchorNode, line);
        return anchorNode;
    }

    private void drawSphere(AnchorNode anchorNode, Line line) {
        MaterialFactory.makeTransparentWithColor(getApplicationContext(), formColor)
                .thenAccept(
                        material -> {

                            ModelRenderable model = ShapeFactory.makeSphere(
                                    0.015f, Vector3.zero(), material
                            );

                            model.setShadowCaster(false);
                            model.setShadowReceiver(false);

                            Node node = new Node();
                            node.setParent(anchorNode);
                            node.setRenderable(model);

                            if (line.getFirstNode() == null) {
                                line.setFirstNode(node);
                            } else {
                                line.setSecondNode(node);
                            }
                        });
    }

    private void createAndDrawLineBetweenTwoPositions(Line line) {

        final Vector3 difference = Vector3.subtract(line.getFirstPos(), line.getSecondPos());
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

                            Node node = new Node();
                            node.setParent(line.getFirstAnchorNode());
                            node.setRenderable(model);
                            node.setWorldPosition(Vector3.add(
                                    line.getFirstPos(),
                                    line.getSecondPos()).scaled(.5f));

                            node.setWorldRotation(rotationFromAToB);
                            line.setLineNode(node);
                        }
                );
    }

    private void updateLine(Line line) {
        final Vector3 difference = Vector3.subtract(line.getFirstPos(), line.getSecondPos());
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

                            Node lineNode = line.getLineNode();
                            lineNode.setRenderable(model);
                            lineNode.setWorldPosition(Vector3.add(
                                    line.getFirstPos(),
                                    line.getSecondPos()).scaled(.5f)
                            );

                            lineNode.setWorldRotation(rotationFromAToB);

                            Node labelNode = line.getLabelNode();
                            ViewRenderable tvLabelRenderable = line.getTvLabelRenderable();

                            if (labelNode == null && tvLabelRenderable != null) {
                                //Log.i(TAG, "creating node for the label");
                                labelNode = new Node();
                                labelNode.setParent(lineNode);
                                labelNode.setRenderable(tvLabelRenderable);
                                labelNode.setWorldPosition(lineNode.getWorldPosition());
                                line.setLabelNode(labelNode);
                                updateDifferenceInMetersLabelState(line, difference.length());
                            } else if (tvLabelRenderable != null) {
                                //Log.i(TAG, "updating existing label");
                                updateDifferenceInMetersLabelState(line, difference.length());
                            }
                        }
                );
    }

    private void updateDifferenceInMetersLabelState(Line line, float difference) {
        Node labelNode = line.getLabelNode();
        Scene scene = labelNode.getScene();
        if (scene != null) {
            Vector3 cameraPosition = scene.getCamera().getWorldPosition();
            Vector3 direction = Vector3.subtract(cameraPosition, labelNode.getWorldPosition());
            Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
            labelNode.setWorldRotation(lookRotation);
        }
        labelNode.setLocalPosition(new Vector3(0, 0.04f, 0));
        line.getTvLabel().setText(String.format(Locale.US, "%.2f", difference) + "m");
    }
}
