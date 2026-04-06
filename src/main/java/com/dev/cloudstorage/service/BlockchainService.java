package com.dev.cloudstorage.service;

import com.dev.cloudstorage.model.BlockchainBlock;
import com.dev.cloudstorage.model.File;

public interface BlockchainService {
    BlockchainBlock createBlock(File file, String operation);
    boolean validateChain();
    void addFileOperationToBlockchain(File file, String operation, Long userId);
    boolean detectFileTampering(File file);
}