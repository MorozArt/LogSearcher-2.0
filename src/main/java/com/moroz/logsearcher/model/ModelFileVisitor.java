package com.moroz.logsearcher.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class ModelFileVisitor extends SimpleFileVisitor<Path> {

    private static final Logger log = LogManager.getLogger(ModelImpl.class.getName());

    private ExecutorService service;
    private String searchText;
    private String filesType;

    private FileTree fileTree;
    private Path root;
    private final Object addFileLock;
    private LinkedBlockingQueue<Future> queue;

    private final List<FoundFile> foundFiles;

    ModelFileVisitor(String searchText, String filesType, Path root) {
        log.trace("call constructor with searchText: "+searchText+" filesType: "+filesType+
        " root: "+root);

        service = Executors.newCachedThreadPool(runnable -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        this.searchText = searchText;
        this.filesType = filesType;

        fileTree = new FileTree();
        fileTree.setRoot(new FileTree.Node(root.getFileName().toString()));
        this.root = root;
        addFileLock = new Object();
        queue = new LinkedBlockingQueue<>();

        foundFiles = new ArrayList<>();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.getFileName().toString().endsWith(filesType)) {
            Runnable task = () -> {
                FoundFile foundFile = new SearchTextService().search(file, searchText);
                if (foundFile != null) {
                    log.trace("adding tree node for file: "+file);
                    addFileTreeNode(file);
                    synchronized (foundFiles) {
                        foundFiles.add(foundFile);
                    }
                }
            };
            queue.add(service.submit(task));
        }

        return FileVisitResult.CONTINUE;
    }

    private void addFileTreeNode(Path file) {
        Path subPath = root.relativize(file);

        synchronized (addFileLock) {
            fileTree.addFile(subPath);
        }
    }

    public List<FoundFile> getFoundFiles() {
        return foundFiles;
    }

    public FileTree getFileTree() {
        while (!queue.isEmpty()) {
            if(queue.peek().isDone()) {
                queue.poll();
            }
        }

        service.shutdown();
        fileTree.sort();
        return fileTree;
    }
}