package com.dev.cloudstorage.repository;

import com.dev.cloudstorage.model.BlockchainBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockchainBlockRepository extends JpaRepository<BlockchainBlock, Long> {
    Optional<BlockchainBlock> findByBlockIndex(Long blockIndex);
    Optional<BlockchainBlock> findTopByOrderByBlockIndexDesc();
}