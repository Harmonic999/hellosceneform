package com.google.ar.sceneform.samples.hellosceneform;


import android.widget.TextView;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class Line {

    private AnchorNode firstAnchorNode;
    private AnchorNode secondAnchorNode;

    private Node firstNode;
    private Node secondNode;

    private Node lineNode;
    private Node labelNode;

    private ViewRenderable tvLabelRenderable;
    private TextView tvLabel;

    public AnchorNode getFirstAnchorNode() {
        return firstAnchorNode;
    }

    public void setFirstAnchorNode(AnchorNode firstAnchorNode) {
        this.firstAnchorNode = firstAnchorNode;
    }

    public AnchorNode getSecondAnchorNode() {
        return secondAnchorNode;
    }

    public void setSecondAnchorNode(AnchorNode secondAnchorNode) {
        this.secondAnchorNode = secondAnchorNode;
    }

    public Node getFirstNode() {
        return firstNode;
    }

    public void setFirstNode(Node firstNode) {
        this.firstNode = firstNode;
    }

    public Node getSecondNode() {
        return secondNode;
    }

    public void setSecondNode(Node secondNode) {
        this.secondNode = secondNode;
    }

    public Node getLineNode() {
        return lineNode;
    }

    public void setLineNode(Node lineNode) {
        this.lineNode = lineNode;
    }

    public Node getLabelNode() {
        return labelNode;
    }

    public void setLabelNode(Node labelNode) {
        this.labelNode = labelNode;
    }

    public ViewRenderable getTvLabelRenderable() {
        return tvLabelRenderable;
    }

    public void setTvLabelRenderable(ViewRenderable tvLabelRenderable) {
        this.tvLabelRenderable = tvLabelRenderable;
    }

    public TextView getTvLabel() {
        return tvLabel;
    }

    public void setTvLabel(TextView tvLabel) {
        this.tvLabel = tvLabel;
    }

    public boolean anchorsInitialized() {
        return firstAnchorNode != null && secondAnchorNode != null;
    }

    public Vector3 getFirstPos() {
        return firstNode.getWorldPosition();
    }

    public Vector3 getSecondPos() {
        return secondNode.getWorldPosition();
    }
}
