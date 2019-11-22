package com.moroz.logsearcher.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileTree {

    private Node root;

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public void addFile(Path file) {
        Node currentNode = root;
        for(int i=0;i<file.getNameCount();++i) {
            String partName = file.getName(i).toString();
            Node node = currentNode.getChild(partName);
            if(node == null) {
                node = new Node(partName);
                currentNode.addChild(node);
            }

            currentNode = node;
        }
    }

    public void sort() {
        if(!root.isLeaf()) {
            sortChild(root);
        }
    }

    private void sortChild(Node parentNode) {
        parentNode.getChilds().sort((o1, o2) -> {
            if((o1.isLeaf() && o2.isLeaf()) || (!o1.isLeaf() && !o2.isLeaf())) {
                return o1.name.compareTo(o2.name);
            }

            return o1.isLeaf() ? 1 : -1;
        });

        for(Node node : parentNode.getChilds()) {
            if(!node.isLeaf()) {
                sortChild(node);
            }
        }
    }

    public static class Node {

        private String name;

        private List<Node> childs;

        public Node(String name) {
            this.name = name;
            childs = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public List<Node> getChilds() {
            return childs;
        }

        public Node getChild(String childName) {

            if (isLeaf()) {
                return null;
            }

            for(Node node : childs) {
                if(node.name.equals(childName)) {
                    return node;
                }
            }

            return null;
        }

        public void addChild(Node node) {
            childs.add(node);
        }

        public boolean isLeaf() {
            return childs.isEmpty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(name, node.name);
        }

        @Override
        public int hashCode() {

            return Objects.hash(name);
        }
    }

    @Override
    public String toString() {
        return "FileTree with root \""+root.getName()+"\"";
    }
}
