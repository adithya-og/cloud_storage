package com.dev.cloudstorage.service.impl;

import com.dev.cloudstorage.model.BlockchainBlock;
import com.dev.cloudstorage.model.File;
import com.dev.cloudstorage.model.TamperedFile;
import com.dev.cloudstorage.repository.BlockchainBlockRepository;
import com.dev.cloudstorage.repository.FileRepository;
import com.dev.cloudstorage.repository.TamperedFileRepository;
import com.dev.cloudstorage.service.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BlockchainServiceImpl implements BlockchainService {

    @Autowired
    private BlockchainBlockRepository blockchainBlockRepository;

    @Autowired
    private TamperedFileRepository tamperedFileRepository;

    @Autowired
    private FileRepository fileRepository;

    @Override
    public BlockchainBlock createBlock(File file, String operation) {
        // Get the latest block to link to
        Optional<BlockchainBlock> latestBlockOpt = blockchainBlockRepository.findTopByOrderByBlockIndexDesc();
        String previousHash = latestBlockOpt.map(BlockchainBlock::getHash).orElse("0");
        Long newBlockIndex = latestBlockOpt.map(block -> block.getBlockIndex() + 1).orElse(0L);

        // Create block data JSON
        String blockData = createBlockData(file, operation);

        // Create new block
        BlockchainBlock newBlock = new BlockchainBlock();
        newBlock.setBlockIndex(newBlockIndex);
        newBlock.setPreviousHash(previousHash);
        newBlock.setData(blockData);
        newBlock.setTimestamp(LocalDateTime.now());

        // Calculate hash for the block
        String hash = calculateHash(newBlock);
        newBlock.setHash(hash);

        // Save to database
        return blockchainBlockRepository.save(newBlock);
    }

    @Override
    public boolean validateChain() {
        List<BlockchainBlock> allBlocks = blockchainBlockRepository.findAll();

        // Check if chain is empty
        if (allBlocks.isEmpty()) {
            return true;
        }

        // Validate each block in the chain
        for (int i = 0; i < allBlocks.size(); i++) {
            BlockchainBlock currentBlock = allBlocks.get(i);

            // Validate the block's hash
            String calculatedHash = calculateHash(currentBlock);
            if (!calculatedHash.equals(currentBlock.getHash())) {
                return false;
            }

            // For non-genesis blocks, check the previous hash link
            if (i > 0) {
                BlockchainBlock previousBlock = allBlocks.get(i - 1);
                if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void addFileOperationToBlockchain(File file, String operation, Long userId) {
        createBlock(file, operation);
    }

    @Override
    public boolean detectFileTampering(File file) {
        try {
            // Calculate the current hash of the file on disk
            byte[] fileBytes = Files.readAllBytes(Paths.get(file.getFilePath()));
            String currentHash = calculateSHA256(fileBytes);

            // Compare with the stored hash
            boolean isTampered = !file.getFileHash().equals(currentHash);

            if (isTampered) {
                // Log tampering
                TamperedFile tamperedFile = new TamperedFile();
                tamperedFile.setFileId(file.getId());
                tamperedFile.setOriginalHash(file.getFileHash());
                tamperedFile.setCurrentHash(currentHash);
                tamperedFile.setStatus(TamperedFile.TamperStatus.DETECTED);
                tamperedFile.setNotes("File hash mismatch detected - potential tampering");

                tamperedFileRepository.save(tamperedFile);
            }

            return isTampered;
        } catch (IOException e) {
            // Handle the exception - file might not exist
            throw new RuntimeException("Error checking file integrity", e);
        }
    }

    private String createBlockData(File file, String operation) {
        return String.format(
            "{\"fileId\": %d, \"fileName\": \"%s\", \"operation\": \"%s\", \"fileHash\": \"%s\", \"timestamp\": \"%s\"}",
            file.getId(),
            file.getOriginalName(),
            operation,
            file.getFileHash(),
            LocalDateTime.now().toString()
        );
    }

    private String calculateHash(BlockchainBlock block) {
        String dataToHash = block.getBlockIndex() +
                           block.getPreviousHash() +
                           block.getData() +
                           block.getTimestamp().toString() +
                           block.getNonce();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dataToHash.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating hash", e);
        }
    }

    private String calculateSHA256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating SHA-256 hash", e);
        }
    }
}